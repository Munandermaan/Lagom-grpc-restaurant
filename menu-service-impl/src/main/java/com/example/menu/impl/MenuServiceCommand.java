package com.example.menu.impl;

import akka.Done;
import com.example.menu.api.MenuItem;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * MenuServiceCommand is an interface for defining all the commands that the MenuService entity supports.
 */
interface MenuServiceCommand extends Jsonable {

    /**
     * Command to create menu.
     */
    @Value
    @Builder
    @JsonDeserialize
    final class CreateMenu implements MenuServiceCommand, CompressedJsonable, PersistentEntity.ReplyType<Done> {
        private final List<MenuItem> menuItems;
    }

    /**
     * Command to delete items from the menu.
     */
    @Value
    @Builder
    @JsonDeserialize
    final class DeleteItemsFromMenu implements MenuServiceCommand, CompressedJsonable, PersistentEntity.ReplyType<Done> {
        private final List<MenuItem> menuItems;
    }
}
