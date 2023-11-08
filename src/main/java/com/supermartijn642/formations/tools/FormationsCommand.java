package com.supermartijn642.formations.tools;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.util.Pair;
import com.supermartijn642.formations.tools.template.TemplateManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;

import java.util.function.Consumer;

/**
 * Created 27/08/2023 by SuperMartijn642
 */
public class FormationsCommand {

    public static void register(){
        MinecraftForge.EVENT_BUS.addListener((Consumer<RegisterCommandsEvent>)event ->
            event.getDispatcher().register(
                Commands.literal("formations")
                    .requires(source -> source.hasPermission(Commands.LEVEL_ADMINS))
                    .then(registerExportTemplates())
                    .then(registerDevMode())
            )
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> registerExportTemplates(){
        return Commands.literal("export")
            .requires(source -> source.hasPermission(Commands.LEVEL_ADMINS) && source.isPlayer())
            .executes(context -> {
                CommandSourceStack source = context.getSource();
                Pair<Integer,Integer> successes = TemplateManager.get(source.getLevel()).exportAll();
                if(successes.left() > 0 && successes.right() > 0)
                    source.sendFailure(TextComponents.translation("formations.command.export.success_fail", successes.left()).get());
                else if(successes.left() > 0)
                    source.sendSuccess(TextComponents.translation("formations.command.export.success", successes.left(), successes.right()).get(), true);
                else if(successes.right() > 0)
                    source.sendFailure(TextComponents.translation("formations.command.export.fail", successes.right()).get());
                else
                    source.sendFailure(TextComponents.translation("formations.command.export.no_templates").get());
                return Command.SINGLE_SUCCESS;
            });
    }

    private static LiteralArgumentBuilder<CommandSourceStack> registerDevMode(){
        return Commands.literal("dev_mode")
            .executes(context -> {
                context.getSource().sendSuccess(TextComponents.translation("formations.command.dev_mode.query", TextComponents.translation("formations.command.dev_mode." + (FormationsLevelData.SERVER.isDevMode() ? "true" : "false")).color(FormationsLevelData.SERVER.isDevMode() ? ChatFormatting.GREEN : ChatFormatting.RED).get()).get(), false);
                return Command.SINGLE_SUCCESS;
            })
            .then(
                Commands.argument("enabled", BoolArgumentType.bool())
                    .executes(context -> {
                        FormationsLevelData.SERVER.setDevMode(BoolArgumentType.getBool(context, "enabled"));
                        context.getSource().sendSuccess(TextComponents.translation("formations.command.dev_mode.set", TextComponents.translation("formations.command.dev_mode." + (FormationsLevelData.SERVER.isDevMode() ? "true" : "false")).color(FormationsLevelData.SERVER.isDevMode() ? ChatFormatting.GREEN : ChatFormatting.RED).get()).get(), false);
                        return Command.SINGLE_SUCCESS;
                    })
            );
    }
}
