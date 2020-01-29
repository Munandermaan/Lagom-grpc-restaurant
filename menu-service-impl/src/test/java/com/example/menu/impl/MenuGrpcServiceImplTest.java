package com.example.menu.impl;

import com.lightbend.lagom.javadsl.testkit.grpc.AkkaGrpcClientHelpers;
import example.myapp.restaurant.grpc.MenuReply;
import example.myapp.restaurant.grpc.RequestMenu;
import example.myapp.restaurant.grpc.RestaurantServiceClient;
import org.junit.Test;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.withServer;
import static org.junit.Assert.assertNotNull;

public class MenuGrpcServiceImplTest {

    @Test
    public void getMenuViaGrpcTest() {
        withServer(defaultSetup().withSsl().withCassandra(true), server -> {
            AkkaGrpcClientHelpers
                    .withGrpcClient(
                            server,
                            RestaurantServiceClient::create,
                            serviceClient -> {
                                final RequestMenu request =
                                        RequestMenu.newBuilder().setItem("Rasmalai").build();
                                final MenuReply reply = serviceClient.getMenuViaGrpc(request)
                                        .toCompletableFuture()
                                        .get();

                                assertNotNull(reply.getMessage());
                            });
        });
    }
}