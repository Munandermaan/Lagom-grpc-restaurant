package com.example.order.impl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.serialization.CompressedJsonable;
import lombok.Builder;
import lombok.Value;

/**
 * OrderServiceState is a class for defining all the states that the OrderService entity supports.
 */
@Value
@Builder
@JsonDeserialize
final class OrderServiceState implements CompressedJsonable {
}
