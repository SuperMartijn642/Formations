package com.supermartijn642.formations.tools;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.block.BlockShape;
import com.supermartijn642.core.render.RenderUtils;
import com.supermartijn642.core.render.RenderWorldEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.StructureVoidBlock;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Created 26/08/2023 by SuperMartijn642
 */
public class StructureVoidHighlighter {

    private static final int HIGHLIGHT_RANGE = 15;
    private static final BlockShape HIGHLIGHT_SHAPE = BlockShape.fullCube().shrink(0.2);
    private static final BlockPos.MutableBlockPos[] DUMMY_POSITIONS = new BlockPos.MutableBlockPos[HIGHLIGHT_RANGE * HIGHLIGHT_RANGE * HIGHLIGHT_RANGE];

    public static void registerListeners(){
        NeoForge.EVENT_BUS.addListener(StructureVoidHighlighter::highlightStructureVoidBlocks);
    }

    private static void highlightStructureVoidBlocks(RenderWorldEvent e){
        // Check if dev mode is enabled
        if(!FormationsLevelData.CLIENT.isDevMode())
            return;

        // Check if the player is holding a structure void block
        Player player = ClientUtils.getPlayer();
        Item mainItem = player.getItemInHand(InteractionHand.MAIN_HAND).getItem();
        Item secondaryItem = player.getItemInHand(InteractionHand.OFF_HAND).getItem();
        if(!(mainItem instanceof BlockItem && ((BlockItem)mainItem).getBlock() instanceof StructureVoidBlock)
            && !(secondaryItem instanceof BlockItem && ((BlockItem)secondaryItem).getBlock() instanceof StructureVoidBlock))
            return;

        // Highlight all the structure void blocks in a HIGHLIGHT_RANGE block radius
        Level level = ClientUtils.getWorld();
        BlockPos center = player.getOnPos();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int index = 0;
        for(int xOffset = -HIGHLIGHT_RANGE; xOffset <= HIGHLIGHT_RANGE; xOffset++){
            for(int yOffset = -HIGHLIGHT_RANGE; yOffset <= HIGHLIGHT_RANGE; yOffset++){
                for(int zOffset = -HIGHLIGHT_RANGE; zOffset <= HIGHLIGHT_RANGE; zOffset++){
                    // Check if the block is a structure void block
                    pos.set(center.getX() + xOffset, center.getY() + yOffset, center.getZ() + zOffset);
                    if(!(level.getBlockState(pos).getBlock() instanceof StructureVoidBlock))
                        continue;

                    BlockPos.MutableBlockPos dummyPosition = DUMMY_POSITIONS[index];
                    if(dummyPosition == null)
                        dummyPosition = DUMMY_POSITIONS[index] = new BlockPos.MutableBlockPos();
                    dummyPosition.set(pos);
                    index++;
                }
            }
        }

        int count = index;
        e.submitFeatures((poseStack, output) -> {
            for(int i = 0; i < count; i++){
                BlockPos.MutableBlockPos dummyPosition = DUMMY_POSITIONS[i];
                poseStack.pushPose();
                poseStack.translate(dummyPosition.getX(), dummyPosition.getY(), dummyPosition.getZ());
                RenderUtils.submitShapeSides(output, poseStack, HIGHLIGHT_SHAPE, 245 / 255f, 93 / 255f, 209 / 255f, 0.9f, true);
                poseStack.popPose();
            }
        });
    }
}
