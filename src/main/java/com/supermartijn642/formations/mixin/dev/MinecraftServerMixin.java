package com.supermartijn642.formations.mixin.dev;

import com.supermartijn642.formations.Formations;
import com.supermartijn642.formations.tools.FormationsLevelData;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created 27/08/2023 by SuperMartijn642
 */
@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(
        method = "saveEverything",
        at = @At("HEAD")
    )
    private void saveEverything(CallbackInfoReturnable<Boolean> ci){
        try{
            //noinspection DataFlowIssue
            FormationsLevelData.SERVER.save((MinecraftServer)(Object)this);
        }catch(Exception e){
            Formations.LOGGER.error("Could not save Formations level data:", e);
        }
    }

    @Inject(
        method = "stopServer",
        at = @At("HEAD")
    )
    private void stopServer(CallbackInfo ci){
        try{
            //noinspection DataFlowIssue
            FormationsLevelData.SERVER.save((MinecraftServer)(Object)this);
        }catch(Exception e){
            Formations.LOGGER.error("Could not save Formations level data:", e);
        }
    }

    @Inject(
        method = "loadLevel",
        at = @At("HEAD")
    )
    private void loadLevel(CallbackInfo ci){
        try{
            //noinspection DataFlowIssue
            FormationsLevelData.SERVER.load((MinecraftServer)(Object)this);
        }catch(Exception e){
            Formations.LOGGER.error("Could not save Formations level data:", e);
        }
    }
}
