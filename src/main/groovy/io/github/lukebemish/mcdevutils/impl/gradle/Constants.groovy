package io.github.lukebemish.mcdevutils.impl.gradle;

import io.github.lukebemish.mcdevutils.sided.api.CheckSide;

class Constants {
    static final Name ONLYIN_PATH = new Name("net.minecraftforge.api.distmarker.OnlyIn")
    static final Name ENVIRONMENT_PATH = new Name("net.fabricmc.api.Environment")
    static final Name CHECKSIDE_PATH = new Name(CheckSide.class.getCanonicalName())


    public static final String CACHE_FOLDER = "mcdevutil"
    public static final String VERSION = "0.1.0"
}
