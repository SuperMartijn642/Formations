package com.supermartijn642.formations.tools.loot_table;

import com.supermartijn642.formations.FormationsDev;
import com.supermartijn642.formations.tools.FormationsLevelData;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.function.Consumer;

/**
 * Created 27/08/2023 by SuperMartijn642
 */
public class ContainerOpenIntercept {

    public static void registerListeners(){
        NeoForge.EVENT_BUS.addListener((Consumer<PlayerInteractEvent.RightClickBlock>)event -> {
            Player player = event.getEntity();
            Level world = event.getLevel();
            if(player.isSpectator() || world.isClientSide || !FormationsLevelData.SERVER.isDevMode())
                return;

            if(player.isShiftKeyDown() && (player.getMainHandItem().getItem() instanceof BlockItem || player.getOffhandItem().getItem() instanceof BlockItem))
                return;

            BlockEntity entity = world.getBlockEntity(event.getPos());
            if(entity instanceof RandomizableContainerBlockEntity && (!player.isShiftKeyDown() || ((RandomizableContainerBlockEntity)entity).lootTable != null)){
                FormationsDev.CHANNEL.sendToPlayer(player, new OpenLootTableScreenPacket(entity.getBlockPos(), ((RandomizableContainerBlockEntity)entity).lootTable.location()));
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.FAIL);
            }
        });
    }
}
