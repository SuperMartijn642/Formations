package com.supermartijn642.formations.tools;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.block.BlockShape;
import com.supermartijn642.core.render.RenderConfiguration;
import com.supermartijn642.core.render.RenderStateConfiguration;
import com.supermartijn642.core.render.RenderUtils;
import com.supermartijn642.core.render.RenderWorldEvent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.StructureVoidBlock;
import net.minecraft.world.phys.Vec3;

/**
 * Created 26/08/2023 by SuperMartijn642
 */
public class StructureVoidHighlighter {

    private static final int HIGHLIGHT_RANGE = 15;
    private static final BlockShape HIGHLIGHT_SHAPE = BlockShape.fullCube().shrink(0.2);

    public static void registerListeners(){
        RenderWorldEvent.EVENT.register(StructureVoidHighlighter::highlightStructureVoidBlocks);
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

        e.getPoseStack().pushPose();
        Vec3 camera = RenderUtils.getCameraPosition();
        e.getPoseStack().translate(-camera.x, -camera.y, -camera.z);

        // Highlight all the structure void blocks in a HIGHLIGHT_RANGE block radius
        Level level = ClientUtils.getWorld();
        BlockPos center = player.getOnPos();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        MultiBufferSource.BufferSource bufferSource = RenderUtils.getMainBufferSource();
        VertexConsumer builder = QUADS.begin(bufferSource);
        for(int xOffset = -HIGHLIGHT_RANGE; xOffset <= HIGHLIGHT_RANGE; xOffset++){
            for(int yOffset = -HIGHLIGHT_RANGE; yOffset <= HIGHLIGHT_RANGE; yOffset++){
                for(int zOffset = -HIGHLIGHT_RANGE; zOffset <= HIGHLIGHT_RANGE; zOffset++){
                    // Check if the block is a structure void block
                    pos.set(center.getX() + xOffset, center.getY() + yOffset, center.getZ() + zOffset);
                    if(!(level.getBlockState(pos).getBlock() instanceof StructureVoidBlock))
                        continue;

                    e.getPoseStack().pushPose();
                    e.getPoseStack().translate(pos.getX(), pos.getY(), pos.getZ());
//                    RenderUtils.renderShapeSides(e.getPoseStack(), HIGHLIGHT_SHAPE, 245 / 255f, 93 / 255f, 209 / 255f, 0.9f, true);
                    renderShapeSides(e.getPoseStack(), builder, HIGHLIGHT_SHAPE, 245 / 255f, 93 / 255f, 209 / 255f, 0.9f);
                    e.getPoseStack().popPose();
                }
            }
        }
        QUADS.end(bufferSource);

        e.getPoseStack().popPose();
    }

    private static final RenderConfiguration QUADS = RenderConfiguration.create("supermartijn642corelib", "quads", DefaultVertexFormat.POSITION_COLOR, RenderConfiguration.PrimitiveType.QUADS, 256, false, true, RenderStateConfiguration.builder().useShader(GameRenderer::getPositionColorShader).useTranslucentTransparency().disableTexture().disableCulling().useLessThanOrEqualDepthTest().disableDepthMask().build());

    public static void renderShapeSides(PoseStack poseStack, VertexConsumer builder, BlockShape shape, float red, float green, float blue, float alpha){
        Matrix4f matrix = poseStack.last().pose();
        shape.forEachBox((box) -> {
            float minX = (float)box.minX;
            float maxX = (float)box.maxX;
            float minY = (float)box.minY;
            float maxY = (float)box.maxY;
            float minZ = (float)box.minZ;
            float maxZ = (float)box.maxZ;
            builder.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        });
    }
}
