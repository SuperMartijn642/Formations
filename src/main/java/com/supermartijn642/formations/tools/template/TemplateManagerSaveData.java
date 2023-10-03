package com.supermartijn642.formations.tools.template;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * Created 20/02/2023 by SuperMartijn642
 */
public class TemplateManagerSaveData extends SavedData {

    private static final String IDENTIFIER = "formations_templates";

    private final TemplateManager manager;

    public static void init(ServerLevel level, TemplateManager manager){
        level.getDataStorage().computeIfAbsent(tag -> {
            TemplateManagerSaveData saveData = new TemplateManagerSaveData(manager);
            saveData.load(tag);
            return saveData;
        }, () -> new TemplateManagerSaveData(manager), IDENTIFIER);
    }

    public TemplateManagerSaveData(TemplateManager manager){
        this.manager = manager;
    }

    @Override
    public CompoundTag save(CompoundTag tag){
        return this.manager.write();
    }

    public void load(CompoundTag tag){
        this.manager.read(tag);
    }

    @Override
    public boolean isDirty(){
        return true;
    }
}
