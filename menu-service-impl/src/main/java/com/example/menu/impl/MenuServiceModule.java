package com.example.menu.impl;

import com.example.menu.api.MenuService;
import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

/**
 * Module to bind APIs and services.
 */
public final class MenuServiceModule extends AbstractModule implements ServiceGuiceSupport {

    /**
     * Configures a Binder via the exposed methods.
     */
    @Override
    protected void configure() {
        bindService(
                MenuService.class, MenuServiceImpl.class,
                additionalRouter(MenuGrpcServiceImpl.class)
        );
    }
}
