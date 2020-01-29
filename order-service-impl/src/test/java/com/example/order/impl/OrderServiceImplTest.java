package com.example.order.impl;

import akka.Done;
import akka.grpc.javadsl.AkkaGrpcClient;
import com.example.menu.api.MenuItem;
import com.example.menu.api.MenuService;
import com.example.order.api.OrderService;
import com.lightbend.lagom.javadsl.testkit.ServiceTest;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import example.myapp.restaurant.grpc.AkkaGrpcClientModule;
import example.myapp.restaurant.grpc.MenuReply;
import example.myapp.restaurant.grpc.RequestMenu;
import example.myapp.restaurant.grpc.RestaurantServiceClient;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import play.inject.Bindings;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class OrderServiceImplTest {

    private static final int TIMEOUT = 100; //in seconds
    private static OrderService service;
    private static volatile ServiceTest.TestServer testServer;
    private static List<MenuItem> items = Collections.singletonList(MenuItem.builder().name("chholeBhature").build());

    @BeforeClass
    public static void setUp() {
        final ServiceTest.Setup setup = ServiceTest.defaultSetup()
                .withCluster(false)
                .withSsl(false)
                .configureBuilder(builder -> builder
                        .disable(AkkaGrpcClientModule.class)
                        .overrides(Bindings.bind(RestaurantServiceClient.class).to(RestaurantServiceClientStub.class))
                        .overrides(Bindings.bind(OrderService.class).to(OrderServiceImpl.class)));
        testServer = ServiceTest.startServer(setup.withCassandra(true));
        service = testServer.client(OrderService.class);
    }

    @Test
    public void placeOrder() throws InterruptedException, ExecutionException, TimeoutException {

        final String actualResponse = service.placeOrder().invoke(items).toCompletableFuture().get(TIMEOUT, TimeUnit.SECONDS);
        Assert.assertNotNull(actualResponse);
    }

    @Test
    public void getMenuItemsViaGrpcTest() throws ExecutionException, InterruptedException, TimeoutException {

        final String actualResponse = service.getMenuItemsViaGrpc().invoke().toCompletableFuture().get(TIMEOUT, TimeUnit.SECONDS);
        Assert.assertEquals("successfull", actualResponse);
    }

    public interface StubbedAkkaGrpcClient extends AkkaGrpcClient {
        @Override
        default CompletionStage<Done> close() {
            return null;
        }

        @Override
        default CompletionStage<Done> closed() {
            return null;
        }
    }

    public static class RestaurantServiceClientStub extends RestaurantServiceClient implements StubbedAkkaGrpcClient {

        @Override
        public CompletionStage<MenuReply> getMenuViaGrpc(RequestMenu in) {
           final  MenuReply reply =
                    MenuReply
                            .newBuilder()
                            .setMessage("successfull")
                            .build();
            return CompletableFuture.completedFuture(reply);
        }

    }
}