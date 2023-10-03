package com.supermartijn642.formations.tools.generators;

import com.supermartijn642.core.generator.ModelGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.formations.Formations;
import com.supermartijn642.formations.FormationsDev;

/**
 * Created 25/08/2023 by SuperMartijn642
 */
public class FormationsToolsModelGenerator extends ModelGenerator {
    public FormationsToolsModelGenerator(ResourceCache cache){
        super(Formations.MODID, cache);
    }

    @Override
    public void generate(){
        this.itemHandheld(FormationsDev.templateEditorItem, Formations.location("items/staff"));
    }

    @Override
    public String getName(){
        return this.modName + " Tools Model Generator";
    }
}
