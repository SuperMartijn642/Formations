package com.supermartijn642.formations.structure.processors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.supermartijn642.formations.FormationsStructures;
import com.supermartijn642.formations.structure.BlockInstance;
import com.supermartijn642.formations.structure.FormationsStructureProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;

/**
 * Created 22/09/2023 by SuperMartijn642
 */
public class RespawnAnchorProcessor extends StructureProcessor implements FormationsStructureProcessor {

    public static final Codec<RespawnAnchorProcessor> CODEC = RecordCodecBuilder.<RespawnAnchorProcessor>create(instance -> instance.group(Codec.intRange(0, 4).optionalFieldOf("minCharges", 0).forGetter(p -> p.minCharges), Codec.intRange(0, 4).optionalFieldOf("maxCharges", 4).forGetter(p -> p.maxCharges)).apply(instance, RespawnAnchorProcessor::new))
        .comapFlatMap(processor -> {
            if(processor.minCharges > processor.maxCharges)
                return DataResult.error("Max charges must be greater than or equal to min charges, minCharges: " + processor.minCharges + ", maxCharges: " + processor.maxCharges);
            return DataResult.success(processor);
        }, Function.identity());

    private final int minCharges, maxCharges;

    public RespawnAnchorProcessor(int minCharges, int maxCharges){
        this.minCharges = minCharges;
        this.maxCharges = maxCharges;
    }

    public RespawnAnchorProcessor(){
        this(0, 4);
    }

    @Override
    public @NotNull BlockInstance processBlock(BlockInstance block, BlockPos pos, LevelReader level, BlockPos piecePosition, BlockPos structurePosition, StructurePlaceSettings placeSettings, Map<BlockPos,BlockInstance> pieceBlocks){
        if(block.state() != null && block.state().is(Blocks.RESPAWN_ANCHOR)){
            int charges = this.minCharges + placeSettings.getRandom(pos).nextInt(this.maxCharges - this.minCharges);
            return new BlockInstance(block.state().setValue(RespawnAnchorBlock.CHARGE, charges), block.nbt());
        }
        return block;
    }

    @Override
    protected StructureProcessorType<?> getType(){
        return FormationsStructures.RESPAWN_ANCHOR_PROCESSOR.get();
    }
}
