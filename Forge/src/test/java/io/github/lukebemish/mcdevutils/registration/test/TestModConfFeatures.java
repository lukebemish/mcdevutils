package io.github.lukebemish.mcdevutils.registration.test;

import io.github.lukebemish.mcdevutils.registration.api.Registrar;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

@Registrar(type = ConfiguredFeature.class, mod_id = "test")
public class TestModConfFeatures {
    @Registrar.Target
    public static TestModConfFeatures MOD_CONFIGURED_FEATURES;

    @Registrar.Named("effect")
    public ConfiguredFeature<NoneFeatureConfiguration, Feature<NoneFeatureConfiguration>> effect1 = new ConfiguredFeature<>(TestModFeatures.MOD_FEATURES.effect1, FeatureConfiguration.NONE);
}

