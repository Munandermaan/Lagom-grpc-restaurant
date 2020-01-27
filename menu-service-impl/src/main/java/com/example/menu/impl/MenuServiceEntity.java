package com.example.menu.impl;

import akka.Done;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

/**
 * MenuServiceEntity is a class for defining the operations related to Menu for command, state and event handling.
 */
public class MenuServiceEntity extends PersistentEntity<MenuServiceCommand, MenuServiceEvent, MenuServiceState> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuServiceEntity.class);

    /**
     * Invokes different commands based on different routes.
     *
     * @param optionalMenuState of type Optional<MenuServiceState>.
     * @return Behavior
     */
    @Override
    public Behavior initialBehavior(final Optional<MenuServiceState> optionalMenuState) {

        LOGGER.debug("Defining Initial behaviour for entity");
        BehaviorBuilder behaviorBuilder = newBehaviorBuilder(optionalMenuState.orElse(MenuServiceState.builder().build()));

        behaviorBuilder.setCommandHandler(MenuServiceCommand.CreateMenu.class, (cmd, ctx) -> {
            LOGGER.info("Persisting MenuServiceCreated event for items" + cmd.getMenuItems());
            return ctx.thenPersist(
                    MenuServiceEvent.MenuServiceCreated.builder().menuItems(cmd.getMenuItems()).build(), evt ->
                            ctx.reply(Done.getInstance()));
        });

        behaviorBuilder.setCommandHandler(MenuServiceCommand.DeleteItemsFromMenu.class, (cmd, ctx) -> {
            LOGGER.info("Persisting ItemsDeletedFromMenuService event for items" + cmd.getMenuItems());
            return ctx.thenPersist(MenuServiceEvent.ItemsDeletedFromMenuService.builder().menuItems(cmd.getMenuItems()).build(), evt ->
                    ctx.reply(Done.getInstance()));
        });

        return behaviorBuilder.build();
    }
}
