package com.supermartijn642.formations.structure.processors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.supermartijn642.formations.FormationsStructures;
import com.supermartijn642.formations.structure.BlockInstance;
import com.supermartijn642.formations.structure.FormationsStructureProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created 14/09/2023 by SuperMartijn642
 */
public class FormationsBlockAgeProcessor extends StructureProcessor implements FormationsStructureProcessor {

    public static final Codec<FormationsBlockAgeProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.floatRange(0, 1).optionalFieldOf("mossy_chance", 0.3f).forGetter(p -> p.mossiness), Codec.floatRange(0, 1).optionalFieldOf("degradation_chance", 0.15f).forGetter(p -> p.degradation), Codec.floatRange(0, 1).optionalFieldOf("disintegration_chance", 0.05f).forGetter(p -> p.disintegration)).apply(instance, FormationsBlockAgeProcessor::new));

    private static final Set<Block> DISINTEGRATABLE_BLOCKS = Set.of(
        Blocks.STONE_BRICKS, Blocks.STONE_BRICK_STAIRS, Blocks.STONE_BRICK_SLAB, Blocks.STONE_BRICK_WALL,
        Blocks.CRACKED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS, Blocks.COBBLESTONE, Blocks.COBBLESTONE_STAIRS,
        Blocks.COBBLESTONE_SLAB, Blocks.COBBLESTONE_WALL, Blocks.MOSSY_STONE_BRICKS, Blocks.MOSSY_STONE_BRICK_STAIRS,
        Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.MOSSY_COBBLESTONE, Blocks.MOSSY_COBBLESTONE_STAIRS,
        Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.MOSSY_COBBLESTONE_WALL, Blocks.SANDSTONE, Blocks.SANDSTONE_STAIRS,
        Blocks.SANDSTONE_SLAB, Blocks.SANDSTONE_WALL, Blocks.CUT_SANDSTONE, Blocks.CHISELED_SANDSTONE,
        Blocks.RED_SANDSTONE, Blocks.RED_SANDSTONE_STAIRS, Blocks.RED_SANDSTONE_SLAB, Blocks.RED_SANDSTONE_WALL,
        Blocks.CUT_RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE
    );
    private static final Map<Block,Block> BLOCK_TO_STAIR = Map.of(
        Blocks.STONE_BRICKS, Blocks.STONE_BRICK_STAIRS,
        Blocks.COBBLESTONE, Blocks.COBBLESTONE_STAIRS,
        Blocks.SANDSTONE, Blocks.SANDSTONE_STAIRS,
        Blocks.RED_SANDSTONE, Blocks.SANDSTONE_STAIRS,
        Blocks.DEEPSLATE_BRICKS, Blocks.DEEPSLATE_BRICK_STAIRS
    );
    private static final Map<Block,Block> STAIR_TO_SLAB = Map.of(
        Blocks.STONE_BRICK_STAIRS, Blocks.STONE_BRICK_SLAB,
        Blocks.COBBLESTONE_STAIRS, Blocks.COBBLESTONE_SLAB,
        Blocks.SANDSTONE_STAIRS, Blocks.SANDSTONE_SLAB,
        Blocks.RED_SANDSTONE_STAIRS, Blocks.RED_SANDSTONE_SLAB,
        Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.DEEPSLATE_BRICK_SLAB
    );
    private static final Map<Block,List<Block>> BLOCK_TO_MOSSINESS = Map.of(
        Blocks.STONE_BRICKS, List.of(Blocks.MOSSY_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS),
        Blocks.STONE_BRICK_STAIRS, List.of(Blocks.MOSSY_STONE_BRICK_STAIRS),
        Blocks.STONE_BRICK_SLAB, List.of(Blocks.MOSSY_STONE_BRICK_SLAB),
        Blocks.STONE_BRICK_WALL, List.of(Blocks.MOSSY_STONE_BRICK_WALL),
        Blocks.COBBLESTONE, List.of(Blocks.MOSSY_COBBLESTONE),
        Blocks.COBBLESTONE_STAIRS, List.of(Blocks.MOSSY_COBBLESTONE_STAIRS),
        Blocks.COBBLESTONE_SLAB, List.of(Blocks.MOSSY_COBBLESTONE_SLAB),
        Blocks.COBBLESTONE_WALL, List.of(Blocks.MOSSY_COBBLESTONE_WALL),
        Blocks.DEEPSLATE_BRICKS, List.of(Blocks.CRACKED_DEEPSLATE_BRICKS),
        Blocks.POLISHED_BLACKSTONE_BRICKS, List.of(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS)
    );

    private final float mossiness, degradation, disintegration;

    public FormationsBlockAgeProcessor(float mossiness, float degradation, float disintegration){
        this.mossiness = mossiness;
        this.degradation = degradation;
        this.disintegration = disintegration;
    }

    public FormationsBlockAgeProcessor(){
        this(0.3f, 0.15f, 0.05f);
    }

    @Override
    public @NotNull BlockInstance processBlock(BlockInstance block, BlockPos pos, LevelReader level, BlockPos piecePosition, BlockPos structurePosition, StructurePlaceSettings placeSettings, Map<BlockPos,BlockInstance> pieceBlocks){
        RandomSource random = placeSettings.getRandom(pos);
        BlockState state = block.state();

        // Disintegrate blocks
        if(DISINTEGRATABLE_BLOCKS.contains(state.getBlock()) && random.nextFloat() < this.disintegration)
            return BlockInstance.NOTHING;

        // Convert full blocks to stairs
        if(BLOCK_TO_STAIR.containsKey(state.getBlock()) && random.nextFloat() < this.degradation){
            state = BLOCK_TO_STAIR.get(state.getBlock()).withPropertiesOf(state);
            if(state.hasProperty(BlockStateProperties.HORIZONTAL_FACING))
                state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random));
            if(state.hasProperty(BlockStateProperties.HALF))
                state = state.setValue(BlockStateProperties.HALF, random.nextBoolean() ? Half.TOP : Half.BOTTOM);
        }
        // Convert stairs to slabs
        if(STAIR_TO_SLAB.containsKey(state.getBlock()) && random.nextFloat() < this.degradation){
            boolean topHalf = state.hasProperty(BlockStateProperties.HALF) ? state.getValue(BlockStateProperties.HALF) == Half.TOP : random.nextBoolean();
            state = STAIR_TO_SLAB.get(state.getBlock()).withPropertiesOf(state);
            if(state.hasProperty(BlockStateProperties.SLAB_TYPE))
                state = state.setValue(BlockStateProperties.SLAB_TYPE, topHalf ? SlabType.TOP : SlabType.BOTTOM);
        }

        // Convert regular blocks to mossy/cracked variants
        if(BLOCK_TO_MOSSINESS.containsKey(state.getBlock()) && random.nextFloat() < this.mossiness){
            List<Block> options = BLOCK_TO_MOSSINESS.get(state.getBlock());
            state = options.get(random.nextInt(options.size())).withPropertiesOf(state);
        }

        if(state == block.state())
            return block;
        return new BlockInstance(state, state.is(block.state().getBlock()) ? block.nbt() : null);
    }

    @Override
    protected StructureProcessorType<?> getType(){
        return FormationsStructures.BLOCK_AGE_PROCESSOR.get();
    }
}
