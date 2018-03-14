package net.craftingstore.bukkit.timers;

import net.craftingstore.CraftingStoreAPI;
import net.craftingstore.bukkit.CraftingStoreBukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class TopDonatorTimer extends BukkitRunnable {

    private CraftingStoreBukkit instance;

    public TopDonatorTimer(CraftingStoreBukkit instance) {
        this.instance = instance;
    }

    @Override
    public void run() {
        try {
            instance.getQueryCache().setTopDonators(CraftingStoreAPI.getInstance().getTopDonators(instance.getKey()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
