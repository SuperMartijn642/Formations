package com.supermartijn642.formations.structure.processors;

import com.mojang.serialization.MapCodec;
import com.supermartijn642.formations.FormationsStructures;
import com.supermartijn642.formations.structure.BlockInstance;
import com.supermartijn642.formations.structure.FormationsStructureProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created 01/09/2023 by SuperMartijn642
 */
public class BedColorProcessor extends StructureProcessor implements FormationsStructureProcessor {

    private static final BedColorProcessor INSTANCE = new BedColorProcessor();
    public static final MapCodec<BedColorProcessor> CODEC = MapCodec.unit(INSTANCE);
    private static final RandomSource RANDOM = RandomSource.create();

    private static final Map<DyeColor,BedBlock> COLOR_TO_BED_MAP;
    private static final List<DyeColor> BED_COLORS;

    static{
        List<Block> beds = List.of(
            Blocks.WHITE_BED,
            Blocks.ORANGE_BED,
            Blocks.MAGENTA_BED,
            Blocks.LIGHT_BLUE_BED,
            Blocks.YELLOW_BED,
            Blocks.LIME_BED,
            Blocks.PINK_BED,
            Blocks.GRAY_BED,
            Blocks.LIGHT_GRAY_BED,
            Blocks.CYAN_BED,
            Blocks.PURPLE_BED,
            Blocks.BLUE_BED,
            Blocks.BROWN_BED,
            Blocks.GREEN_BED,
            Blocks.RED_BED,
            Blocks.BLACK_BED
        );
        COLOR_TO_BED_MAP = beds.stream()
            .filter(BedBlock.class::isInstance)
            .map(BedBlock.class::cast)
            .collect(Collectors.toUnmodifiableMap(BedBlock::getColor, o -> o));
        BED_COLORS = Arrays.asList(COLOR_TO_BED_MAP.keySet().toArray(new DyeColor[0]));
    }

    @Override
    public @NotNull BlockInstance processBlock(BlockInstance block, BlockPos pos, LevelReader level, BlockPos piecePosition, BlockPos structurePosition, StructurePlaceSettings placeSettings, Map<BlockPos,BlockInstance> pieceBlocks){
        BlockState state = block.state();
        if(state != null && state.getBlock() instanceof BedBlock){
            BlockPos headPosition = state.getValue(BedBlock.PART) == BedPart.HEAD ? pos : pos.relative(state.mirror(placeSettings.getMirror()).rotate(placeSettings.getRotation()).getValue(BlockStateProperties.HORIZONTAL_FACING));
            RANDOM.setSeed(headPosition.asLong());
            DyeColor color = BED_COLORS.get(RANDOM.nextInt(BED_COLORS.size()));
            state = COLOR_TO_BED_MAP.get(color).withPropertiesOf(state);
            return new BlockInstance(state, block.nbt());
        }
        return block;
    }

    @Override
    protected StructureProcessorType<?> getType(){
        return FormationsStructures.BED_COLOR_PROCESSOR;
    }
}
