package com.supermartijn642.formations.mixin.dev;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.formations.tools.template.TemplateEditorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created 26/08/2023 by SuperMartijn642
 */
@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(
        method = "startAttack",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;resetAttackStrengthTicker()V",
            shift = At.Shift.AFTER
        )
    )
    private void checkTemplateEditing(CallbackInfoReturnable<Boolean> ci){
        Player player = ClientUtils.getPlayer();
        ItemStack stack = player.getMainHandItem();
        if(stack.getItem() instanceof TemplateEditorItem)
            ((TemplateEditorItem)stack.getItem()).leftClickMiss(stack, player);
    }
}
