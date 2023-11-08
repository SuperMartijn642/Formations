package com.supermartijn642.formations.tools.loot_table;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.BaseWidget;
import com.supermartijn642.core.gui.widget.premade.TextFieldWidget;
import com.supermartijn642.core.registry.RegistryUtil;
import com.supermartijn642.formations.Formations;
import com.supermartijn642.formations.FormationsDev;
import com.supermartijn642.formations.tools.template.screen.TemplateEditButton;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/**
 * Created 27/08/2023 by SuperMartijn642
 */
public class LootTableEditingScreen extends BaseWidget {

    private static final ResourceLocation SCREEN_BACKGROUND = Formations.location("textures/gui/loot_table_editing_screen.png");

    private final BlockPos pos;
    private final ResourceLocation original;

    private TemplateEditButton saveButton;

    public LootTableEditingScreen(BlockPos pos, ResourceLocation originalLootTable){
        super(0, 0, 200, 66);
        this.pos = pos;
        this.original = originalLootTable;
    }

    @Override
    public Component getNarrationMessage(){
        return TextComponents.translation("formations.edit_loot.title").get();
    }

    @Override
    protected void addWidgets(){
        // Loot table field
        TextFieldWidget lootTableField = new TextFieldWidget(8, 32, 184, 12, this.original == null ? "" : this.original.toString(), 100, t -> this.saveButton.active = t.isBlank() || RegistryUtil.isValidIdentifier(t.trim()));
        lootTableField.setSuggestion(I18n.get("formations.edit_loot.loot_table_hint"));
        this.addWidget(lootTableField);
        // Cancel button
        this.addWidget(new TemplateEditButton(0, 51, 72, 15, TextComponents.translation("formations.edit_loot.cancel").get(), TemplateEditButton.LEFT_BUTTON, () -> {
            ClientUtils.getMinecraft().setScreen(null);
        }));
        // Save button
        this.saveButton = this.addWidget(new TemplateEditButton(128, 51, 72, 15, TextComponents.translation("formations.edit_loot.save").get(), TemplateEditButton.GREEN_RIGHT_BUTTON, () -> {
            String lootTableText = lootTableField.getText().trim();
            if(lootTableText.isEmpty() || RegistryUtil.isValidIdentifier(lootTableText)){
                ResourceLocation lootTable = lootTableText.isEmpty() ? null : new ResourceLocation(lootTableText);
                if(!Objects.equals(lootTable, this.original))
                    FormationsDev.CHANNEL.sendToServer(new SetLootTablePacket(this.pos, lootTable));
                ClientUtils.getMinecraft().setScreen(null);
            }
        }));
    }

    @Override
    public void renderBackground(PoseStack poseStack, int mouseX, int mouseY){
        ScreenUtils.bindTexture(SCREEN_BACKGROUND);
        ScreenUtils.drawTexture(poseStack, this.left(), this.top(), this.width, this.height, 0, 0, 1, 1);
        super.renderBackground(poseStack, mouseX, mouseY);
    }

    @Override
    public void renderForeground(PoseStack poseStack, int mouseX, int mouseY){
        super.renderForeground(poseStack, mouseX, mouseY);
        // Title
        ScreenUtils.drawCenteredStringWithShadow(poseStack, TextComponents.translation("formations.edit_loot.title").get(), 100, 3, ScreenUtils.ACTIVE_TEXT_COLOR);
        // Loot table field label
        ScreenUtils.drawCenteredString(poseStack, TextComponents.translation("formations.edit_loot.loot_table_hint").get(), 35, 22);
    }
}
