package com.supermartijn642.formations.tools.template.packets;

import com.supermartijn642.core.network.BasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.formations.tools.template.Template;
import com.supermartijn642.formations.tools.template.TemplateManager;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Created 26/08/2023 by SuperMartijn642
 */
public class DeleteTemplatePacket implements BasePacket {

    private Template template;

    public DeleteTemplatePacket(Template template){
        this.template = template;
    }

    public DeleteTemplatePacket(){
    }

    @Override
    public void write(FriendlyByteBuf buffer){
        this.template.write(buffer);
    }

    @Override
    public void read(FriendlyByteBuf buffer){
        this.template = Template.load(buffer);
    }

    @Override
    public void handle(PacketContext context){
        TemplateManager templateManager = TemplateManager.get(context.getWorld());
        Template templateByName = templateManager.getTemplateByName(this.template.getName());
        if(templateByName != null && templateByName.getArea().equals(this.template.getArea()))
            templateManager.removeTemplate(this.template);
    }
}
