package com.supermartijn642.formations.mixin.dev;

import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.formations.tools.FormationsLevelData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.TickTask;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created 31/08/2023 by SuperMartijn642
 */
@Mixin(TntBlock.class)
public class TntBlockMixin {

    @Inject(
        method = "explode(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/LivingEntity;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void cancelExplosion(Level level, BlockPos pos, LivingEntity igniter, CallbackInfo ci){
        if(!level.isClientSide && FormationsLevelData.SERVER.isDevMode()){
            // Place back the tnt block during the next tick
            BlockState state = level.getBlockState(pos);
            CommonUtils.getServer().tell(new TickTask(0, () -> level.setBlock(pos, state, Block.UPDATE_ALL)));
            ci.cancel();
        }
    }
}
