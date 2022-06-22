package io.github.lukebemish.mcdevutils.registration.test;

import net.minecraft.core.Registry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

public class TestModInitializer {
    public TestModInitializer() {
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        modbus.register(new TestModItemsRegistrar(ForgeRegistries.Keys.ITEMS));
        modbus.register(new TestModFeaturesRegistrar(ForgeRegistries.Keys.FEATURES));
        modbus.register(new TestModConfFeaturesRegistrar(Registry.CONFIGURED_FEATURE_REGISTRY));
    }
}
