package io.github.lukebemish.mcdevutils.registration.test;

import io.github.lukebemish.mcdevutils.registration.api.Registrar;
import net.minecraft.world.level.levelgen.feature.BasaltPillarFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

@Registrar(type = Feature.class, mod_id = "test")
public class TestModFeatures {
    @Registrar.Target
    public static TestModFeatures MOD_FEATURES;

    @Registrar.Named("feature")
    public Feature<NoneFeatureConfiguration> feature = new BasaltPillarFeature(NoneFeatureConfiguration.CODEC);
}
