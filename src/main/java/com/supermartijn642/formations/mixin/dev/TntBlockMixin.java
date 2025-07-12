package com.supermartijn642.formations.mixin.dev;

import com.supermartijn642.formations.tools.FormationsLevelData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TntBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created 31/08/2023 by SuperMartijn642
 */
@Mixin(TntBlock.class)
public class TntBlockMixin {

    @Inject(
        method = "prime(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/LivingEntity;)Z",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void cancelExplosion(Level level, BlockPos pos, LivingEntity igniter, CallbackInfoReturnable<Boolean> ci){
        if(!level.isClientSide && FormationsLevelData.SERVER.isDevMode())
            ci.setReturnValue(false);
    }
}
