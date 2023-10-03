package com.supermartijn642.formations.mixin.dev;

import com.supermartijn642.formations.tools.FormationsLevelData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created 18/09/2023 by SuperMartijn642
 */
@Mixin(RespawnAnchorBlock.class)
public class RespawnAnchorBlockMixin {

    @Inject(
        method = "explode",
        at = @At("HEAD"),
        cancellable = true
    )
    private void explode(BlockState state, Level level, BlockPos pos, CallbackInfo ci){
        if(level.isClientSide ? FormationsLevelData.CLIENT.isDevMode() : FormationsLevelData.SERVER.isDevMode())
            ci.cancel();
    }
}
