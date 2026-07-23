package com.chu.sih.utils;

import com.chu.sih.entity.Role;

import java.util.Arrays;
import java.util.Map;

public final class RoleCatalog {

    private RoleCatalog() {
    }

    public static Map<String, String> labels() {
        return Arrays.stream(Role.values())
                .collect(java.util.stream.Collectors.toMap(Role::name, Role::getLabel));
    }
}
