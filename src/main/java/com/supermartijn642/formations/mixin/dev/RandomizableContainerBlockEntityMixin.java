package com.supermartijn642.formations.mixin.dev;

import com.supermartijn642.formations.FormationsDev;
import com.supermartijn642.formations.tools.FormationsLevelData;
import com.supermartijn642.formations.tools.loot_table.OpenLootTableScreenPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created 27/08/2023 by SuperMartijn642
 */
@Mixin(RandomizableContainerBlockEntity.class)
public class RandomizableContainerBlockEntityMixin {

    @Inject(
        method = "createMenu",
        at = @At("HEAD"),
        cancellable = true
    )
    private void openLootTableMenu(int containerId, Inventory inventory, Player player, CallbackInfoReturnable<AbstractContainerMenu> ci){
        if(!player.level.isClientSide && FormationsLevelData.SERVER.isDevMode()){
            //noinspection DataFlowIssue
            RandomizableContainerBlockEntity entity = (RandomizableContainerBlockEntity)(Object)this;
            if(!player.isShiftKeyDown() || entity.lootTable != null){
                FormationsDev.CHANNEL.sendToPlayer(player, new OpenLootTableScreenPacket(entity.getBlockPos(), entity.lootTable));
                ci.setReturnValue(null);
            }
        }
    }
}
