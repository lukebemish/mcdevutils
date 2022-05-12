package io.github.lukebemish.mcdevutils.registration.test;

import net.minecraft.data.BuiltinRegistries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class TestModInitializer {
    public TestModInitializer() {
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        modbus.register(TestModItemsRegistrar.class);
        modbus.register(TestModFeaturesRegistrar.class);
        modbus.register(new TestModConfFeaturesRegistrar(BuiltinRegistries.CONFIGURED_FEATURE));
    }
}
