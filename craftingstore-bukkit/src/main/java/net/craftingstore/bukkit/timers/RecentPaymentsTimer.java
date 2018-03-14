package net.craftingstore.bukkit.timers;

import net.craftingstore.CraftingStoreAPI;
import net.craftingstore.bukkit.CraftingStoreBukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class RecentPaymentsTimer extends BukkitRunnable {

    private CraftingStoreBukkit instance;

    public RecentPaymentsTimer(CraftingStoreBukkit instance) {
        this.instance = instance;
    }

    @Override
    public void run() {
        try {
            instance.getQueryCache().setRecentPayments(CraftingStoreAPI.getInstance().getPayments(instance.getKey()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
