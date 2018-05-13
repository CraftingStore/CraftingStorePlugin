package net.craftingstore.bungee.commands;

import net.craftingstore.bungee.CraftingStoreBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class CraftingStoreCommand extends Command {

    public CraftingStoreCommand() {
        super("csb", "craftingstore.admin");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

        // Reload command
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {

            CraftingStoreBungee.getInstance().refreshKey(); // update key.
            commandSender.sendMessage(CraftingStoreBungee.getInstance().prefix + "The plugin has been reloaded!");
            return;

            // Set key command.
        } else if (args.length == 2 && args[0].equalsIgnoreCase("key")) {

            // Set new key & update config.
            CraftingStoreBungee.getInstance().getConfig().set("api-key", args[1]);
            CraftingStoreBungee.getInstance().saveConfig();

            commandSender.sendMessage(CraftingStoreBungee.getInstance().prefix + "The key has been set, we'll check if it's valid now, please look in the console.");
            CraftingStoreBungee.getInstance().refreshKey();

            return;
        }

        commandSender.sendMessage(ChatColor.GRAY + "" +ChatColor.STRIKETHROUGH + "-----------------------");
        commandSender.sendMessage(ChatColor.DARK_GRAY + ">" + ChatColor.GRAY + " /cs reload" + ChatColor.DARK_GRAY + " -> " + ChatColor.GRAY + "Reload the config.");
        commandSender.sendMessage(ChatColor.DARK_GRAY + ">" + ChatColor.GRAY + " /cs key <your key>" + ChatColor.DARK_GRAY + " -> " + ChatColor.GRAY + "Update the key.");
        commandSender.sendMessage(ChatColor.GRAY + "" +ChatColor.STRIKETHROUGH + "-----------------------");
    }
}
