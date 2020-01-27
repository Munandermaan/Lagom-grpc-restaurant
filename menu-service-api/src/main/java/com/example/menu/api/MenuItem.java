package com.example.menu.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Value
@Builder
@JsonDeserialize
@Getter
public final class MenuItem {
    private final String name;
}
