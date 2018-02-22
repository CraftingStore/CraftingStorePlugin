package net.craftingstore.bukkit.commands;

import net.craftingstore.bukkit.CraftingStoreBukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class CraftingStoreCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender.hasPermission("craftingstore.admin")) {

            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {

                CraftingStoreBukkit.getInstance().refreshKey(); // update key.
                sender.sendMessage("[CraftingStore] The plugin has been reloaded!");
                return true;

            }

            sender.sendMessage("--[CraftingStore]--");
            sender.sendMessage("> /cs reload -> Reload the config.");
            return true;

        }

        sender.sendMessage("[CraftingStore] You don't have the required permission!");
        return true;
    }

}
