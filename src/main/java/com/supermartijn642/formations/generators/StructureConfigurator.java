package com.supermartijn642.formations.generators;

import java.util.Locale;

/**
 * Created 03/09/2023 by SuperMartijn642
 */
public abstract class StructureConfigurator {

    final String name, identifier;

    public StructureConfigurator(String name){
        this.name = name;
        this.identifier = name.toLowerCase(Locale.ROOT).replace(' ', '_');
    }

    protected abstract void configureStructure(StructureConfiguration properties);

    protected abstract void createTemplatePools(TemplatePoolGenerator generator);

    protected void createLootTables(FormationsLootTableGenerator generator){
    }
}
