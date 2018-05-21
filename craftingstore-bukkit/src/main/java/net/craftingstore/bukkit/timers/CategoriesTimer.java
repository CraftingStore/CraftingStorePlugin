package net.craftingstore.bukkit.timers;

import net.craftingstore.CraftingStoreAPI;
import net.craftingstore.bukkit.CraftingStoreBukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public class CategoriesTimer extends BukkitRunnable {

    private CraftingStoreBukkit instance;

    public CategoriesTimer(CraftingStoreBukkit instance) {
        this.instance = instance;
    }

    @Override
    public void run() {
        try {
            instance.getQueryCache().setCategories(CraftingStoreAPI.getInstance().getCategories(instance.getKey()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
