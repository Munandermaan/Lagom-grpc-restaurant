package com.example.order.impl;

import akka.NotUsed;
import com.example.menu.api.MenuItem;
import com.example.order.api.OrderService;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import example.myapp.restaurant.grpc.MenuReply;
import example.myapp.restaurant.grpc.RequestMenu;
import example.myapp.restaurant.grpc.RestaurantServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

/**
 * OrderServiceImpl is service implementation of interface {@link OrderService} for providing implementation of Services
 * Call methods related to the routes for Menu Service API.
 */
public class OrderServiceImpl implements OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final PersistentEntityRegistry persistentEntityRegistry;
    private final RestaurantServiceClient restaurantServiceClient;

    /**
     * Parametrized constructor to initialize class parameters using Google Guice.
     *
     * @param persistentEntityRegistry to send commands to to entity class.
     * @param readSide                 handles the management of lagom read-side.
     * @param restaurantServiceClient  client of restaurant service defined in proto file.
     */
    @Inject
    OrderServiceImpl(final PersistentEntityRegistry persistentEntityRegistry, final ReadSide readSide,
                     final RestaurantServiceClient restaurantServiceClient) {

        this.persistentEntityRegistry = persistentEntityRegistry;
        this.restaurantServiceClient = restaurantServiceClient;
        persistentEntityRegistry.register(OrderServiceEntity.class);

        readSide.register(OrderServiceEventProcessor.class);
    }

    @Override
    public ServiceCall<List<MenuItem>, String> placeOrder() {
        return request -> {
            LOGGER.info("Placing order with items" + request);
            final String entityId = UUID.randomUUID().toString();
            return persistentEntityRegistry.refFor(OrderServiceEntity.class, entityId)
                    .ask(OrderServiceCommand.PlaceOrderService.builder().id(entityId).menuItems(request).build());
        };
    }

    @Override
    public ServiceCall<NotUsed, String> getMenuItemsViaGrpc() {
        return req -> {
            LOGGER.info("Getting menu items via gRPC");
            return restaurantServiceClient
                    .getMenuViaGrpc(
                            RequestMenu.newBuilder()
                                    .setItem(NotUsed.getInstance().toString())
                                    .build()
                    ).thenApply(
                    MenuReply::getMessage
            );
        };
    }
}



