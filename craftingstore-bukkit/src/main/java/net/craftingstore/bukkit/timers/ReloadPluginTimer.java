package net.craftingstore.bukkit.timers;

import net.craftingstore.bukkit.CraftingStoreBukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public class ReloadPluginTimer extends BukkitRunnable {

    private Plugin instance;

    public ReloadPluginTimer(Plugin instance) {
        this.instance = instance;
    }

    public void run() {
        try {
            if (CraftingStoreBukkit.getInstance().getDebug()) {
                CraftingStoreBukkit.getInstance().getLogger().log(Level.INFO, "Reloading plugin by timer.");
            }

            // Reload plugin.
            CraftingStoreBukkit.getInstance().getSocket();
            CraftingStoreBukkit.getInstance().connectToSocket();
            CraftingStoreBukkit.getInstance().startTimers(CraftingStoreBukkit.getInstance().getIntervalDonationTimer(), CraftingStoreBukkit.getInstance().getIntervalOtherTimers());

        } catch (Exception e) {
            instance.getLogger().log(Level.SEVERE, "An error occurred while checking for donations.", e);
        }
    }

}
