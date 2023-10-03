package com.supermartijn642.formations.tools.template.packets;

import com.supermartijn642.core.CoreSide;
import com.supermartijn642.core.network.BasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.formations.tools.template.Template;
import com.supermartijn642.formations.tools.template.TemplateManager;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Created 25/08/2023 by SuperMartijn642
 */
public class CreateTemplatePacket implements BasePacket {

    private Template template;

    public CreateTemplatePacket(Template template){
        this.template = template;
    }

    public CreateTemplatePacket(){
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
    public boolean verify(PacketContext context){
        return Template.isValidName(this.template.getName());
    }

    @Override
    public void handle(PacketContext context){
        if(context.getHandlingSide() == CoreSide.SERVER){
            TemplateManager templateManager = TemplateManager.get(context.getWorld());
            Template sameNameTemplate = templateManager.getTemplateByName(this.template.getName());
            if(sameNameTemplate == null || sameNameTemplate.getArea().equals(this.template.getArea()))
                TemplateManager.get(context.getWorld()).addTemplate(this.template);
        }
    }
}
