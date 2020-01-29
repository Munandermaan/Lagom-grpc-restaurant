package com.example.menu.impl;

import akka.Done;
import akka.actor.ActorSystem;
import com.example.menu.api.MenuItem;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver;
import org.hamcrest.CoreMatchers;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class MenuServiceEntityTest {

    private static ActorSystem actorSystem;
    private static String entityID = "0x1234";
    private static List<MenuItem> items = Collections.singletonList(MenuItem.builder().name("chholeBhature").build());
    private static PersistentEntityTestDriver<MenuServiceCommand, MenuServiceEvent, MenuServiceState> driver;

    @BeforeClass
    public static void beforeClass() {
        actorSystem = ActorSystem.create();
        driver = new PersistentEntityTestDriver<>(actorSystem, new MenuServiceEntity(), entityID);
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
                .Outcome<MenuServiceEvent, MenuServiceState> outcome = driver.run(MenuServiceCommand.CreateMenu
                .builder()
                .menuItems(items)
                .build());

        Assert.assertNotNull(outcome);
        Assert.assertThat(outcome.getReplies().get(0), CoreMatchers.is(CoreMatchers.equalTo(Done.getInstance())));
    }

    @Test
    public void testDeleteMenuEntity() {
        final PersistentEntityTestDriver
                .Outcome<MenuServiceEvent, MenuServiceState> outcome = driver.run(MenuServiceCommand.DeleteItemsFromMenu
                .builder()
                .menuItems(items)
                .build());

        Assert.assertNotNull(outcome);
        Assert.assertThat(outcome.getReplies().get(0), CoreMatchers.is(CoreMatchers.equalTo(Done.getInstance())));
    }
}