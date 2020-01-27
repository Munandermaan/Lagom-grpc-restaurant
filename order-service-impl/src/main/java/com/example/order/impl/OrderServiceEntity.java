package com.example.order.impl;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

final class OrderServiceEntity extends PersistentEntity<OrderServiceCommand, OrderServiceEvent, OrderServiceState> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceEntity.class);

    /**
     * Invokes different commands based on different routes.
     *
     * @param optionalOrderState of type Optional<MenuServiceState>.
     * @return Behavior
     */
    @Override
    public Behavior initialBehavior(Optional<OrderServiceState> optionalOrderState) {
        LOGGER.debug("Defining Initial behaviour for entity");
        BehaviorBuilder behaviorBuilder = newBehaviorBuilder(optionalOrderState.orElse(OrderServiceState.builder().build()));

        behaviorBuilder.setCommandHandler(OrderServiceCommand.PlaceOrderService.class, (cmd, ctx) -> {
            LOGGER.info("Persisting OrderServicePlaced event for order items");
            return ctx.thenPersist(OrderServiceEvent.OrderServicePlaced.builder().menuItems(cmd.getMenuItems()).id(cmd.getId()).build(), evt ->
                    ctx.reply(cmd.getId()));
        });

        return behaviorBuilder.build();
    }
}
