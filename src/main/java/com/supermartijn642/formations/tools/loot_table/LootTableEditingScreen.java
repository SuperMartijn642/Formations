package com.supermartijn642.formations.tools.loot_table;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.GuiGraphicsHelper;
import com.supermartijn642.core.gui.widget.BaseWidget;
import com.supermartijn642.core.gui.widget.WidgetRenderContext;
import com.supermartijn642.core.gui.widget.premade.TextFieldWidget;
import com.supermartijn642.core.registry.RegistryUtil;
import com.supermartijn642.formations.Formations;
import com.supermartijn642.formations.FormationsDev;
import com.supermartijn642.formations.tools.template.screen.TemplateEditButton;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Objects;

/**
 * Created 27/08/2023 by SuperMartijn642
 */
public class LootTableEditingScreen extends BaseWidget {

    private static final Identifier SCREEN_BACKGROUND = Formations.location("textures/gui/loot_table_editing_screen.png");

    private final BlockPos pos;
    private final Identifier original;

    private TemplateEditButton saveButton;

    public LootTableEditingScreen(BlockPos pos, Identifier originalLootTable){
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
                Identifier lootTable = lootTableText.isEmpty() ? null : Identifier.parse(lootTableText);
                if(!Objects.equals(lootTable, this.original))
                    FormationsDev.CHANNEL.sendToServer(new SetLootTablePacket(this.pos, lootTable));
                ClientUtils.getMinecraft().setScreen(null);
            }
        }));
    }

    @Override
    public void renderBackground(WidgetRenderContext context, GuiGraphicsHelper graphics, int mouseX, int mouseY){
        graphics.submitTexture(SCREEN_BACKGROUND, this.left(), this.top(), this.width, this.height);
        super.renderBackground(context, graphics, mouseX, mouseY);
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public void renderForeground(WidgetRenderContext context, GuiGraphicsHelper graphics, int mouseX, int mouseY){
        super.renderForeground(context, graphics, mouseX, mouseY);
        // Title
        graphics.submitText(TextComponents.translation("formations.edit_loot.title").get(), 100, 3, p -> p.shadow().activeColor().centerHorizontally());
        // Loot table field label
        graphics.submitText(TextComponents.translation("formations.edit_loot.loot_table_hint").get(), 35, 22, p -> p.centerHorizontally());
    }
}
