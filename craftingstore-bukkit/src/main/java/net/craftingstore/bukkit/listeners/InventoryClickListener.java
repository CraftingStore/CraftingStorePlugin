package net.craftingstore.bukkit.listeners;

import net.craftingstore.Category;
import net.craftingstore.Package;
import net.craftingstore.bukkit.CraftingStoreBukkit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        // Check if a player clicked.
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) e.getWhoClicked();
        Inventory inventory = e.getInventory();
        ItemStack itemClicked = e.getCurrentItem();

        // Ignore if it's not our inventory.
        if (!inventory.getTitle().equalsIgnoreCase("Store Categories")) {
            return;
        }

        if (itemClicked == null || !itemClicked.hasItemMeta()) {
            return;
        }

        // Make sure that the items stays locked.
        e.setCancelled(true);

        // Get all categories.
        Category categories[] = CraftingStoreBukkit.getInstance().getQueryCache().getCategories();

        // Pre-define rows
        Integer inventorySlots = 9;

        // Set boolean if inventory is ready.
        Inventory packagesInventory = null;

        for (Category category : categories) {
            
            if (!itemClicked.getItemMeta().getDisplayName().equals(category.getName())) {
                continue;
            }

            // Get packages
            Package packages[] = category.getpackages();

            Integer packageCount = packages.length;
            while (packageCount > inventorySlots) {
                inventorySlots = inventorySlots + 9;
            }

            // Create inventory
            packagesInventory = Bukkit.createInventory(null, inventorySlots, "Category " + category.getName());

            Integer loop = 0;
            for (Package packageItem : packages) {

                // Get material
                Material material = Material.getMaterial(packageItem.getMinecraftIconName());
                if (material == null) {
                    material = Material.DIRT;
                }

                // Set item meta.
                ItemStack item = new ItemStack(material, 1);
                ItemMeta im = item.getItemMeta();
                im.setDisplayName(packageItem.getName());
                item.setItemMeta(im);

                packagesInventory.setItem(loop, item);
                loop++;
            }
        }

        // Check if the inventory is created.
        if (packagesInventory == null) {
            return;
        }

        // Close old inventory.
        player.closeInventory();

        // Open inventory!
        player.openInventory(packagesInventory);
    }

}
