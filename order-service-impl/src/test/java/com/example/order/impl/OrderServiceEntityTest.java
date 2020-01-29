package com.example.order.impl;

import akka.actor.ActorSystem;
import com.example.menu.api.MenuItem;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;


public class OrderServiceEntityTest {
    private static final String entityID = UUID.randomUUID().toString();
    private static final List<MenuItem> items = Collections.singletonList(MenuItem.builder().name("chholeBhature").build());
    private static ActorSystem actorSystem;
    private static PersistentEntityTestDriver<OrderServiceCommand, OrderServiceEvent, OrderServiceState> driver;

    @BeforeClass
    public static void beforeClass() {
        actorSystem = ActorSystem.create();
        driver = new PersistentEntityTestDriver<>(actorSystem, new OrderServiceEntity(), entityID);
    }

    @AfterClass
    public static void afterClass() {
        if (actorSystem != null) {
            actorSystem.terminate();
        }
    }

    @Test
    public void testCreateMenuEntity() {
        final PersistentEntityTestDriver
                .Outcome<OrderServiceEvent, OrderServiceState> outcome = driver.run(OrderServiceCommand.PlaceOrderService
                .builder()
                .menuItems(items)
                .id(entityID)
                .build());
        Assert.assertNotNull(outcome);
    }

}