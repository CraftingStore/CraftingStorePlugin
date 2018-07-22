package net.craftingstore.bukkit.commands;

import net.craftingstore.Category;
import net.craftingstore.bukkit.CraftingStoreBukkit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.logging.Level;

public class BuyCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Check if we're not console.
        if (!(sender instanceof Player)) {
            sender.sendMessage(CraftingStoreBukkit.getInstance().prefix + "This command may only be executed from in-game!");
            return false;
        }

        // Get all categories.
        Category categories[] = CraftingStoreBukkit.getInstance().getQueryCache().getCategories();

        // Pre-define rows
        Integer inventorySlots = 9;

        Integer categoryCount = categories.length;
        while(categoryCount > inventorySlots) {
            inventorySlots = inventorySlots + 9;
        }

        // Create inventory
        Inventory categoriesInventory = Bukkit.createInventory(null, inventorySlots, CraftingStoreBukkit.getInstance().getConfig().getString("gui-prefix") + ": Categories");

        // Get player
        Player player = (Player) sender;

        Integer loop = 0;

        // Walk though categories to build inventory
        for (Category category : categories) {

            // Only show main categories.
            if (category.isSubCategory()) {
                continue;
            }

            // Get material by name.
            Material material = Material.CHEST;

            try {
                material = category.getMinecraftIconName() == null ? Material.CHEST : Material.valueOf(category.getMinecraftIconName());
            } catch (IllegalArgumentException el) {
                // Error in name, using the default instead.
            }

            // Set item meta.
            ItemStack item = new ItemStack(material, 1);
            ItemMeta im = item.getItemMeta();
            im.setDisplayName(category.getName());
            item.setItemMeta(im);

            categoriesInventory.setItem(loop, item);
            loop++;
        }

        // Add inventory to our listener & open for player.
        CraftingStoreBukkit.getInstance().getQueryCache().addInventory(categoriesInventory.getName());
        if (CraftingStoreBukkit.getInstance().getDebug()) {
            CraftingStoreBukkit.getInstance().getLogger().log(Level.INFO, "Added inventory to our storage. Name: " + categoriesInventory.getName());
        }
        player.openInventory(categoriesInventory);

        return true;

    }

}
