package net.craftingstore.sponge.commands;

import net.craftingstore.sponge.CraftingStoreSponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class CraftingStoreCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Optional<String> argumentOne = args.getOne("arg1");
        Optional<String> argumentTwo = args.getOne("arg2");


        // Reload command
        if (argumentOne.isPresent() && argumentOne.get().equalsIgnoreCase("reload")) {

            CraftingStoreSponge.getInstance().refreshKey(); // update key.
            src.sendMessage(CraftingStoreSponge.getInstance().prefix.append(Text.builder("The plugin has been reloaded.").color(TextColors.WHITE).build()).build());

            return CommandResult.success();

        // Set key command.
        } else if (argumentOne.isPresent() && argumentTwo.isPresent() && argumentOne.get().equalsIgnoreCase("key")) {

            // Set new key & update config.
            CraftingStoreSponge.getInstance().getConfig().getConfig().getNode("api-key").setValue(argumentTwo.get());
            CraftingStoreSponge.getInstance().getConfig().saveConfig();

            src.sendMessage(CraftingStoreSponge.getInstance().prefix.append(Text.builder("The key has been set, we'll check if it's valid now, please look in the console.").color(TextColors.WHITE).build()).build());
            CraftingStoreSponge.getInstance().refreshKey(); // update key.

            return CommandResult.success();
        }

        // Show help
        src.sendMessage(Text.builder("-----------------------").color(TextColors.GOLD).build());
        src.sendMessage(Text.builder("> ").color(TextColors.DARK_GRAY)
                .append(Text.builder("/cs reload").color(TextColors.RED).build())
                .append(Text.builder(" -> ").color(TextColors.DARK_GRAY).build())
                .append(Text.builder("Reload the config.").color(TextColors.GRAY).build())
                .build());

        src.sendMessage(Text.builder("> ").color(TextColors.DARK_GRAY)
                .append(Text.builder("/cs key <your key>").color(TextColors.RED).build())
                .append(Text.builder(" -> ").color(TextColors.DARK_GRAY).build())
                .append(Text.builder("Update your key.").color(TextColors.GRAY).build())
                .build());
        src.sendMessage(Text.builder("-----------------------").color(TextColors.GOLD).build());

        return CommandResult.success();
    }
}