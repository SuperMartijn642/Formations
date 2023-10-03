package com.supermartijn642.formations;

import com.supermartijn642.formations.tools.StructureVoidHighlighter;
import com.supermartijn642.formations.tools.template.TemplateRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Created 7/1/2021 by SuperMartijn642
 */
public class FormationsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient(){
        // Only register dev stuff if we're in a development environment
        if(FabricLoader.getInstance().isModLoaded("supermartijn642corelib") && FabricLoader.getInstance().isDevelopmentEnvironment()){
            TemplateRenderer.registerListeners();
            StructureVoidHighlighter.registerListeners();
        }
    }
}
