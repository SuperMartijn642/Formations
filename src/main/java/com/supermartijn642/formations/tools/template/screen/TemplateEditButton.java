package com.supermartijn642.formations.tools.template.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.premade.AbstractButtonWidget;
import com.supermartijn642.formations.Formations;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Created 25/08/2023 by SuperMartijn642
 */
public class TemplateEditButton extends AbstractButtonWidget {

    public static final ResourceLocation LEFT_BUTTON = Formations.location("textures/gui/template_screen_left_button.png");
    public static final ResourceLocation RED_LEFT_BUTTON = Formations.location("textures/gui/template_delete_button.png");
    public static final ResourceLocation RIGHT_BUTTON = Formations.location("textures/gui/template_screen_right_button.png");
    public static final ResourceLocation GREEN_RIGHT_BUTTON = Formations.location("textures/gui/template_save_button.png");

    private final Component text;
    private final ResourceLocation background;
    public boolean active = true;

    public TemplateEditButton(int x, int y, int width, int height, Component text, ResourceLocation background, Runnable onPress){
        super(x, y, width, height, onPress);
        this.text = text;
        this.background = background;
    }

    @Override
    public Component getNarrationMessage(){
        return this.text;
    }

    @Override
    public void renderBackground(PoseStack poseStack, int mouseX, int mouseY){
        ScreenUtils.bindTexture(this.background);
        ScreenUtils.drawTexture(poseStack, this.x, this.y, this.width, this.height, 0, this.active ? this.isFocused() ? 1 / 3f : 0 : 2 / 3f, 1, 1 / 3f);
        super.renderBackground(poseStack, mouseX, mouseY);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY){
        ScreenUtils.drawCenteredString(poseStack, this.text, this.x + this.width / 2f, this.y + 3, this.active ? ScreenUtils.DEFAULT_TEXT_COLOR : ScreenUtils.INACTIVE_TEXT_COLOR);
        super.render(poseStack, mouseX, mouseY);
    }

    @Override
    public void onPress(){
        if(this.active)
            super.onPress();
    }
}
