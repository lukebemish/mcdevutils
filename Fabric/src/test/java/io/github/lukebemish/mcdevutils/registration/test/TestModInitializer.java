package io.github.lukebemish.mcdevutils.registration.test;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;

public class TestModInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        TestModItemsRegistrar.init(Registry.ITEM);
        TestModItems items = TestModItems.MOD_ITEMS;
    }
}
