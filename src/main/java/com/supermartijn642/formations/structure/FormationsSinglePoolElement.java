package com.supermartijn642.formations.structure;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.supermartijn642.formations.FormationsStructures;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/**
 * Created 30/08/2023 by SuperMartijn642
 */
public class FormationsSinglePoolElement extends SinglePoolElement {

    public static final MapCodec<FormationsSinglePoolElement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        SinglePoolElement.templateCodec(),
        SinglePoolElement.processorsCodec(),
        SinglePoolElement.projectionCodec(),
        ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("ground_level", 1).forGetter(FormationsSinglePoolElement::getGroundLevelDelta)
    ).apply(instance, FormationsSinglePoolElement::new));

    private final int ground_level;

    protected FormationsSinglePoolElement(Either<ResourceLocation,StructureTemplate> either, Holder<StructureProcessorList> holder, StructureTemplatePool.Projection projection, int groundLevel){
        super(either, holder, projection);
        this.ground_level = groundLevel;
    }

    @Override
    public int getGroundLevelDelta(){
        return this.ground_level;
    }

    @Override
    public StructurePoolElementType<?> getType(){
        return FormationsStructures.SINGLE_POOL_ELEMENT;
    }
}
