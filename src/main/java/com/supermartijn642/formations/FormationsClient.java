package com.supermartijn642.formations;

import com.supermartijn642.formations.tools.StructureVoidHighlighter;
import com.supermartijn642.formations.tools.template.TemplateRenderer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * Created 7/1/2021 by SuperMartijn642
 */
public class FormationsClient {

    public static void onInitializeClient(){
        // Only register dev stuff if we're in a development environment
        if(ModList.get().isLoaded("supermartijn642corelib") && !FMLEnvironment.production){
            TemplateRenderer.registerListeners();
            StructureVoidHighlighter.registerListeners();
        }
    }
}
