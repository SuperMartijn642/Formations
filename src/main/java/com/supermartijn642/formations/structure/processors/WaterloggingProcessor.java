package com.supermartijn642.formations.structure.processors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.supermartijn642.formations.FormationsStructures;
import com.supermartijn642.formations.structure.BlockInstance;
import com.supermartijn642.formations.structure.FormationsStructureProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;

/**
 * Created 22/11/2024 by SuperMartijn642
 */
public class WaterloggingProcessor extends StructureProcessor implements FormationsStructureProcessor {

    public static final Codec<WaterloggingProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.group(WaterHandling.CODEC.optionalFieldOf("handling", WaterHandling.TAKE_STRUCTURE).forGetter(p -> p.waterHandling)).apply(instance, WaterloggingProcessor::new));

    private final WaterHandling waterHandling;

    public WaterloggingProcessor(WaterHandling waterHandling){
        this.waterHandling = waterHandling;
    }

    @Override
    public @NotNull BlockInstance processBlock(BlockInstance block, BlockPos pos, LevelReader level, BlockPos piecePosition, BlockPos structurePosition, StructurePlaceSettings placeSettings, Map<BlockPos,BlockInstance> pieceBlocks){
        BlockState state = block.state();
        if(state != null && state.hasProperty(BlockStateProperties.WATERLOGGED)){
            boolean waterlogged = state.getValue(BlockStateProperties.WATERLOGGED);
            if(this.waterHandling == WaterHandling.DRY)
                return waterlogged ? new BlockInstance(state.setValue(BlockStateProperties.WATERLOGGED, false), block.nbt()) : block;
            if(this.waterHandling == WaterHandling.WET)
                return waterlogged ? block : new BlockInstance(state.setValue(BlockStateProperties.WATERLOGGED, true), block.nbt());

            // Get whether the block contained water in the structure
            BlockState structureState = pieceBlocks.get(pos).state();
            boolean structureWaterlogged = structureState != null && structureState.getFluidState().is(Fluids.WATER);
            if(this.waterHandling == WaterHandling.TAKE_STRUCTURE)
                return waterlogged == structureWaterlogged ? block : new BlockInstance(state.setValue(BlockStateProperties.WATERLOGGED, structureWaterlogged), block.nbt());

            // Get whether the block contained water in the world
            boolean worldWaterlogged = level.getFluidState(pos).is(Fluids.WATER);
            if(this.waterHandling == WaterHandling.TAKE_WORLD)
                return waterlogged == worldWaterlogged ? block : new BlockInstance(state.setValue(BlockStateProperties.WATERLOGGED, worldWaterlogged), block.nbt());

            // Compare waterlogged between world and structure
            if(this.waterHandling == WaterHandling.WORLD_OR_STRUCTURE_WET)
                return waterlogged == (worldWaterlogged || structureWaterlogged) ? block : new BlockInstance(state.setValue(BlockStateProperties.WATERLOGGED, worldWaterlogged || structureWaterlogged), block.nbt());
            if(this.waterHandling == WaterHandling.WORLD_OR_STRUCTURE_DRY)
                return waterlogged == (worldWaterlogged && structureWaterlogged) ? block : new BlockInstance(state.setValue(BlockStateProperties.WATERLOGGED, worldWaterlogged && structureWaterlogged), block.nbt());
        }
        return block;
    }

    @Override
    protected StructureProcessorType<?> getType(){
        return FormationsStructures.WATERLOGGING_PROCESSOR.get();
    }

    public enum WaterHandling implements StringRepresentable {
        /**
         * Any waterloggable blocks will not be waterlogged.
         */
        DRY,
        /**
         * Any waterloggable blocks will be waterlogged.
         */
        WET,
        /**
         * Any waterloggable blocks will be waterlogged only if the block's position contained water before the structure was placed.
         */
        TAKE_WORLD,
        /**
         * Any waterloggable blocks will be waterlogged only if the block is waterlogged in the structure.
         */
        TAKE_STRUCTURE,
        /**
         * Any waterloggable blocks will be waterlogged only if either the block's position contained water before the structure was placed, or the block is waterlogged in the structure.
         */
        WORLD_OR_STRUCTURE_WET,
        /**
         * Any waterloggable blocks will be waterlogged only if both the block's position contained water before the structure was placed, and the block is waterlogged in the structure.
         */
        WORLD_OR_STRUCTURE_DRY;

        static final Codec<WaterHandling> CODEC = StringRepresentable.fromEnum(WaterHandling::values);

        @Override
        public String getSerializedName(){
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
