package io.github.lukebemish.mcdevutils.registration.test;

import net.minecraft.core.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class TestModInitializer implements ModInitializer {
    @Override
    public void onInitialize(ModContainer container) {
        TestModItemsRegistrar.init(Registry.ITEM);
        TestModItems items = TestModItems.MOD_ITEMS;
    }
}
