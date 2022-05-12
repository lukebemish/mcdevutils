package io.github.lukebemish.mcdevutils.registration.test;

import net.fabricmc.api.ModInitializer;

public class TestModInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        TestModItemsRegistrar.init();
        TestModFeaturesRegistrar.init();
    }
}
