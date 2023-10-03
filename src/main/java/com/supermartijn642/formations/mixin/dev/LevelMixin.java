package com.supermartijn642.formations.mixin.dev;

import com.supermartijn642.formations.extensions.TemplateHoldingLevel;
import com.supermartijn642.formations.tools.template.TemplateManager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created 25/08/2023 by SuperMartijn642
 */
@Mixin(Level.class)
public class LevelMixin implements TemplateHoldingLevel {

    @Unique
    private TemplateManager templateManager;

    @Override
    public TemplateManager getFormationsTemplateManager(){
        return this.templateManager;
    }

    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    private void createTemplateManager(CallbackInfo ci){
        //noinspection DataFlowIssue
        this.templateManager = new TemplateManager((Level)(Object)this);
    }
}
