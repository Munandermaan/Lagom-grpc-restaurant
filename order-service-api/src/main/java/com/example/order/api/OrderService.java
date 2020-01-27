package com.example.order.api;

import akka.NotUsed;
import com.example.menu.api.MenuItem;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

import java.util.List;

public interface OrderService extends Service {

    /**
     * Placed the order.
     *
     * @return order id.
     */
    ServiceCall<List<MenuItem>, String> placeOrder();

    /**
     * Get the menu items from yhe menu service via gRPC.
     *
     * @return menu items.
     */
    ServiceCall<NotUsed, String> getMenuItemsViaGrpc();

    @Override
    default Descriptor descriptor() {
        return Service.named("orderService").withCalls(
                Service.restCall(Method.POST, "/order", this::placeOrder),
                Service.restCall(Method.GET, "/getMenu", this::getMenuItemsViaGrpc)
        ).withAutoAcl(true);
    }
}
