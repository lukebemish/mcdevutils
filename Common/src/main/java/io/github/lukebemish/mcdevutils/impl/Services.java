package io.github.lukebemish.mcdevutils.impl;

import java.util.ServiceLoader;

public class Services {
    public static <T> T load(ClassLoader classLoader, Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz, classLoader)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        return loadedService;
    }
}
