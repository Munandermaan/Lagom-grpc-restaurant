package com.example.menu.impl;

import akka.Done;
import com.example.menu.api.MenuItem;
import com.example.menu.api.MenuService;
import com.lightbend.lagom.javadsl.testkit.ServiceTest;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import play.inject.Bindings;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MenuServiceImplTest {

    private static final int TIMEOUT = 100; //in seconds
    private static MenuService service;
    private static volatile ServiceTest.TestServer testServer;
    private static final List<MenuItem> items = Collections.singletonList(MenuItem.builder().name("chholeBhature").build());

    @BeforeClass
    public static void beforeClass() {
        Config config = ConfigFactory.load();
        final ServiceTest.Setup setup = ServiceTest.defaultSetup()
                .configureBuilder(builder -> builder.loadConfig(config).overrides(
                        Bindings.bind(MenuService.class).to(MenuServiceImpl.class)));

        testServer = ServiceTest.startServer(setup.withCassandra(true));
        service = testServer.client(MenuService.class);
    }

    @AfterClass
    public static void afterClass() {
        if (testServer != null) {
            testServer.stop();
            testServer = null;
        }
    }

    @Test
    public void createMenuService() throws InterruptedException, ExecutionException, TimeoutException {
        final Done actualResponse = service.createMenuService().invoke(items).toCompletableFuture().get(TIMEOUT, TimeUnit.SECONDS);

        Assert.assertEquals(Done.getInstance(), actualResponse);
    }

    @Test
    public void deleteItemsFromMenuService() throws InterruptedException, ExecutionException, TimeoutException {
        final Done actualResponse = service.deleteItemsFromMenuService().invoke(items).toCompletableFuture().get(TIMEOUT, TimeUnit.SECONDS);

        Assert.assertEquals(Done.getInstance(), actualResponse);
    }
}