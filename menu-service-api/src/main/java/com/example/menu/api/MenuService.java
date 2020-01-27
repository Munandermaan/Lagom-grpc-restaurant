package com.example.menu.api;

import akka.Done;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

import java.util.List;

/**
 * MenuService is an interface extending {@link Service} defining service descriptor to serve the route calls
 * for MenuService.
 */
public interface MenuService extends Service {

    /**
     *  Creates or add items in the Menu.
     *
     * @return Done instance of akka.
     */
    ServiceCall<List<MenuItem>, Done> createMenuService();

    /**
     * Delete items from menu Service.
     *
     * @return Done instance of akka.
     */
    ServiceCall<List<MenuItem>, Done> deleteItemsFromMenuService();

    @Override
    default Descriptor descriptor() {

        return Service.named("menuService").withCalls(
                Service.restCall(Method.POST, "/menu/createMenu", this::createMenuService),
                Service.restCall(Method.DELETE, "/menu/item", this::deleteItemsFromMenuService)
        ).withAutoAcl(true);
    }
}
