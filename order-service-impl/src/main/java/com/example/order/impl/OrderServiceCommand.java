package com.example.order.impl;

import com.example.menu.api.MenuItem;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * OrderServiceCommand is an interface for defining all the commands that the MenuService entity supports.
 */
interface OrderServiceCommand extends Jsonable {

    /**
     * Command to place order.
     */
    @Value
    @Builder
    @JsonDeserialize
    final class PlaceOrderService implements OrderServiceCommand, CompressedJsonable, PersistentEntity.ReplyType<String> {
        private final String id;
        private final List<MenuItem> menuItems;
    }
}
