package com.supermartijn642.formations.structure;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Created 25/09/2023 by SuperMartijn642
 */
public record BlockInstance(@Nullable BlockState state, @Nullable CompoundTag nbt) {

    public static final BlockInstance NOTHING = new BlockInstance(null, null);

    @Override
    public String toString(){
        return "{state=" + this.state + ", nbt=" + this.nbt + '}';
    }
}
