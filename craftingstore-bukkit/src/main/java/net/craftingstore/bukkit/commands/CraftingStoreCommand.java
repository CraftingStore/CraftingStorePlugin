package net.craftingstore.bukkit.commands;

import net.craftingstore.bukkit.CraftingStoreBukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CraftingStoreCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender.hasPermission("craftingstore.admin")) {

            // Reload command
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {

                CraftingStoreBukkit.getInstance().refreshKey(); // update key.
                sender.sendMessage(CraftingStoreBukkit.getInstance().prefix + "The plugin has been reloaded!");
                return true;

            // Set key command.
            } else if (args.length == 2 && args[0].equalsIgnoreCase("key")) {

                // Set new key & update config.
                CraftingStoreBukkit.getInstance().getConfig().set("api-key", args[1]);
                CraftingStoreBukkit.getInstance().saveConfig();

                sender.sendMessage(CraftingStoreBukkit.getInstance().prefix + "The key has been set, we'll check if it's valid now, please look in the console.");
                CraftingStoreBukkit.getInstance().refreshKey();

                return true;
            }

            sender.sendMessage(ChatColor.GRAY + "" +ChatColor.STRIKETHROUGH + "-----------------------");
            sender.sendMessage(ChatColor.DARK_GRAY + ">" + ChatColor.GRAY + " /cs reload" + ChatColor.DARK_GRAY + " -> " + ChatColor.GRAY + "Reload the config.");
            sender.sendMessage(ChatColor.DARK_GRAY + ">" + ChatColor.GRAY + " /cs key <your key>" + ChatColor.DARK_GRAY + " -> " + ChatColor.GRAY + "Update the key.");
            sender.sendMessage(ChatColor.GRAY + "" +ChatColor.STRIKETHROUGH + "-----------------------");

            return true;

        }

        sender.sendMessage(CraftingStoreBukkit.getInstance().prefix + "You don't have the required permission!");
        return true;
    }

}
