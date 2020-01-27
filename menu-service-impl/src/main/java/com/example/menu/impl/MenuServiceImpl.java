package com.example.menu.impl;

import akka.Done;
import akka.NotUsed;
import com.example.menu.api.MenuItem;
import com.example.menu.api.MenuService;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * MenuServiceImpl is service implementation of interface {@link MenuService} for providing implementation of Services
 * Call methods related to the routes for Menu Service API.
 */
public class MenuServiceImpl implements MenuService {

    private final PersistentEntityRegistry persistentEntityRegistry;
    private final CassandraSession cassandraSession;

    /**
     * Parametrized constructor to initialize class parameters using Google Guice.
     *
     * @param persistentEntityRegistry to send commands to to entity class.
     * @param cassandraSession         data access object for cassandra database.
     * @param readSide                 handles the management of lagom read-side.
     */
    @Inject
    MenuServiceImpl(final PersistentEntityRegistry persistentEntityRegistry,
                    final CassandraSession cassandraSession,
                    final ReadSide readSide) {
        this.persistentEntityRegistry = persistentEntityRegistry;
        persistentEntityRegistry.register(MenuServiceEntity.class);

        this.cassandraSession = cassandraSession;

        readSide.register(MenuServiceEventProcessor.class);
    }

    /**
     * To get the menu items.
     *
     * @return list of items in the database.
     */
    public ServiceCall<NotUsed, List<String>> getMenuService() {
        return request ->
                cassandraSession.selectAll("select * from menu")
                        .thenApply(rows ->
                                rows.stream().map(row -> row.getString("items")).collect(Collectors.toList()));
    }

    @Override
    public ServiceCall<List<MenuItem>, Done> createMenuService() {
        return request -> {
            final String entityId = UUID.randomUUID().toString();
            return persistentEntityRegistry.refFor(MenuServiceEntity.class, entityId)
                    .ask(MenuServiceCommand.CreateMenu.builder().menuItems(request).build());
        };
    }

    @Override
    public ServiceCall<List<MenuItem>, Done> deleteItemsFromMenuService() {
        return request -> {
            final String entityId = UUID.randomUUID().toString();
            return persistentEntityRegistry.refFor(MenuServiceEntity.class, entityId)
                    .ask(MenuServiceCommand.DeleteItemsFromMenu.builder().menuItems((request)).build());
        };
    }
}
