package com.supermartijn642.formations.tools.loot_table;

import com.supermartijn642.formations.FormationsDev;
import com.supermartijn642.formations.tools.FormationsLevelData;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;

/**
 * Created 27/08/2023 by SuperMartijn642
 */
public class ContainerOpenIntercept {

    public static void registerListeners(){
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if(player.isSpectator() || world.isClientSide || !FormationsLevelData.SERVER.isDevMode())
                return InteractionResult.PASS;

            if(player.isShiftKeyDown() && (player.getMainHandItem().getItem() instanceof BlockItem || player.getOffhandItem().getItem() instanceof BlockItem))
                return InteractionResult.PASS;

            BlockEntity entity = world.getBlockEntity(hitResult.getBlockPos());
            if(entity instanceof RandomizableContainerBlockEntity && (!player.isShiftKeyDown() || ((RandomizableContainerBlockEntity)entity).lootTable != null)){
                FormationsDev.CHANNEL.sendToPlayer(player, new OpenLootTableScreenPacket(entity.getBlockPos(), ((RandomizableContainerBlockEntity)entity).lootTable.location()));
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });
    }
}
