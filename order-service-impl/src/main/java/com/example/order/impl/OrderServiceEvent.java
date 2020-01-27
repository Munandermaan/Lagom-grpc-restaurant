package com.example.order.impl;

import com.example.menu.api.MenuItem;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * OrderServiceEvent is an interface for defining all the events that the OrderServiceEntity entity supports.
 */
interface OrderServiceEvent extends Jsonable, AggregateEvent<OrderServiceEvent> {
    AggregateEventShards<OrderServiceEvent> TAG = AggregateEventTag.sharded(OrderServiceEvent.class, 2);

    @Override
    default AggregateEventTagger<OrderServiceEvent> aggregateTag() {
        return TAG;
    }

    /**
     * Event generated when order is placed.
     */
    @Value
    @Builder
    @JsonDeserialize
    final class OrderServicePlaced implements OrderServiceEvent {
        private final String id;
        private final List<MenuItem> menuItems;
    }
}
