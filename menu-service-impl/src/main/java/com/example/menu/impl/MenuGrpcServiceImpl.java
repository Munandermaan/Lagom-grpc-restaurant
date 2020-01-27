package com.example.menu.impl;

import akka.actor.ActorSystem;
import akka.stream.Materializer;
import example.myapp.restaurant.grpc.AbstractRestaurantServiceRouter;
import example.myapp.restaurant.grpc.MenuReply;
import example.myapp.restaurant.grpc.RequestMenu;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

/**
 * HelloGrpcServiceImpl is a singleton class to build the reply using Grpc.
 */
@Singleton
public final class MenuGrpcServiceImpl extends AbstractRestaurantServiceRouter {
    private static final Logger LOGGER = Logger.getLogger(MenuGrpcServiceImpl.class.getName());
    private final MenuServiceImpl menuServiceImpl;

    /**
     * Parametrized constructor to initialize class parameters using Google Guice.
     *
     * @param sys actor of class Actor system.
     * @param mat materializer to set medium.
     */
    @Inject
    public MenuGrpcServiceImpl(final ActorSystem sys, final Materializer mat, final MenuServiceImpl menuServiceImpl) {
        super(mat, sys);
        this.menuServiceImpl = menuServiceImpl;
    }

    @Override
    public CompletionStage<MenuReply> getMenuViaGrpc(RequestMenu requestMenu) {
        LOGGER.info("Getting menu items via gRPC");
        return menuServiceImpl.getMenuService().invoke().thenApply(result ->
                MenuReply
                        .newBuilder()
                        .setMessage(result.toString())
                        .build());
    }
}
