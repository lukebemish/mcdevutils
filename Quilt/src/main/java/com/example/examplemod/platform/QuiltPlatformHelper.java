package com.example.examplemod.platform;

import com.example.examplemod.platform.services.IPlatformHelper;
import com.google.auto.service.AutoService;
import org.quiltmc.loader.api.QuiltLoader;

@AutoService(IPlatformHelper.class)
public class QuiltPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Quilt";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return QuiltLoader.isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return QuiltLoader.isDevelopmentEnvironment();
    }
}
