package com.supermartijn642.formations.structure.processors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.supermartijn642.formations.FormationsStructures;
import com.supermartijn642.formations.structure.BlockInstance;
import com.supermartijn642.formations.structure.FormationsStructureProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Created 01/09/2023 by SuperMartijn642
 */
public class PlantGrowthProcessor extends StructureProcessor implements FormationsStructureProcessor {

    public static final MapCodec<PlantGrowthProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.floatRange(0, 1).optionalFieldOf("minGrowth", 0f).forGetter(p -> p.minGrowth), Codec.floatRange(0, 1).optionalFieldOf("maxGrowth", 1f).forGetter(p -> p.maxGrowth)).apply(instance, PlantGrowthProcessor::new));

    private final float minGrowth, maxGrowth;

    public PlantGrowthProcessor(float minGrowth, float maxGrowth){
        this.minGrowth = minGrowth;
        this.maxGrowth = maxGrowth;
    }

    @Override
    public @NotNull BlockInstance processBlock(BlockInstance block, BlockPos pos, LevelReader level, BlockPos piecePosition, BlockPos structurePosition, StructurePlaceSettings placeSettings, Map<BlockPos,BlockInstance> pieceBlocks){
        if(block.state() == null)
            return block;

        // CropBlock
        if(block.state().getBlock() instanceof CropBlock crop){
            int age = Math.round((this.minGrowth + placeSettings.getRandom(pos).nextFloat() * (this.maxGrowth - this.minGrowth)) * crop.getMaxAge());
            return new BlockInstance(crop.getStateForAge(age), block.nbt());
        }
        // StemBlock
        if(block.state().getBlock() instanceof StemBlock){
            int age = Math.round((this.minGrowth + placeSettings.getRandom(pos).nextFloat() * (this.maxGrowth - this.minGrowth)) * StemBlock.MAX_AGE);
            return new BlockInstance(block.state().setValue(StemBlock.AGE, age), block.nbt());
        }
        // NetherWartBlock
        if(block.state().getBlock() instanceof NetherWartBlock){
            int age = Math.round((this.minGrowth + placeSettings.getRandom(pos).nextFloat() * (this.maxGrowth - this.minGrowth)) * NetherWartBlock.MAX_AGE);
            return new BlockInstance(block.state().setValue(NetherWartBlock.AGE, age), block.nbt());
        }
        return block;
    }

    @Override
    protected StructureProcessorType<?> getType(){
        return FormationsStructures.PLANT_GROWTH_PROCESSOR;
    }
}
