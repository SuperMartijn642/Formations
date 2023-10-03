package com.supermartijn642.formations.tools.loot_table;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.CoreSide;
import com.supermartijn642.core.gui.WidgetScreen;
import com.supermartijn642.core.network.BasePacket;
import com.supermartijn642.core.network.PacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * Created 27/08/2023 by SuperMartijn642
 */
public class OpenLootTableScreenPacket implements BasePacket {

    private BlockPos pos;
    private ResourceLocation original;

    public OpenLootTableScreenPacket(BlockPos pos, ResourceLocation original){
        this.pos = pos;
        this.original = original;
    }

    public OpenLootTableScreenPacket(){
    }

    @Override
    public void write(FriendlyByteBuf buffer){
        buffer.writeBlockPos(this.pos);
        buffer.writeBoolean(this.original != null);
        if(this.original != null)
            buffer.writeResourceLocation(this.original);
    }

    @Override
    public void read(FriendlyByteBuf buffer){
        this.pos = buffer.readBlockPos();
        this.original = buffer.readBoolean() ? buffer.readResourceLocation() : null;
    }

    @Override
    public void handle(PacketContext context){
        if(context.getHandlingSide() == CoreSide.CLIENT)
            ClientUtils.displayScreen(WidgetScreen.of(new LootTableEditingScreen(this.pos, this.original)));
    }
}
