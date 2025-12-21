package com.supermartijn642.formations.tools.template.screen;

import com.supermartijn642.core.gui.GuiGraphicsHelper;
import com.supermartijn642.core.gui.widget.WidgetRenderContext;
import com.supermartijn642.core.gui.widget.premade.AbstractButtonWidget;
import com.supermartijn642.formations.Formations;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * Created 25/08/2023 by SuperMartijn642
 */
public class TemplateEditButton extends AbstractButtonWidget {

    public static final Identifier LEFT_BUTTON = Formations.location("textures/gui/template_screen_left_button.png");
    public static final Identifier RED_LEFT_BUTTON = Formations.location("textures/gui/template_delete_button.png");
    public static final Identifier RIGHT_BUTTON = Formations.location("textures/gui/template_screen_right_button.png");
    public static final Identifier GREEN_RIGHT_BUTTON = Formations.location("textures/gui/template_save_button.png");

    private final Component text;
    private final Identifier background;
    public boolean active = true;

    public TemplateEditButton(int x, int y, int width, int height, Component text, Identifier background, Runnable onPress){
        super(x, y, width, height, onPress);
        this.text = text;
        this.background = background;
    }

    @Override
    public Component getNarrationMessage(){
        return this.text;
    }

    @Override
    public void renderBackground(WidgetRenderContext context, GuiGraphicsHelper graphics, int mouseX, int mouseY){
//        graphics.submitTexture(this.background, this.x, this.y, this.width, this.height, p -> p.uv(0, this.active ? this.isFocused() ? 1 / 3f : 0 : 2 / 3f, 1, 1 / 3f)); TODO
        super.renderBackground(context, graphics, mouseX, mouseY);
    }

    @Override
    public void render(WidgetRenderContext context, GuiGraphicsHelper graphics, int mouseX, int mouseY){
        graphics.submitText(this.text, this.x + this.width / 2f, this.y + 3, p -> (this.active ? p.defaultColor() : p.inactiveColor()).centerHorizontally());
        super.render(context, graphics, mouseX, mouseY);
    }

    @Override
    public void onPress(){
        if(this.active)
            super.onPress();
    }
}
