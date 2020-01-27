package com.example.order.impl;

import com.example.order.api.OrderService;
import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

/**
 * Module to bind APIs and services.
 */
public final class OrderServiceModule extends AbstractModule implements ServiceGuiceSupport {

    /**
     * Configures a Binder via the exposed methods.
     */
    @Override
    protected void configure() {
        bindService(OrderService.class, OrderServiceImpl.class);
    }

}
