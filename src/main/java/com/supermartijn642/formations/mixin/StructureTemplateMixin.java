package com.supermartijn642.formations.mixin;

import com.mojang.datafixers.util.Pair;
import com.supermartijn642.formations.Formations;
import com.supermartijn642.formations.structure.BlockInstance;
import com.supermartijn642.formations.structure.FormationsStructureProcessor;
import com.supermartijn642.formations.structure.processors.WaterloggingProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

/**
 * Created 25/09/2023 by SuperMartijn642
 */
@Mixin(StructureTemplate.class)
public class StructureTemplateMixin {

    @Inject(
        method = "placeInWorld(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Lnet/minecraft/util/RandomSource;I)Z",
        at = @At("HEAD")
    )
    private void placeInWorld(ServerLevelAccessor level, BlockPos structurePosition, BlockPos piecePosition, StructurePlaceSettings placeSettings, RandomSource random, int setBlockFlags, CallbackInfoReturnable<Boolean> ci){
        // If the list of processors contains a 'formations:waterlogging' processor, disable the keepLiquids option
        for(StructureProcessor processor : placeSettings.getProcessors()){
            if(processor instanceof WaterloggingProcessor){
                placeSettings.setKeepLiquids(false);
                return;
            }
        }
    }

    @ModifyVariable(
        method = "processBlockInfos",
        at = @At("HEAD"),
        ordinal = 0
    )
    private static List<StructureTemplate.StructureBlockInfo> processBlockInfos(List<StructureTemplate.StructureBlockInfo> blocks, ServerLevelAccessor level, BlockPos piecePosition, BlockPos structurePosition, StructurePlaceSettings placeSettings){
        // Find all the processors
        List<FormationsStructureProcessor> processors = null;
        for(StructureProcessor processor : placeSettings.getProcessors()){
            if(processor instanceof FormationsStructureProcessor){
                if(processors == null)
                    processors = new ArrayList<>();
                processors.add((FormationsStructureProcessor)processor);
            }
        }
        // Ignore if there aren't any FormationsStructureProcessor
        if(processors == null)
            return blocks;

        // Put all the blocks into a map
        Map<BlockPos,Pair<BlockPos,BlockInstance>> blocksByPosition = new HashMap<>(blocks.size());
        Map<BlockPos,BlockInstance> blockView = new HashMap<>(blocks.size());
        for(StructureTemplate.StructureBlockInfo block : blocks){
            BlockPos realPosition = StructureTemplate.calculateRelativePosition(placeSettings, block.pos()).offset(piecePosition);
            BlockInstance blockInstance = new BlockInstance(block.state(), block.nbt());
            blocksByPosition.put(realPosition, Pair.of(block.pos(), blockInstance));
            blockView.put(realPosition, blockInstance);
        }
        blockView = Collections.unmodifiableMap(blockView);
        // Create a list containing the processed blocks
        List<StructureTemplate.StructureBlockInfo> newBlocks = new ArrayList<>(blocks.size());
        for(Map.Entry<BlockPos,Pair<BlockPos,BlockInstance>> entry : blocksByPosition.entrySet()){
            BlockPos pos = entry.getKey();
            BlockInstance block = entry.getValue().getSecond();
            // Run all the processors
            for(FormationsStructureProcessor processor : processors){
                try{
                    BlockInstance newBlock = processor.processBlock(block, pos, level, piecePosition, structurePosition, placeSettings, blockView);
                    if(newBlock == null)
                        throw new NullPointerException("Processor returned null!");
                    block = newBlock;
                }catch(Exception e){
                    Formations.LOGGER.error("Encountered an exception whilst processing block '{}' with processor of class '{}'!", block, processor.getClass(), e);
                }
            }
            // Finally, add the resulting block to the list
            if(block.state() != null)
                newBlocks.add(new StructureTemplate.StructureBlockInfo(entry.getValue().getFirst(), block.state(), block.nbt()));
        }
        return newBlocks;
    }
}
