package com.supermartijn642.formations.mixin.dev;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.formations.tools.template.TemplateEditorItem;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created 25/08/2023 by SuperMartijn642
 */
@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    @Inject(
        method = "startDestroyBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/GameType;isCreative()Z",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    private void interruptDestroyBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> ci){
        ItemStack stack = ClientUtils.getPlayer().getMainHandItem();
        if(stack.getItem() instanceof TemplateEditorItem && ((TemplateEditorItem)stack.getItem()).leftClickBlock(stack, ClientUtils.getPlayer(), pos))
            ci.setReturnValue(true);
    }
}
