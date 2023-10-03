package com.supermartijn642.formations.mixin.dev;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StructureVoidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created 26/08/2023 by SuperMartijn642
 */
@Mixin(StructureVoidBlock.class)
public class StructureVoidBlockMixin {

    @Inject(
        method = "getShape",
        at = @At("HEAD"),
        cancellable = true
    )
    private void overrideShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> ci){
        if(context instanceof EntityCollisionContext && ((EntityCollisionContext)context).getEntity() instanceof Player player){
            Item mainItem = player.getItemInHand(InteractionHand.MAIN_HAND).getItem();
            Item secondaryItem = player.getItemInHand(InteractionHand.OFF_HAND).getItem();
            if((mainItem instanceof BlockItem && ((BlockItem)mainItem).getBlock() instanceof StructureVoidBlock)
                || (secondaryItem instanceof BlockItem && ((BlockItem)secondaryItem).getBlock() instanceof StructureVoidBlock))
                ci.setReturnValue(Shapes.block());
        }
    }
}
