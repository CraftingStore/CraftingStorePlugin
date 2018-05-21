package net.craftingstore.bukkit.timers;

import net.craftingstore.CraftingStoreAPI;
import net.craftingstore.bukkit.CraftingStoreBukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class PackagesTimer extends BukkitRunnable {

    private CraftingStoreBukkit instance;

    public PackagesTimer(CraftingStoreBukkit instance) {
        this.instance = instance;
    }

    @Override
    public void run() {
        try {
            //instance.getQueryCache().setPackages(CraftingStoreAPI.getInstance().getPackages(instance.getKey()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
