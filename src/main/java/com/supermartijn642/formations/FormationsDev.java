package com.supermartijn642.formations;

import com.supermartijn642.core.item.CreativeItemGroup;
import com.supermartijn642.core.item.ItemProperties;
import com.supermartijn642.core.item.ItemRarity;
import com.supermartijn642.core.network.PacketChannel;
import com.supermartijn642.core.registry.GeneratorRegistrationHandler;
import com.supermartijn642.core.registry.RegistrationHandler;
import com.supermartijn642.core.registry.RegistryEntryAcceptor;
import com.supermartijn642.formations.tools.FormationsCommand;
import com.supermartijn642.formations.tools.FormationsLevelData;
import com.supermartijn642.formations.tools.SyncFormationsLevelDataPacket;
import com.supermartijn642.formations.tools.generators.FormationsToolsLanguageGenerator;
import com.supermartijn642.formations.tools.generators.FormationsToolsModelGenerator;
import com.supermartijn642.formations.tools.loot_table.ContainerOpenIntercept;
import com.supermartijn642.formations.tools.loot_table.OpenLootTableScreenPacket;
import com.supermartijn642.formations.tools.loot_table.SetLootTablePacket;
import com.supermartijn642.formations.tools.template.TemplateEditorItem;
import com.supermartijn642.formations.tools.template.TemplateManager;
import com.supermartijn642.formations.tools.template.packets.AllTemplatesPacket;
import com.supermartijn642.formations.tools.template.packets.CreateTemplatePacket;
import com.supermartijn642.formations.tools.template.packets.DeleteTemplatePacket;

/**
 * Created 03/10/2023 by SuperMartijn642
 */
public class FormationsDev {

    public static final PacketChannel CHANNEL = PacketChannel.create(Formations.MODID);

    @RegistryEntryAcceptor(namespace = Formations.MODID, identifier = "template_editor", registry = RegistryEntryAcceptor.Registry.ITEMS)
    public static TemplateEditorItem templateEditorItem;

    public static final CreativeItemGroup TOOLS_ITEM_GROUP = CreativeItemGroup.create(Formations.MODID, () -> templateEditorItem);

    public static void initDevTools(){
        CHANNEL.registerMessage(AllTemplatesPacket.class, AllTemplatesPacket::new, true);
        CHANNEL.registerMessage(CreateTemplatePacket.class, CreateTemplatePacket::new, true);
        CHANNEL.registerMessage(DeleteTemplatePacket.class, DeleteTemplatePacket::new, true);
        CHANNEL.registerMessage(OpenLootTableScreenPacket.class, OpenLootTableScreenPacket::new, true);
        CHANNEL.registerMessage(SetLootTablePacket.class, SetLootTablePacket::new, true);
        CHANNEL.registerMessage(SyncFormationsLevelDataPacket.class, SyncFormationsLevelDataPacket::new, true);
        FormationsLevelData.registerListeners();
        TemplateManager.registerListeners();
        ContainerOpenIntercept.registerListeners();

        register();
        registerGenerators();
    }

    private static void register(){
        RegistrationHandler.get(Formations.MODID).registerItem("template_editor", () -> templateEditorItem = new TemplateEditorItem(ItemProperties.create().group(TOOLS_ITEM_GROUP).rarity(ItemRarity.EPIC)));
        FormationsCommand.register();
    }

    private static void registerGenerators(){
        GeneratorRegistrationHandler handler = GeneratorRegistrationHandler.get(Formations.MODID);
        handler.addGenerator(FormationsToolsModelGenerator::new);
        handler.addGenerator(FormationsToolsLanguageGenerator::new);
    }
}
