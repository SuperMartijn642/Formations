package com.supermartijn642.formations.tools.template;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.block.BlockShape;
import com.supermartijn642.core.render.RenderUtils;
import com.supermartijn642.core.render.RenderWorldEvent;
import com.supermartijn642.core.util.Pair;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created 25/08/2023 by SuperMartijn642
 */
public class TemplateRenderer {

    public static ResourceKey<Level> selectionDimension;
    public static BlockPos selectionPos1, selectionPos2;

    public static void registerListeners(){
        RenderWorldEvent.EVENT_BUS.addListener(TemplateRenderer::renderTemplates);
    }

    public static Template getAimedAtTemplate(){
        // Check if the player is looking at a block
        HitResult hitResult = ClientUtils.getMinecraft().hitResult;
        if(hitResult != null && hitResult.getType() != HitResult.Type.MISS)
            return null;

        // Find the aimed at template
        Vec3 playerPos = ClientUtils.getMinecraft().gameRenderer.mainCamera().position();
        Vec3 playerFacingPos = new Vec3(ClientUtils.getMinecraft().gameRenderer.mainCamera().position().toVector3f().add(ClientUtils.getMinecraft().gameRenderer.mainCamera().forwardVector().mul(100, new Vector3f())));
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
        List<Box> boxes = new ArrayList<>();
        List<Pair<AABB,String>> text = new ArrayList<>();

        // Render all the saved templates
        TemplateManager.get(ClientUtils.getWorld()).getAllTemplates().stream()
            .filter(template -> template.getArea().distanceToSqr(ClientUtils.getPlayer().position()) < 200 * 200)
            .forEach(template -> {
                BlockShape shape = BlockShape.create(template.getArea().inflate(0.1));
                boxes.add(new Box(shape, false, 1, 1, 1, 0.8f, true));
                boxes.add(new Box(shape, false, 1, 1, 1, 0.2f, true));
                text.add(Pair.of(template.getArea(), template.getName()));
            });

        if((ClientUtils.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof TemplateEditorItem)){
            // Render the template currently being added
            if(ClientUtils.getWorld().dimension() == selectionDimension && selectionPos1 != null && selectionPos2 != null){
                BlockShape shape = BlockShape.create(new AABB(selectionPos1).minmax(new AABB(selectionPos2)).inflate(0.1));
                boxes.add(new Box(shape, false, 1, 1, 0.5f, 1, false));
                boxes.add(new Box(shape, true, 1, 1, 0.5f, 0.3f, false));
            }else{
                if(ClientUtils.getWorld().dimension() == selectionDimension && (selectionPos1 != null || selectionPos2 != null))
                    boxes.add(new Box(BlockShape.create(new AABB(selectionPos1 == null ? selectionPos2 : selectionPos1)), false, 1, 1, 0.5f, 1, false));

                // If the player is looking at a box, highlight it
                Template aimedTemplate = getAimedAtTemplate();
                if(aimedTemplate != null){
                    BlockShape shape = BlockShape.create(aimedTemplate.getArea().inflate(0.1));
                    boxes.add(new Box(shape, false, 1, 1, 0.5f, 1, false));
                    boxes.add(new Box(shape, true, 1, 1, 1, 0.4f, false));
                }
            }
        }

        e.submitFeatures((poseStack, output) -> {
            for(Box box : boxes)
                box.submit(output, poseStack);
            for(Pair<AABB,String> t : text)
                submitTemplateText(output, poseStack, t.left(), t.right());
        });
    }

    private static void submitTemplateText(SubmitNodeCollector output, PoseStack poseStack, AABB area, String name){
        Font renderer = ClientUtils.getFontRenderer();
        int nameWidth = renderer.width(name);
        Vec3 center = area.getCenter();
        float xScaling = -Math.min(((float)area.getXsize() + 1) / nameWidth, ((float)area.getYsize() + 1) / renderer.lineHeight) * 0.8f;
        float zScaling = -Math.min(((float)area.getZsize() + 1) / nameWidth, ((float)area.getYsize() + 1) / renderer.lineHeight) * 0.8f;
        // South
        poseStack.pushPose();
        poseStack.translate(center.x, center.y, area.minZ - 0.1);
        poseStack.scale(xScaling, xScaling, 1);
        output.submitText(poseStack, -nameWidth / 2f, -renderer.lineHeight / 2f, FormattedCharSequence.forward(name, Style.EMPTY), false, Font.DisplayMode.NORMAL, LightCoordsUtil.FULL_BRIGHT, 0xC8FFFFFF, 0, 0);
        poseStack.popPose();
        // North
        poseStack.pushPose();
        poseStack.translate(center.x, center.y, area.maxZ + 0.1);
        poseStack.scale(-xScaling, xScaling, 1);
        output.submitText(poseStack, -nameWidth / 2f, -renderer.lineHeight / 2f, FormattedCharSequence.forward(name, Style.EMPTY), false, Font.DisplayMode.NORMAL, LightCoordsUtil.FULL_BRIGHT, 0xC8FFFFFF, 0, 0);
        poseStack.popPose();
        // West
        poseStack.pushPose();
        poseStack.translate(area.minX - 0.1, center.y, center.z);
        poseStack.scale(1, zScaling, zScaling);
        poseStack.mulPose(new Quaternionf().rotateAxis((float)Math.PI / 2, 0, 1, 0));
        output.submitText(poseStack, -nameWidth / 2f, -renderer.lineHeight / 2f, FormattedCharSequence.forward(name, Style.EMPTY), false, Font.DisplayMode.NORMAL, LightCoordsUtil.FULL_BRIGHT, 0xC8FFFFFF, 0, 0);
        poseStack.popPose();
        // East
        poseStack.pushPose();
        poseStack.translate(area.maxX + 0.1, center.y, center.z);
        poseStack.scale(1, zScaling, -zScaling);
        poseStack.mulPose(new Quaternionf().rotateAxis((float)Math.PI / 2, 0, 1, 0));
        output.submitText(poseStack, -nameWidth / 2f, -renderer.lineHeight / 2f, FormattedCharSequence.forward(name, Style.EMPTY), false, Font.DisplayMode.NORMAL, LightCoordsUtil.FULL_BRIGHT, 0xC8FFFFFF, 0, 0);
        poseStack.popPose();
    }

    private record Box(BlockShape box, boolean sides, float red, float green, float blue, float alpha, boolean depthTest) {
        void submit(SubmitNodeCollector output, PoseStack poseStack){
            if(this.sides)
                RenderUtils.submitShapeSides(output, poseStack, this.box, this.red, this.green, this.blue, this.alpha, this.depthTest);
            else
                RenderUtils.submitShape(output, poseStack, this.box, this.red, this.green, this.blue, this.alpha, this.depthTest);
        }
    }
}
