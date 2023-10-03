package com.supermartijn642.formations.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Created 25/09/2023 by SuperMartijn642
 */
public interface FormationsStructureProcessor {

    /**
     * Processes a given block when a structure piece is about to be placed.
     * @param block             current state of the block
     * @param pos               position of the block
     * @param level             the level the structure is placed in
     * @param piecePosition     the position the structure piece is to be placed at
     * @param structurePosition the position of the overall structure
     * @param placeSettings     placement settings
     * @param pieceBlocks       all blocks in the structure piece (before any processing)
     * @return the desired instance of the block, if the block state is {@code null} the original block in the level will be kept
     */
    @NotNull
    BlockInstance processBlock(BlockInstance block, BlockPos pos, LevelReader level, BlockPos piecePosition, BlockPos structurePosition, StructurePlaceSettings placeSettings, Map<BlockPos,BlockInstance> pieceBlocks);
}
