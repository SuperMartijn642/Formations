package com.supermartijn642.formations.generators;

import com.supermartijn642.core.generator.LootTableGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import net.minecraft.resources.Identifier;

/**
 * Created 04/09/2023 by SuperMartijn642
 */
public abstract class FormationsLootTableGenerator extends LootTableGenerator {

    public FormationsLootTableGenerator(String modid, ResourceCache cache){
        super(modid, cache);
    }

    @Override
    public LootTableBuilder lootTable(Identifier identifier){
        return super.lootTable(identifier);
    }

    @Override
    public LootTableBuilder lootTable(String namespace, String path){
        return super.lootTable(namespace, path);
    }

    public LootTableBuilder lootTable(String path){
        return this.lootTable(this.modid, path);
    }
}
