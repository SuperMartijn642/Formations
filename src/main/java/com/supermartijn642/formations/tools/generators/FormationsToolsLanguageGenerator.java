package com.supermartijn642.formations.tools.generators;

import com.supermartijn642.core.generator.LanguageGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.formations.Formations;
import com.supermartijn642.formations.FormationsDev;

/**
 * Created 25/08/2023 by SuperMartijn642
 */
public class FormationsToolsLanguageGenerator extends LanguageGenerator {

    public FormationsToolsLanguageGenerator(ResourceCache cache){
        super(Formations.MODID, cache, "en_us");
    }

    @Override
    public void generate(){
        this.itemGroup(FormationsDev.TOOLS_ITEM_GROUP, "Formations Development Tools");
        // Template editor item
        this.item(FormationsDev.templateEditorItem, "Template Staff");
        this.translation("formations.template_editor_item.left_click_block", "%s: Select first position");
        this.translation("formations.template_editor_item.right_click_block", "%s: Select second position");
        this.translation("formations.template_editor_item.shift_right_click", "%s + %s: Create template");
        this.translation("formations.template_editor_item.left_click", "%s on template: Edit template");
        // Template edit screen
        this.translation("formations.template.edit.edit_template", "Edit Template");
        this.translation("formations.template.edit.new_template", "New Template");
        this.translation("formations.template.edit.name", "Name");
        this.translation("formations.template.edit.name_hint", "Template name");
        this.translation("formations.template.edit.location", "Location");
        this.translation("formations.template.edit.location.x", "x-position:");
        this.translation("formations.template.edit.location.y", "y-position:");
        this.translation("formations.template.edit.location.z", "z-position:");
        this.translation("formations.template.edit.size", "Size");
        this.translation("formations.template.edit.size.x", "x-axis:");
        this.translation("formations.template.edit.size.y", "y-axis:");
        this.translation("formations.template.edit.size.z", "z-axis:");
        this.translation("formations.template.edit.cancel", "Cancel");
        this.translation("formations.template.edit.delete", "Delete");
        this.translation("formations.template.edit.confirm", "Confirm");
        this.translation("formations.template.edit.save", "Save");
        this.translation("formations.template.edit.no_selection", "Please select an area");
        // Export command
        this.translation("formations.command.export.success", "Successfully exported %s templates");
        this.translation("formations.command.export.success_fail", "Exported %1$s templates, %2$s templates failed to export. Please see the console for errors!");
        this.translation("formations.command.export.fail", "%1$s templates failed to export. Please see the console for errors!");
        this.translation("formations.command.export.no_templates", "There are no templates in this world!");
        // Dev mode command
        this.translation("formations.command.dev_mode.query", "Formations development mode: %s");
        this.translation("formations.command.dev_mode.set", "Formations development mode has been set to: %s");
        this.translation("formations.command.dev_mode.true", "Enabled");
        this.translation("formations.command.dev_mode.false", "Disabled");
        // Loot table editing
        this.translation("formations.edit_loot.title", "Edit Loot Table");
        this.translation("formations.edit_loot.loot_table_hint", "Loot table");
        this.translation("formations.edit_loot.cancel", "Cancel");
        this.translation("formations.edit_loot.save", "Save");
    }
}
