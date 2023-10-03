package com.supermartijn642.formations.tools.template.packets;

import com.supermartijn642.core.CoreSide;
import com.supermartijn642.core.network.BasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.formations.tools.template.Template;
import com.supermartijn642.formations.tools.template.TemplateManager;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created 25/08/2023 by SuperMartijn642
 */
public class AllTemplatesPacket implements BasePacket {

    private static final int MAX_TEMPLATES = 1000;

    private List<Template> templates;

    public AllTemplatesPacket(Collection<Template> templates){
        if(templates.size() > MAX_TEMPLATES)
            throw new IllegalArgumentException("More than " + MAX_TEMPLATES + " templates! Realistically this should never happen!");
        this.templates = new ArrayList<>(templates);
    }

    public AllTemplatesPacket(){
    }

    @Override
    public void write(FriendlyByteBuf buffer){
        buffer.writeInt(this.templates.size());
        this.templates.forEach(template -> template.write(buffer));
    }

    @Override
    public void read(FriendlyByteBuf buffer){
        int count = buffer.readInt();
        if(count > MAX_TEMPLATES)
            throw new IllegalStateException("Received AllTemplatesPacket packet for " + count + " templates! This should never be more than " + MAX_TEMPLATES + "!");
        this.templates = new ArrayList<>(count);
        for(int i = 0; i < count; i++)
            this.templates.add(Template.load(buffer));
    }

    @Override
    public boolean verify(PacketContext context){
        return this.templates.stream().allMatch(template -> Template.isValidName(template.getName()));
    }

    @Override
    public void handle(PacketContext context){
        if(context.getHandlingSide() == CoreSide.CLIENT){
            TemplateManager templateManager = TemplateManager.get(context.getWorld());
            templateManager.clearAll();
            this.templates.forEach(templateManager::addTemplate);
        }
    }
}
