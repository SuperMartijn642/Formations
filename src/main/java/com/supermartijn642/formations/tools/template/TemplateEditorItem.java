package com.supermartijn642.formations.tools.template;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.WidgetScreen;
import com.supermartijn642.core.item.BaseItem;
import com.supermartijn642.core.item.ItemProperties;
import com.supermartijn642.formations.tools.template.screen.TemplateEditingScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Created 25/08/2023 by SuperMartijn642
 */
public class TemplateEditorItem extends BaseItem {

    public TemplateEditorItem(ItemProperties properties){
        super(properties);
    }

    @Override
    public ItemUseResult interact(ItemStack stack, Player player, InteractionHand hand, Level level){
        if(player.isShiftKeyDown()){
            if(level.isClientSide){
                if(TemplateRenderer.selectionDimension == level.dimension() && TemplateRenderer.selectionPos1 != null && TemplateRenderer.selectionPos2 != null)
                    ClientUtils.displayScreen(WidgetScreen.of(new TemplateEditingScreen(null)));
                else
                    player.displayClientMessage(TextComponents.translation("formations.template.edit.no_selection").color(ChatFormatting.RED).get(), true);
            }
            return ItemUseResult.success(stack);
        }
        return super.interact(stack, player, hand, level);
    }

    public void leftClickMiss(ItemStack stack, Player player){
        // If the player was aiming at a template, open the editor screen
        Template template = TemplateRenderer.getAimedAtTemplate();
        if(template != null)
            ClientUtils.displayScreen(WidgetScreen.of(new TemplateEditingScreen(template)));
    }

    @Override
    public InteractionFeedback interactWithBlock(ItemStack stack, Player player, InteractionHand hand, Level level, BlockPos hitPos, Direction hitSide, Vec3 hitLocation){
        if(level.isClientSide){
            if(player.isShiftKeyDown()){
                // Open the editor menu
                if(TemplateRenderer.selectionDimension == level.dimension() && TemplateRenderer.selectionPos1 != null && TemplateRenderer.selectionPos2 != null)
                    ClientUtils.displayScreen(WidgetScreen.of(new TemplateEditingScreen(null)));
                else
                    player.displayClientMessage(TextComponents.translation("formations.template.edit.no_selection").color(ChatFormatting.RED).get(), true);
                return InteractionFeedback.SUCCESS;
            }else{
                // Set second selection pos
                if(TemplateRenderer.selectionDimension != level.dimension())
                    TemplateRenderer.selectionPos1 = null;
                TemplateRenderer.selectionDimension = level.dimension();
                TemplateRenderer.selectionPos2 = hitPos;
            }
        }
        return InteractionFeedback.SUCCESS;
    }

    public boolean leftClickBlock(ItemStack stack, Player player, BlockPos pos){
        Level level = player.level;
        if(level.isClientSide){
            if(TemplateRenderer.selectionDimension != level.dimension())
                TemplateRenderer.selectionPos2 = null;
            TemplateRenderer.selectionDimension = level.dimension();
            TemplateRenderer.selectionPos1 = pos;
        }
        return true;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, Player player){
        return true;
    }

    @Override
    protected void appendItemInformation(ItemStack stack, @Nullable BlockGetter level, Consumer<Component> info, boolean advanced){
        info.accept(TextComponents.translation("formations.template_editor_item.left_click_block", TextComponents.fromTextComponent(ClientUtils.getMinecraft().options.keyAttack.getTranslatedKeyMessage()).color(ChatFormatting.GOLD).get()).color(ChatFormatting.GRAY).get());
        info.accept(TextComponents.translation("formations.template_editor_item.right_click_block", TextComponents.fromTextComponent(ClientUtils.getMinecraft().options.keyUse.getTranslatedKeyMessage()).color(ChatFormatting.GOLD).get()).color(ChatFormatting.GRAY).get());
        info.accept(TextComponents.translation("formations.template_editor_item.shift_right_click", TextComponents.fromTextComponent(ClientUtils.getMinecraft().options.keyShift.getTranslatedKeyMessage()).color(ChatFormatting.GOLD).get(), TextComponents.fromTextComponent(ClientUtils.getMinecraft().options.keyUse.getTranslatedKeyMessage()).color(ChatFormatting.GOLD).get()).color(ChatFormatting.GRAY).get());
        info.accept(TextComponents.translation("formations.template_editor_item.left_click", TextComponents.fromTextComponent(ClientUtils.getMinecraft().options.keyAttack.getTranslatedKeyMessage()).color(ChatFormatting.GOLD).get()).color(ChatFormatting.GRAY).get());
        super.appendItemInformation(stack, level, info, advanced);
    }
}
