package com.supermartijn642.formations.tools.template.screen;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.GuiGraphicsHelper;
import com.supermartijn642.core.gui.widget.BaseWidget;
import com.supermartijn642.core.gui.widget.WidgetRenderContext;
import com.supermartijn642.core.gui.widget.premade.TextFieldWidget;
import com.supermartijn642.formations.Formations;
import com.supermartijn642.formations.FormationsDev;
import com.supermartijn642.formations.tools.template.Template;
import com.supermartijn642.formations.tools.template.TemplateManager;
import com.supermartijn642.formations.tools.template.TemplateRenderer;
import com.supermartijn642.formations.tools.template.packets.CreateTemplatePacket;
import com.supermartijn642.formations.tools.template.packets.DeleteTemplatePacket;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Created 25/08/2023 by SuperMartijn642
 */
public class TemplateEditingScreen extends BaseWidget {

    private static final Identifier SCREEN_BACKGROUND = Formations.location("textures/gui/template_creation_screen.png");

    private final Template template;
    private TemplateEditButton saveButton;

    public TemplateEditingScreen(Template template){
        super(0, 0, 144, 166);
        this.template = template;
    }

    @Override
    public Component getNarrationMessage(){
        return this.template == null ?
            TextComponents.translation("formations.template.edit.new_template").get() :
            TextComponents.translation("formations.template.edit.edit_template").get();
    }

    @Override
    public void addWidgets(){
        if(this.template == null){
            // Name field
            TextFieldWidget nameField = new TextFieldWidget(8, 32, 128, 12, "", Template.MAX_NAME_LENGTH, t -> this.saveButton.active = Template.isValidName(t) && TemplateManager.get(ClientUtils.getWorld()).getTemplateByName(t.trim()) == null);
            nameField.setSuggestion(I18n.get("formations.template.edit.name_hint"));
            this.addWidget(nameField);
            // Cancel button
            this.addWidget(new TemplateEditButton(0, 151, 72, 15, TextComponents.translation("formations.template.edit.cancel").get(), TemplateEditButton.LEFT_BUTTON, () -> {
                TemplateRenderer.selectionDimension = null;
                ClientUtils.getMinecraft().gui.setScreen(null);
            }));
            // Save button
            this.saveButton = new TemplateEditButton(72, 151, 72, 15, TextComponents.translation("formations.template.edit.save").get(), TemplateEditButton.GREEN_RIGHT_BUTTON, () -> {
                String name = nameField.getText().trim();
                if(Template.isValidName(name) && TemplateManager.get(ClientUtils.getWorld()).getTemplateByName(name) == null){
                    FormationsDev.CHANNEL.sendToServer(new CreateTemplatePacket(Template.create(new AABB(TemplateRenderer.selectionPos1).minmax(new AABB(TemplateRenderer.selectionPos2)), name)));
                    TemplateRenderer.selectionDimension = null;
                    ClientUtils.getMinecraft().gui.setScreen(null);
                }
            });
            this.saveButton.active = false;
            this.addWidget(this.saveButton);
        }else{
            // Name field
            TextFieldWidget nameField = new TextFieldWidget(8, 32, 128, 12, this.template.getName(), Template.MAX_NAME_LENGTH, t -> this.saveButton.active = Template.isValidName(t) && (t.trim().equals(this.template.getName()) || TemplateManager.get(ClientUtils.getWorld()).getTemplateByName(t.trim()) == null));
            nameField.setSuggestion(I18n.get("formations.template.edit.name_hint"));
            this.addWidget(nameField);
            // Delete button
            this.addWidget(new TemplateEditButton(0, 151, 72, 15, TextComponents.translation("formations.template.edit.delete").get(), TemplateEditButton.RED_LEFT_BUTTON, () -> {
                FormationsDev.CHANNEL.sendToServer(new DeleteTemplatePacket(this.template));
                ClientUtils.getMinecraft().gui.setScreen(null);
            }));
            // Confirm button
            this.saveButton = new TemplateEditButton(72, 151, 72, 15, TextComponents.translation("formations.template.edit.confirm").get(), TemplateEditButton.RIGHT_BUTTON, () -> {
                String name = nameField.getText().trim();
                if(name.equals(this.template.getName()))
                    ClientUtils.getMinecraft().gui.setScreen(null);
                else if(Template.isValidName(name) && TemplateManager.get(ClientUtils.getWorld()).getTemplateByName(name) == null){
                    FormationsDev.CHANNEL.sendToServer(new CreateTemplatePacket(Template.create(this.template.getArea(), name)));
                    ClientUtils.getMinecraft().gui.setScreen(null);
                }
            });
            this.saveButton.active = true;
            this.addWidget(this.saveButton);
        }
    }

    @Override
    public void renderBackground(WidgetRenderContext context, GuiGraphicsHelper graphics, int mouseX, int mouseY){
//        graphics.submitTexture(SCREEN_BACKGROUND, this.left(), this.top(), this.width, this.height); TODO
        super.renderBackground(context, graphics, mouseX, mouseY);
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public void renderForeground(WidgetRenderContext context, GuiGraphicsHelper graphics, int mouseX, int mouseY){
        super.renderForeground(context, graphics, mouseX, mouseY);
        AABB area = this.template == null ? new AABB(TemplateRenderer.selectionPos1).minmax(new AABB(TemplateRenderer.selectionPos2)) : this.template.getArea();
        Vec3 center = area.getCenter();
        // Title
        graphics.submitText(TextComponents.translation("formations.template.edit.new_template").get(), 72, 3, p -> p.shadow().activeColor().centerHorizontally());
        // Name label
        graphics.submitText(TextComponents.translation("formations.template.edit.name").get(), 32, 22, p -> p.centerHorizontally());
        // Location
        graphics.submitText(TextComponents.translation("formations.template.edit.location").get(), 32, 51, p -> p.centerHorizontally());
        graphics.submitText(TextComponents.translation("formations.template.edit.location.x").get(), 10, 63);
        graphics.submitText(TextComponents.translation("formations.template.edit.location.y").get(), 10, 74);
        graphics.submitText(TextComponents.translation("formations.template.edit.location.z").get(), 10, 85);
        Font font = ClientUtils.getFontRenderer();
        Component posX = TextComponents.number(center.x, 1).get();
        Component posY = TextComponents.number(center.y, 1).get();
        Component posZ = TextComponents.number(center.z, 1).get();
        graphics.submitText(posX, 132 - font.width(posX), 63);
        graphics.submitText(posY, 132 - font.width(posY), 74);
        graphics.submitText(posZ, 132 - font.width(posZ), 85);
        // Size
        graphics.submitText(TextComponents.translation("formations.template.edit.size").get(), 32, 101, p -> p.centerHorizontally());
        graphics.submitText(TextComponents.translation("formations.template.edit.size.x").get(), 10, 113);
        graphics.submitText(TextComponents.translation("formations.template.edit.size.y").get(), 10, 124);
        graphics.submitText(TextComponents.translation("formations.template.edit.size.z").get(), 10, 135);
        Component sizeX = TextComponents.number((int)area.getXsize()).get();
        Component sizeY = TextComponents.number((int)area.getYsize()).get();
        Component sizeZ = TextComponents.number((int)area.getZsize()).get();
        graphics.submitText(sizeX, 132 - font.width(sizeX), 113);
        graphics.submitText(sizeY, 132 - font.width(sizeY), 124);
        graphics.submitText(sizeZ, 132 - font.width(sizeZ), 135);
    }
}
