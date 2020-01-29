package com.example.menu.impl;

import akka.Done;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.apache.log4j.Logger;
import org.pcollections.PSequence;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * MenuServiceEventProcessor is a class which implements code for Read side processor to handle MenuService events.
 */
public final class MenuServiceEventProcessor extends ReadSideProcessor<MenuServiceEvent> {
    private static final Logger LOGGER = Logger.getLogger(MenuServiceEventProcessor.class);
    private final CassandraSession cassandraSession;
    private final CassandraReadSide cassandraReadSide;
    private final ObjectMapper objectMapper;

    private PreparedStatement insertItemsInMenu;
    private PreparedStatement deleteItemsInMenu;

    /**
     * Parameterized constructor for Menu Service Event Processor.
     *
     * @param cassandraSession  data access object for cassandra database.
     * @param cassandraReadSide Cassandra Read side processor for database.
     * @param objectMapper      Jackson Object Mapper for json parsing.
     */
    @Inject
    private MenuServiceEventProcessor(final CassandraSession cassandraSession, final CassandraReadSide cassandraReadSide,
                                      final ObjectMapper objectMapper) {
        this.cassandraSession = cassandraSession;
        this.cassandraReadSide = cassandraReadSide;
        this.objectMapper = objectMapper;
    }

    @Override
    public ReadSideHandler<MenuServiceEvent> buildHandler() {
        LOGGER.debug("Creating BuildHandler method in event read side");
        return cassandraReadSide.<MenuServiceEvent>builder("menu_offset")
                .setGlobalPrepare(this::createTable)
                .setPrepare(tag -> prepareInsertItem()
                        .thenCompose(done -> prepareDeleteItem()))
                .setEventHandler(MenuServiceEvent.MenuServiceCreated.class, evt -> insertItemsInMenu(evt))
                .setEventHandler(MenuServiceEvent.ItemsDeletedFromMenuService.class, evt -> deleteItemsFromMenu(evt))
                .build();
    }

    @Override
    public PSequence<AggregateEventTag<MenuServiceEvent>> aggregateTags() {
        return MenuServiceEvent.TAG.allTags();
    }

    /**
     * Create Schema in Database.
     *
     * @return Done instance wrapped in Completion Stage.
     */
    private CompletionStage<Done> createTable() {
        return cassandraSession.executeCreateTable(
                "CREATE TABLE IF NOT EXISTS menu ("
                        + "items text PRIMARY KEY"
                        + ")");
    }

    /**
     * Prepared query to insert values into the table already created.
     *
     * @return Done instance wrapped in Completion Stage.
     */
    private CompletionStage<Done> prepareInsertItem() {
        return cassandraSession.prepare("INSERT INTO menu (items) VALUES (?)")
                .thenApply(preparedStatement -> {
                    insertItemsInMenu = preparedStatement;
                    return Done.getInstance();
                });
    }

    /**
     * Prepared query to delete values from the table being created.
     *
     * @return Done instance wrapped in Completion Stage
     */
    private CompletionStage<Done> prepareDeleteItem() {
        return cassandraSession.prepare("DELETE FROM menu WHERE items=?")
                .thenApply(preparedStatement -> {
                    deleteItemsInMenu = preparedStatement;
                    return Done.getInstance();
                });
    }

    /**
     * Insert the items into the table being created.
     *
     * @param menuCreated contains values to be inserted.
     * @return List of  prepared statements with values bound to the bind variables.
     */
    private CompletionStage<List<BoundStatement>> insertItemsInMenu(MenuServiceEvent.MenuServiceCreated menuCreated) {
        LOGGER.info("Inserting items in the cassandra table"+ menuCreated.getMenuItems());
        try {
            return CassandraReadSide.completedStatement(insertItemsInMenu.bind(
                    objectMapper.writeValueAsString(menuCreated.getMenuItems())));
        } catch (Exception ex) {
            LOGGER.error("Caught exception while creating menu: " + menuCreated.getMenuItems(), ex);
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
    }

    /**
     * Insert the items into the table being created.
     *
     * @param itemsDeleted contains values to be deleted.
     * @return List of  prepared statements with values bound to the bind variables.
     */
    private CompletionStage<List<BoundStatement>> deleteItemsFromMenu(MenuServiceEvent.ItemsDeletedFromMenuService itemsDeleted) {
        LOGGER.info("Deleting items from the cassandra table" + itemsDeleted.getMenuItems());
        try {
            return CassandraReadSide.completedStatement(deleteItemsInMenu.bind(
                    objectMapper.writeValueAsString(itemsDeleted.getMenuItems())));
        } catch (Exception ex) {
            LOGGER.error("Caught exception while deleting items: " + itemsDeleted.getMenuItems(), ex);
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
    }
}
