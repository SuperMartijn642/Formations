package com.supermartijn642.formations.tools.template;

import com.supermartijn642.core.util.Pair;
import com.supermartijn642.formations.Formations;
import com.supermartijn642.formations.FormationsDev;
import com.supermartijn642.formations.extensions.TemplateHoldingLevel;
import com.supermartijn642.formations.tools.template.packets.AllTemplatesPacket;
import com.supermartijn642.formations.tools.template.packets.DeleteTemplatePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created 25/08/2023 by SuperMartijn642
 */
public class TemplateManager {

    public static TemplateManager get(Level level){
        return ((TemplateHoldingLevel)level).getFormationsTemplateManager();
    }

    public static void registerListeners(){
        MinecraftForge.EVENT_BUS.addListener((Consumer<PlayerEvent.PlayerChangedDimensionEvent>)event -> get(event.getEntity().level).sendTemplatesToPlayer(event.getEntity()));
        MinecraftForge.EVENT_BUS.addListener((Consumer<PlayerEvent.PlayerLoggedInEvent>)event -> get(event.getEntity().level).sendTemplatesToPlayer(event.getEntity()));
        MinecraftForge.EVENT_BUS.addListener((Consumer<PlayerEvent.PlayerRespawnEvent>)event -> get(event.getEntity().level).sendTemplatesToPlayer(event.getEntity()));
    }

    private final Level level;
    private final boolean isClientSide;
    private final Set<Template> templates = new HashSet<>();
    private final Map<AABB,Template> templatesByArea = new HashMap<>();
    private final Map<String,Template> templatesByName = new HashMap<>();
    private final Set<String> removedTemplates = new HashSet<>();

    public TemplateManager(Level level){
        this.level = level;
        this.isClientSide = level.isClientSide;
    }

    public void addTemplate(Template template){
        if(this.templatesByArea.containsKey(template.getArea())){
            Template oldTemplate = this.templatesByArea.get(template.getArea());
            this.templates.remove(oldTemplate);
            this.templatesByName.remove(oldTemplate.getName());
            this.removedTemplates.add(oldTemplate.getName());
        }
        this.templates.add(template);
        this.templatesByArea.put(template.getArea(), template);
        this.templatesByName.put(template.getName(), template);

        // Send the changes to the clients
        if(!this.isClientSide)
            this.sendAllTemplates();
    }

    public Template getTemplateByName(String name){
        return this.templatesByName.get(name);
    }

    public Set<Template> getAllTemplates(){
        return Collections.unmodifiableSet(this.templates);
    }

    public void removeTemplate(Template template){
        this.templates.remove(template);
        this.templatesByName.remove(template.getName());
        this.templatesByArea.remove(template.getArea());
        this.removedTemplates.add(template.getName());

        // Send a packet to clients
        if(!this.isClientSide)
            FormationsDev.CHANNEL.sendToDimension(this.level, new DeleteTemplatePacket(template));
    }

    public void clearAll(){
        this.templates.clear();
        this.templatesByArea.clear();
        this.templatesByName.clear();
        this.removedTemplates.clear();
    }

    private void sendTemplatesToPlayer(Player player){
        if(this.isClientSide)
            throw new AssertionError();

        FormationsDev.CHANNEL.sendToPlayer(player, new AllTemplatesPacket(this.templates));
    }

    private void sendAllTemplates(){
        if(this.isClientSide)
            throw new AssertionError();

        FormationsDev.CHANNEL.sendToDimension(this.level, new AllTemplatesPacket(this.templates));
    }

    public Pair<Integer,Integer> exportAll(){
        if(this.isClientSide)
            throw new AssertionError();

        // Check if a specific output location is set
        String pathProperty = System.getProperty("formations.templates.export-location");
        Path path = pathProperty != null && !pathProperty.isBlank() ?
            Paths.get(pathProperty.trim()) :
            this.level.getServer().storageSource.getDimensionPath(this.level.dimension()).resolve("formations").resolve("templates");

        // Remove files for removed templates
        this.removedTemplates.removeAll(this.templatesByName.keySet());
        for(String removedTemplate : this.removedTemplates){
            Path file = path.resolve(removedTemplate + ".nbt");
            try{
                Files.deleteIfExists(file);
            }catch(IOException e){
                Formations.LOGGER.error("Failed to remove file '" + file + "' for removed a template:", e);
            }
        }

        // Save all templates
        int successes = 0, fails = 0;
        for(Template template : this.templates){
            Path file = path.resolve(template.getName() + ".nbt");
            try{
                if(template.recordAndExport(this.level, file))
                    successes++;
                else
                    fails++;
            }catch(Exception e){
                Formations.LOGGER.error("Failed to capture and save template '" + template.getName() + "':", e);
                fails++;
            }
        }

        return Pair.of(successes, fails);
    }

    CompoundTag write(){
        if(this.isClientSide)
            throw new AssertionError();

        CompoundTag data = new CompoundTag();
        ListTag templates = new ListTag();
        this.templates.stream().map(Template::write).forEach(templates::add);
        data.put("templates", templates);
        ListTag removedTemplates = new ListTag();
        this.removedTemplates.stream().map(StringTag::valueOf).forEach(removedTemplates::add);
        data.put("removedTemplates", removedTemplates);
        return data;
    }

    void read(CompoundTag data){
        if(this.isClientSide)
            throw new AssertionError();

        this.clearAll();
        ListTag templates = data.getList("templates", Tag.TAG_COMPOUND);
        templates.stream()
            .map(CompoundTag.class::cast)
            .map(Template::load)
            .forEach(this.templates::add);
        this.templates.forEach(template -> this.templatesByArea.put(template.getArea(), template));
        this.templates.forEach(template -> this.templatesByName.put(template.getName(), template));
        ListTag removedTemplates = data.getList("removedTemplates", Tag.TAG_STRING);
        removedTemplates.stream()
            .map(StringTag.class::cast)
            .map(StringTag::getAsString)
            .forEach(this.removedTemplates::add);
    }
}
