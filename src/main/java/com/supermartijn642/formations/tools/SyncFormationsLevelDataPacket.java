package com.supermartijn642.formations.tools;

import com.supermartijn642.core.CoreSide;
import com.supermartijn642.core.network.BasePacket;
import com.supermartijn642.core.network.PacketContext;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Created 27/08/2023 by SuperMartijn642
 */
public class SyncFormationsLevelDataPacket implements BasePacket {

    private boolean devMode;

    public SyncFormationsLevelDataPacket(boolean devMode){
        this.devMode = devMode;
    }

    public SyncFormationsLevelDataPacket(){
    }

    @Override
    public void write(FriendlyByteBuf buffer){
        buffer.writeBoolean(this.devMode);
    }

    @Override
    public void read(FriendlyByteBuf buffer){
        this.devMode = buffer.readBoolean();
    }

    @Override
    public void handle(PacketContext context){
        if(context.getHandlingSide() == CoreSide.CLIENT)
            FormationsLevelData.CLIENT.setDevMode(this.devMode);
    }
}
