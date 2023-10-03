package com.supermartijn642.formations.tools.template;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.render.RenderUtils;
import com.supermartijn642.core.render.RenderWorldEvent;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

import java.util.Optional;

/**
 * Created 25/08/2023 by SuperMartijn642
 */
public class TemplateRenderer {

    public static ResourceKey<Level> selectionDimension;
    public static BlockPos selectionPos1, selectionPos2;

    public static void registerListeners(){
        RenderWorldEvent.EVENT.register(TemplateRenderer::renderTemplates);
    }

    public static Template getAimedAtTemplate(){
        // Check if the player is looking at a block
        HitResult hitResult = ClientUtils.getMinecraft().hitResult;
        if(hitResult != null && hitResult.getType() != HitResult.Type.MISS)
            return null;

        // Find the aimed at template
        Vec3 playerPos = ClientUtils.getMinecraft().gameRenderer.getMainCamera().getPosition();
        Vec3 playerFacingPos = new Vec3(ClientUtils.getMinecraft().gameRenderer.getMainCamera().getPosition().toVector3f().add(ClientUtils.getMinecraft().gameRenderer.getMainCamera().getLookVector().mul(100)));
        Template aimedTemplate = null;
        double distance = 0;
        for(Template template : TemplateManager.get(ClientUtils.getWorld()).getAllTemplates()){
            Optional<Double> d = template.getArea().clip(playerPos, playerFacingPos).map(playerPos::distanceToSqr);
            if(d.isPresent() && (aimedTemplate == null || d.get() < distance)){
                aimedTemplate = template;
                distance = d.get();
            }
        }
        return aimedTemplate;
    }

    private static void renderTemplates(RenderWorldEvent e){
        e.getPoseStack().pushPose();

        Vec3 camera = RenderUtils.getCameraPosition();
        e.getPoseStack().translate(-camera.x, -camera.y, -camera.z);

        // Render all the saved templates
        TemplateManager.get(ClientUtils.getWorld()).getAllTemplates().stream()
            .filter(template -> template.getArea().distanceToSqr(ClientUtils.getPlayer().position()) < 200 * 200)
            .forEach(template -> {
                RenderUtils.renderBox(e.getPoseStack(), template.getArea().inflate(0.1), 1, 1, 1, 0.8f, true);
                RenderUtils.renderBoxSides(e.getPoseStack(), template.getArea().inflate(0.1), 1, 1, 1, 0.2f, true);
                renderTemplateText(e.getPoseStack(), template);
            });

        if(!(ClientUtils.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof TemplateEditorItem)){
            e.getPoseStack().popPose();
            return;
        }

        // Render the template currently being added
        if(ClientUtils.getWorld().dimension() == selectionDimension && selectionPos1 != null && selectionPos2 != null){
            AABB box = new AABB(selectionPos1).minmax(new AABB(selectionPos2)).inflate(0.1);
            RenderUtils.renderBox(e.getPoseStack(), box, 1, 1, 0.5f, 1, false);
            RenderUtils.renderBoxSides(e.getPoseStack(), box, 1, 1, 0.5f, 0.1f, false);
            RenderUtils.renderBoxSides(e.getPoseStack(), box, 1, 1, 0.5f, 0.3f, true);
        }else{
            if(ClientUtils.getWorld().dimension() == selectionDimension && (selectionPos1 != null || selectionPos2 != null))
                RenderUtils.renderBox(e.getPoseStack(), new AABB(selectionPos1 == null ? selectionPos2 : selectionPos1), 1, 1, 0.5f, 1, false);

            // If the player is looking at a box, highlight it
            Template aimedTemplate = getAimedAtTemplate();
            if(aimedTemplate != null){
                RenderUtils.renderBox(e.getPoseStack(), aimedTemplate.getArea().inflate(0.1), 1, 1, 0.5f, 1, false);
                RenderUtils.renderBoxSides(e.getPoseStack(), aimedTemplate.getArea().inflate(0.1), 1, 1, 1, 0.4f, false);
            }
        }

        e.getPoseStack().popPose();
    }

    private static void renderTemplateText(PoseStack poseStack, Template template){
        AABB area = template.getArea();
        Font renderer = ClientUtils.getFontRenderer();
        int nameWidth = renderer.width(template.getName());
        Vec3 center = area.getCenter();
        float xScaling = -Math.min(((float)area.getXsize() + 1) / nameWidth, ((float)area.getYsize() + 1) / renderer.lineHeight) * 0.8f;
        float zScaling = -Math.min(((float)area.getZsize() + 1) / nameWidth, ((float)area.getYsize() + 1) / renderer.lineHeight) * 0.8f;
        // South
        poseStack.pushPose();
        poseStack.translate(center.x, center.y, area.minZ - 0.1);
        poseStack.scale(xScaling, xScaling, 1);
        ScreenUtils.drawString(poseStack, template.getName(), -nameWidth / 2f, -renderer.lineHeight / 2f, 0xC8FFFFFF);
        poseStack.popPose();
        // North
        poseStack.pushPose();
        poseStack.translate(center.x, center.y, area.maxZ + 0.1);
        poseStack.scale(-xScaling, xScaling, 1);
        ScreenUtils.drawString(poseStack, template.getName(), -nameWidth / 2f, -renderer.lineHeight / 2f, 0xC8FFFFFF);
        poseStack.popPose();
        // West
        poseStack.pushPose();
        poseStack.translate(area.minX - 0.1, center.y, center.z);
        poseStack.scale(1, zScaling, zScaling);
        poseStack.mulPose(new Quaternionf().rotateAxis((float)Math.PI / 2, 0, 1, 0));
        ScreenUtils.drawString(poseStack, template.getName(), -nameWidth / 2f, -renderer.lineHeight / 2f, 0xC8FFFFFF);
        poseStack.popPose();
        // East
        poseStack.pushPose();
        poseStack.translate(area.maxX + 0.1, center.y, center.z);
        poseStack.scale(1, zScaling, -zScaling);
        poseStack.mulPose(new Quaternionf().rotateAxis((float)Math.PI / 2, 0, 1, 0));
        ScreenUtils.drawString(poseStack, template.getName(), -nameWidth / 2f, -renderer.lineHeight / 2f, 0xC8FFFFFF);
        poseStack.popPose();
    }
}
