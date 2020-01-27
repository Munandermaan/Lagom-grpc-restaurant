package com.example.menu.impl;

import com.example.menu.api.MenuItem;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.util.List;

/**
 * MenuServiceEvent is an interface for defining all the events that the MenuServiceEntity entity supports.
 */
interface MenuServiceEvent extends Jsonable, AggregateEvent<MenuServiceEvent> {
    AggregateEventShards<MenuServiceEvent> TAG = AggregateEventTag.sharded(MenuServiceEvent.class, 2);

    @Override
    default AggregateEventTagger<MenuServiceEvent> aggregateTag() {
        return TAG;
    }

    /**
     * Event generated when menu items being  created.
     */
    @Value
    @Builder
    @JsonDeserialize
    @Getter
    final class MenuServiceCreated implements MenuServiceEvent {
        private final List<MenuItem> menuItems;
    }

    /**
     * Event generated when menu items being deleted.
     */
    @Value
    @Builder
    @JsonDeserialize
    @Getter
    final class ItemsDeletedFromMenuService implements MenuServiceEvent {
        private final List<MenuItem> menuItems;
    }
}
