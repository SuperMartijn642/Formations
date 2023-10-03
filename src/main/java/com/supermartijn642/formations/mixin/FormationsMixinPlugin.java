package com.supermartijn642.formations.mixin;

import net.fabricmc.loader.impl.util.SystemProperties;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Created 26/04/2023 by SuperMartijn642
 */
public class FormationsMixinPlugin implements IMixinConfigPlugin {

    private static final boolean isDevEnvironment = Boolean.parseBoolean(System.getProperty(SystemProperties.DEVELOPMENT, "false"));

    @Override
    public void onLoad(String mixinPackage){
    }

    @Override
    public String getRefMapperConfig(){
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName){
        return isDevEnvironment || !mixinClassName.startsWith("com.supermartijn642.formations.mixin.dev.");
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets){
    }

    @Override
    public List<String> getMixins(){
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo){
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo){
    }
}
