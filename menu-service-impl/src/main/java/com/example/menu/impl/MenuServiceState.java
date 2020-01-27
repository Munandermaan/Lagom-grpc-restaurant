package com.example.menu.impl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.serialization.CompressedJsonable;
import lombok.Builder;
import lombok.Value;

/**
 * MenuServiceState is a class for defining all the states that the MenuService entity supports.
 */
@Value
@Builder
@JsonDeserialize
public final class MenuServiceState implements CompressedJsonable {
}
