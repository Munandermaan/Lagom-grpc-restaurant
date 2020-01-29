package com.example.order.impl;

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
 * OrderServiceEventProcessor is a class which implements code for Read side processor to handle OrderService events.
 */
public final class OrderServiceEventProcessor extends ReadSideProcessor<OrderServiceEvent> {
    private static final Logger LOGGER = Logger.getLogger(OrderServiceEventProcessor.class);

    private final CassandraSession cassandraSession;
    private final CassandraReadSide cassandraReadSide;
    private final ObjectMapper objectMapper;
    private PreparedStatement insertOrder;

    /**
     * Parameterized constructor for Order Service Event Processor.
     *
     * @param cassandraSession  data access object for cassandra database.
     * @param cassandraReadSide cassandraReadSide Cassandra Read side processor for database.
     * @param objectMapper      Jackson Object Mapper for json parsing.
     */
    @Inject
    private OrderServiceEventProcessor(final CassandraSession cassandraSession,
                                       final CassandraReadSide cassandraReadSide,
                                       final ObjectMapper objectMapper) {
        this.cassandraSession = cassandraSession;
        this.cassandraReadSide = cassandraReadSide;
        this.objectMapper = objectMapper;
    }

    @Override
    public ReadSideHandler<OrderServiceEvent> buildHandler() {
        LOGGER.debug("Creating BuildHandler method in event read side");
        return cassandraReadSide.<OrderServiceEvent>builder("order_offset")
                .setGlobalPrepare(this::createTable)
                .setPrepare(tag -> prepareInsertItem())
                .setEventHandler(OrderServiceEvent.OrderServicePlaced.class, evt -> insertOrder(evt))
                .build();
    }

    @Override
    public PSequence<AggregateEventTag<OrderServiceEvent>> aggregateTags() {
        return OrderServiceEvent.TAG.allTags();
    }

    /**
     * Create Schema in Database.
     *
     * @return Done instance wrapped in Completion Stage.
     */
    private CompletionStage<Done> createTable() {
        return cassandraSession.executeCreateTable(
                "CREATE TABLE IF NOT EXISTS orders ("
                        + "id text PRIMARY KEY,"
                        + "items text"
                        + ")");
    }

    /**
     * Prepared query to insert values into the table already created.
     *
     * @return Done instance wrapped in Completion Stage.
     */
    private CompletionStage<Done> prepareInsertItem() {
        return cassandraSession.prepare("INSERT INTO orders (id, items) VALUES (?, ?)")
                .thenApply(preparedStatement -> {
                    insertOrder = preparedStatement;
                    return Done.getInstance();
                });
    }

    /**
     * Insert the items into the table being created.
     *
     * @param orderPlaced contains items to be placed.
     * @return List of  prepared statements with values bound to the bind variables.
     */
    private CompletionStage<List<BoundStatement>> insertOrder(OrderServiceEvent.OrderServicePlaced orderPlaced) {
        LOGGER.debug("Placing order for items" + orderPlaced.getMenuItems());
        try {
            return CassandraReadSide.completedStatement(insertOrder.bind(orderPlaced.getId(),
                    objectMapper.writeValueAsString(orderPlaced.getMenuItems())));
        } catch (Exception ex) {
            LOGGER.error("Caught exception while placing order: " + orderPlaced.getId(), ex);
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
    }
}
