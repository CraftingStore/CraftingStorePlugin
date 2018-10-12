package net.craftingstore.bukkit.timers;

import net.craftingstore.bukkit.CraftingStoreBukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public class SocketCheckTimer extends BukkitRunnable {

    private Plugin instance;

    public SocketCheckTimer(Plugin instance) {
        this.instance = instance;
    }

    public void run() {
        try {

            if (CraftingStoreBukkit.getInstance().getDebug()) {
                CraftingStoreBukkit.getInstance().getLogger().log(Level.INFO, "Checking for updates on the socket server.");
            }

            boolean actionUpdate = CraftingStoreBukkit.getInstance().getWebSocketUtils().getActionUpdate();
            boolean actionReload = CraftingStoreBukkit.getInstance().getWebSocketUtils().getActionReload();
            boolean actionPolling = CraftingStoreBukkit.getInstance().getWebSocketUtils().getActionPolling();
            boolean isConnected = CraftingStoreBukkit.getInstance().getWebSocketUtils().getConnected();

            // Update (Donation received)
            if (actionUpdate) {
                if (CraftingStoreBukkit.getInstance().getDebug()) {
                    CraftingStoreBukkit.getInstance().getLogger().log(Level.INFO, "Processing donation request.");
                }

                new DonationCheckTimer(CraftingStoreBukkit.getInstance()).runTaskAsynchronously(CraftingStoreBukkit.getInstance());
                new TopDonatorTimer(CraftingStoreBukkit.getInstance()).runTaskAsynchronously(CraftingStoreBukkit.getInstance());
                new RecentPaymentsTimer(CraftingStoreBukkit.getInstance()).runTaskAsynchronously(CraftingStoreBukkit.getInstance());
            }

            // Reload plugin
            if (actionReload) {
                if (CraftingStoreBukkit.getInstance().getDebug()) {
                    CraftingStoreBukkit.getInstance().getLogger().log(Level.INFO, "Reloading plugin.");
                }

                CraftingStoreBukkit.getInstance().refreshKey();
            }

            // Enable polling.
            if (actionPolling || !isConnected) {
                if (CraftingStoreBukkit.getInstance().getDebug()) {
                    CraftingStoreBukkit.getInstance().getLogger().log(Level.INFO, "Switching to polling.");
                }

                CraftingStoreBukkit.getInstance().getWebSocketUtils().disconnect();
                CraftingStoreBukkit.getInstance().setSocketEnabled(false);
                CraftingStoreBukkit.getInstance().startTimers(CraftingStoreBukkit.getInstance().getIntervalDonationTimer(), CraftingStoreBukkit.getInstance().getIntervalOtherTimers());

                new ReloadPluginTimer(CraftingStoreBukkit.getInstance()).runTaskLater(CraftingStoreBukkit.getInstance(), 600 * 20); // Try to reload & re-connect in 5 min. (600 sec)
            }

        } catch (Exception e) {
            instance.getLogger().log(Level.SEVERE, "An error occurred while checking for donations.", e);
        }
    }

}
