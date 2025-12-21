package com.supermartijn642.formations;

import net.minecraft.resources.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created 7/7/2020 by SuperMartijn642
 */
@Mod(Formations.MODID)
public class Formations {

    public static final String MODID = "formations";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static Identifier location(String identifier){
        return Identifier.fromNamespaceAndPath(MODID, identifier);
    }

    public Formations(FMLJavaModLoadingContext context){
        FormationsStructures.init(context);

        // Only register dev stuff if we're in a development environment
        if(ModList.get().isLoaded("supermartijn642corelib") && !FMLEnvironment.production)
            FormationsDev.initDevTools();

        // Client stuff
        if(FMLEnvironment.dist == Dist.CLIENT)
            FormationsClient.onInitializeClient();
    }
}
