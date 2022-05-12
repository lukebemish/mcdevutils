package io.github.lukebemish.mcdevutils.registration.test;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class TestModInitializer implements ModInitializer {
    @Override
    public void onInitialize(ModContainer container) {
        TestModItemsRegistrar.init();
        TestModFeaturesRegistrar.init();
    }
}
