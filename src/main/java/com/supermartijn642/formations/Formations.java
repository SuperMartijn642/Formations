package com.supermartijn642.formations;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created 7/7/2020 by SuperMartijn642
 */
public class Formations implements ModInitializer {

    public static final String MODID = "formations";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static ResourceLocation location(String identifier){
        return ResourceLocation.fromNamespaceAndPath(MODID, identifier);
    }

    @Override
    public void onInitialize(){
        FormationsStructures.init();

        // Only register dev stuff if we're in a development environment
        if(FabricLoader.getInstance().isModLoaded("supermartijn642corelib") && FabricLoader.getInstance().isDevelopmentEnvironment())
            FormationsDev.initDevTools();
    }
}
