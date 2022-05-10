package io.github.lukebemish.mcdevutils.sided.impl.discovery;

public class SideStringFormatter {
    public static final SideStringFormatter FORMATTER = new SideStringFormatter();

    public Platform platform;

    private SideStringFormatter() {
        Platform platform1 = null;
        try {
            Class.forName("net.fabricmc.api.Environment");
            platform1 = Platform.FABRIQUILT;
        } catch (ClassNotFoundException ignored) {
            // It's just not present, so we ignore it.
        }

        // Forge
        try {
            Class.forName("net.minecraftforge.api.distmarker.OnlyIn");
            platform1 = Platform.FORGE;
        } catch (ClassNotFoundException ignored) {
            // It's just not present, so we ignore it.
        }

        platform = platform1;
    }

    public String stringify(BuiltinSide side) {
        if (this.platform==Platform.FABRIQUILT) {
            String name = "";
            switch(side) {
                case SERVER -> name = "SERVER";
                case CLIENT -> name = "CLIENT";
            }
            return "@Environment("+name+")";
        } else if (this.platform==Platform.FORGE) {
            String name = "";
            switch(side) {
                case SERVER -> name = "DEDICATED_SERVER";
                case CLIENT -> name = "CLIENT";
            }
            return "@OnlyIn("+name+")";
        }
        String name = "";
        switch(side) {
            case SERVER -> name = "DEDICATED_SERVER";
            case CLIENT -> name = "CLIENT";
        }
        return "<builtin_platform_annotation>("+name+")";
    }

    public enum Platform {
        FORGE,FABRIQUILT;
    }
}
