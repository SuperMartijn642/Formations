package com.supermartijn642.formations.mixin.dev;

import com.supermartijn642.formations.tools.FormationsLevelData;
import net.minecraft.world.level.BaseSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created 01/09/2023 by SuperMartijn642
 */
@Mixin(BaseSpawner.class)
public class BaseSpawnerMixin {

    @Shadow
    private int spawnDelay;

    @Inject(
        method = "clientTick",
        at = @At("HEAD")
    )
    private void clientTick(CallbackInfo ci){
        // Always set the spawn delay to 20 ticks if dev mode is enabled
        if(FormationsLevelData.CLIENT.isDevMode())
            this.spawnDelay = 20;
    }

    @Inject(
        method = "serverTick",
        at = @At("HEAD")
    )
    private void serverTick(CallbackInfo ci){
        // Always set the spawn delay to 20 ticks if dev mode is enabled
        if(FormationsLevelData.SERVER.isDevMode())
            this.spawnDelay = 20;
    }
}
