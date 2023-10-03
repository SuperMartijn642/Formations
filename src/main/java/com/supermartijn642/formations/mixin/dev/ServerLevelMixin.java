package com.supermartijn642.formations.mixin.dev;

import com.supermartijn642.formations.tools.template.TemplateManager;
import com.supermartijn642.formations.tools.template.TemplateManagerSaveData;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created 21/02/2023 by SuperMartijn642
 */
@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    private void constructor(CallbackInfo ci){
        //noinspection DataFlowIssue
        ServerLevel level = (ServerLevel)(Object)this;
        TemplateManagerSaveData.init(level, TemplateManager.get(level));
    }
}
