package com.supermartijn642.formations.tools;

import com.supermartijn642.formations.FormationsDev;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Created 27/08/2023 by SuperMartijn642
 */
public class FormationsLevelData {

    public static void registerListeners(){
        MinecraftForge.EVENT_BUS.addListener((Consumer<PlayerEvent.PlayerLoggedInEvent>)event -> SERVER.syncToPlayer(event.getEntity()));
    }

    public static FormationsLevelData SERVER = new FormationsLevelData(), CLIENT = new FormationsLevelData();

    private boolean devMode;

    private FormationsLevelData(){
    }

    public boolean isDevMode(){
        return this.devMode;
    }

    public void setDevMode(boolean devMode){
        if(this.devMode == devMode)
            return;
        this.devMode = devMode;

        // Sync change to players
        if(this == SERVER)
            this.syncToAll();
    }

    public void save(MinecraftServer server){
        Path file = server.storageSource.getLevelPath(LevelResource.ROOT).resolve("formations-data");
        try(OutputStream stream = Files.newOutputStream(file)){
            stream.write(this.devMode ? 1 : 0);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    public void load(MinecraftServer server){
        Path file = server.storageSource.getLevelPath(LevelResource.ROOT).resolve("formations-data");
        if(Files.exists(file)){
            try(InputStream stream = Files.newInputStream(file)){
                this.devMode = stream.read() == 1;
            }catch(IOException e){
                throw new RuntimeException(e);
            }
        }else
            this.devMode = false;
    }

    public void syncToPlayer(Player player){
        FormationsDev.CHANNEL.sendToPlayer(player, new SyncFormationsLevelDataPacket(this.devMode));
    }

    public void syncToAll(){
        FormationsDev.CHANNEL.sendToAllPlayers(new SyncFormationsLevelDataPacket(this.devMode));
    }
}
