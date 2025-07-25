package com.supermartijn642.formations.tools.loot_table;

import com.supermartijn642.core.CoreSide;
import com.supermartijn642.core.network.BasePacket;
import com.supermartijn642.core.network.PacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;

/**
 * Created 27/08/2023 by SuperMartijn642
 */
public class SetLootTablePacket implements BasePacket {

    private BlockPos pos;
    private ResourceLocation lootTable;

    public SetLootTablePacket(BlockPos pos, ResourceLocation lootTable){
        this.pos = pos;
        this.lootTable = lootTable;
    }

    public SetLootTablePacket(){
    }

    @Override
    public void write(FriendlyByteBuf buffer){
        buffer.writeBlockPos(this.pos);
        buffer.writeBoolean(this.lootTable != null);
        if(this.lootTable != null)
            buffer.writeResourceLocation(this.lootTable);
    }

    @Override
    public void read(FriendlyByteBuf buffer){
        this.pos = buffer.readBlockPos();
        this.lootTable = buffer.readBoolean() ? buffer.readResourceLocation() : null;
    }

    @Override
    public void handle(PacketContext context){
        if(context.getHandlingSide() == CoreSide.SERVER && context.getSendingPlayer().blockPosition().distManhattan(this.pos) <= 32){
            BlockEntity entity = context.getWorld().getBlockEntity(this.pos);
            if(entity instanceof RandomizableContainerBlockEntity){
                Clearable.tryClear(entity);
                ((RandomizableContainerBlockEntity)entity).setLootTable(ResourceKey.create(Registries.LOOT_TABLE, this.lootTable), 0);
                entity.setChanged();
            }
        }
    }
}
