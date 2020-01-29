import akka.grpc.gen.javadsl.play.{ PlayJavaClientCodeGenerator, PlayJavaServerCodeGenerator }
import sbt.Def
import sbt.Keys.dependencyOverrides

organization in ThisBuild := "com.example"
version in ThisBuild := "1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.12.8"

lagomServiceEnableSsl in ThisBuild := true
val `menu-impl-HTTPS-port` = 11000

val lagomGrpcTestkit = "com.lightbend.play" %% "lagom-javadsl-grpc-testkit" % "0.6.0"

val lombokDependency = "org.projectlombok" % "lombok" % "1.16.16" % "provided"

lazy val `lagom-grpc-restaurant` = (project in file("."))
  .aggregate(`menu-service-api`, `menu-service-impl`, `order-service-api`, `order-service-impl`)

lazy val `menu-service-api` = (project in file("menu-service-api"))
  .settings(common)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslApi,
      lombokDependency
    )
  )

lazy val `menu-service-impl` = (project in file("menu-service-impl"))
  .enablePlugins(LagomJava)
  .enablePlugins(AkkaGrpcPlugin) // enables source generation for gRPC
  .enablePlugins(PlayAkkaHttp2Support) // enables serving HTTP/2 and gRPC
  .settings(common)
  .settings(
  akkaGrpcGeneratedLanguages := Seq(AkkaGrpc.Java),
  akkaGrpcGeneratedSources :=
    Seq(
      AkkaGrpc.Server,
      AkkaGrpc.Client
    ),
  akkaGrpcExtraGenerators in Compile += PlayJavaServerCodeGenerator,

  // WORKAROUND: Lagom still can't register a service under the gRPC name so we hard-code
  // the port and the use the value to add the entry on the Service Registry
  lagomServiceHttpsPort := `menu-impl-HTTPS-port`,

  libraryDependencies ++= Seq(
    lagomJavadslTestKit,
    lagomLogback,
    lagomGrpcTestkit,
    lombokDependency,
    lagomJavadslPersistenceCassandra
  )
).settings(lagomForkedTestSettings: _*)
  .dependsOn(`menu-service-api`)

lazy val `order-service-api` = (project in file("order-service-api"))
  .settings(common)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslApi,
      lombokDependency
    )
  )
  .dependsOn(`menu-service-api`)

lazy val `order-service-impl` = (project in file("order-service-impl"))
  .enablePlugins(LagomJava)
  .enablePlugins(AkkaGrpcPlugin) // enables source generation for gRPC
  .settings(common)
  .settings(
  akkaGrpcGeneratedLanguages := Seq(AkkaGrpc.Java),
  akkaGrpcExtraGenerators += PlayJavaClientCodeGenerator,
).settings(
  libraryDependencies ++= Seq(
    lagomJavadslTestKit,
    lagomLogback,
    lagomJavadslPersistenceCassandra,
    lombokDependency
  )
)
  .dependsOn(`order-service-api`)

lazy val docs = (project in file("docs")).enablePlugins(ParadoxPlugin)

lagomCassandraEnabled in ThisBuild := true
lagomKafkaEnabled in ThisBuild := false

// This adds an entry on the LagomDevMode Service Registry. With this information on
// the Service Registry a client using Service Discovery to Lookup("restaurant.RestaurantService")
// will get "https://localhost:11000" and then be able to send a request.
// See declaration and usages of `hello-impl-HTTPS-port`.
lagomUnmanagedServices in ThisBuild := Map("restaurant.RestaurantService" -> s"https://localhost:${`menu-impl-HTTPS-port`}")

def common = Seq(
  javacOptions in Compile ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-parameters", "-Werror")
)
