package com.supermartijn642.formations.tools.template;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.supermartijn642.formations.Formations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/**
 * Created 20/02/2023 by SuperMartijn642
 */
public class TemplateManagerSaveData extends SavedData {

    private static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath(Formations.MODID, "templates");

    private final TemplateManager manager;

    public static void init(ServerLevel level, TemplateManager manager){
        level.getDataStorage().computeIfAbsent(new SavedDataType<>(
            IDENTIFIER,
            () -> new TemplateManagerSaveData(manager),
            new Codec<>() {
                @Override
                public <T> DataResult<Pair<TemplateManagerSaveData,T>> decode(DynamicOps<T> ops, T input){
                    try{
                        TemplateManagerSaveData saveData = new TemplateManagerSaveData(manager);
                        saveData.load((CompoundTag)ops.convertTo(NbtOps.INSTANCE, input));
                        return DataResult.success(Pair.of(saveData, input));
                    }catch(Exception e){
                        return DataResult.error(e::getMessage);
                    }
                }

                @Override
                public <T> DataResult<T> encode(TemplateManagerSaveData input, DynamicOps<T> ops, T prefix){
                    try{
                        return DataResult.success(NbtOps.INSTANCE.convertTo(ops, input.save()));
                    }catch(Exception e){
                        return DataResult.error(e::getMessage);
                    }
                }
            },
            null
        ));
    }

    public TemplateManagerSaveData(TemplateManager manager){
        this.manager = manager;
    }

    public CompoundTag save(){
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
