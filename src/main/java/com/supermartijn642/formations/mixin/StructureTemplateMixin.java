package com.supermartijn642.formations.mixin;

import com.supermartijn642.formations.Formations;
import com.supermartijn642.formations.structure.BlockInstance;
import com.supermartijn642.formations.structure.FormationsStructureProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created 25/09/2023 by SuperMartijn642
 */
@Mixin(StructureTemplate.class)
public class StructureTemplateMixin {

    @ModifyVariable(
        method = "processBlockInfos",
        at = @At("HEAD"),
        ordinal = 0
    )
    private static List<StructureTemplate.StructureBlockInfo> processBlockInfos(List<StructureTemplate.StructureBlockInfo> blocks, LevelAccessor level, BlockPos piecePosition, BlockPos structurePosition, StructurePlaceSettings placeSettings){
        // Find all the processors
        List<FormationsStructureProcessor> processors = placeSettings.getProcessors().stream()
            .filter(FormationsStructureProcessor.class::isInstance)
            .map(FormationsStructureProcessor.class::cast)
            .toList();
        // Ignore if there aren't any FormationsStructureProcessor
        if(processors.isEmpty())
            return blocks;

        // Put all the blocks into a map
        Map<BlockPos,BlockInstance> blocksByPosition = blocks.stream()
            .collect(Collectors.toUnmodifiableMap(info -> info.pos, block -> new BlockInstance(block.state, block.nbt)));
        // Create a list containing the processed blocks
        List<StructureTemplate.StructureBlockInfo> newBlocks = new ArrayList<>(blocks.size());
        for(Map.Entry<BlockPos,BlockInstance> entry : blocksByPosition.entrySet()){
            BlockPos pos = entry.getKey();
            BlockInstance block = entry.getValue();
            // Run all the processors
            for(FormationsStructureProcessor processor : processors){
                try{
                    BlockInstance newBlock = processor.processBlock(block, pos, level, piecePosition, structurePosition, placeSettings, blocksByPosition);
                    if(newBlock == null)
                        throw new NullPointerException("Processor returned null!");
                    block = newBlock;
                }catch(Exception e){
                    Formations.LOGGER.error("Encountered an exception whilst processing block '" + block + "' with processor of class '" + processor.getClass() + "'!", e);
                }
            }
            // Finally, add the resulting block to the list
            if(block.state() != null)
                newBlocks.add(new StructureTemplate.StructureBlockInfo(pos, block.state(), block.nbt()));
        }
        return newBlocks;
    }
}
