package com.supermartijn642.formations;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created 7/7/2020 by SuperMartijn642
 */
@Mod(Formations.MODID)
public class Formations {

    public static final String MODID = "formations";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static ResourceLocation location(String identifier){
        return ResourceLocation.fromNamespaceAndPath(MODID, identifier);
    }

    public Formations(){
        FormationsStructures.init();

        // Only register dev stuff if we're in a development environment
        if(ModList.get().isLoaded("supermartijn642corelib") && !FMLEnvironment.production)
            FormationsDev.initDevTools();

        // Client stuff
        if(FMLEnvironment.dist == Dist.CLIENT)
            FormationsClient.onInitializeClient();
    }
}
