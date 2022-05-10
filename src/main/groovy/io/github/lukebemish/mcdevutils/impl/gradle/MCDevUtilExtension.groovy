package io.github.lukebemish.mcdevutils.impl.gradle

import org.gradle.api.provider.Property

abstract class MCDevUtilExtension extends GroovyObjectSupport {
    public static final String NAME = "mcDevUtil";
    abstract Property<String> getPlatform()

    MCDevUtilExtension() {
        this.platform.convention(Platform.COMMON.toString())
    }

    Platform platform() {
        switch (platform.get().toLowerCase(Locale.ROOT)) {
            case "fabric" : return Platform.FABRIC;
            case "forge" : return Platform.FORGE;
            case "quilt" : return Platform.QUILT;
            case "common" : return Platform.COMMON;
            default : throw new IllegalArgumentException("Unknown project platform " + platform);
        };
    }

    enum Platform {
        FABRIC {
            @Override
            String toString() {
                return "fabric";
            }
        },
        FORGE {
            @Override
            String toString() {
                return "forge";
            }
        },
        QUILT {
            @Override
            String toString() {
                return "quilt";
            }
        },
        COMMON {
            @Override
            String toString() {
                return "common";
            }
        }
    }
}
