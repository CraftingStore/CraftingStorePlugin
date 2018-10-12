package net.craftingstore.bungee.timers;

import net.craftingstore.bungee.CraftingStoreBungee;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class SocketCheckTimer implements Runnable {

    private Plugin instance;

    public SocketCheckTimer(Plugin instance) {
        this.instance = instance;
    }

    public void run() {
        try {

            boolean actionUpdate = CraftingStoreBungee.getInstance().getWebSocketUtils().getActionUpdate();
            boolean actionReload = CraftingStoreBungee.getInstance().getWebSocketUtils().getActionReload();
            boolean actionPolling = CraftingStoreBungee.getInstance().getWebSocketUtils().getActionPolling();
            boolean isConnected = CraftingStoreBungee.getInstance().getWebSocketUtils().getConnected();

            // Update (Donation received)
            if (actionUpdate) {
                // Donation event received, update commands.
                CraftingStoreBungee.getInstance().getProxy().getScheduler().schedule(CraftingStoreBungee.getInstance(), new DonationCheckTimer(CraftingStoreBungee.getInstance()), 1, TimeUnit.SECONDS);

                if (CraftingStoreBungee.getInstance().getDebug()) {
                    CraftingStoreBungee.getInstance().getLogger().log(Level.INFO, "Donation update request received though socket.");
                }
            }

            // Reload plugin
            if (actionReload) {
                if (CraftingStoreBungee.getInstance().getDebug()) {
                    CraftingStoreBungee.getInstance().getLogger().log(Level.INFO, "Reloading plugin.");
                }

                CraftingStoreBungee.getInstance().refreshKey();
            }

            // Enable polling.
            if (actionPolling || !isConnected) {
                if (CraftingStoreBungee.getInstance().getDebug()) {
                    CraftingStoreBungee.getInstance().getLogger().log(Level.INFO, "Switching to polling.");
                }

                CraftingStoreBungee.getInstance().getWebSocketUtils().disconnect();
                CraftingStoreBungee.getInstance().startTimers(CraftingStoreBungee.getInstance().getIntervalDonationTimer());

                // Try to reload/re-connect after 10 mins.
                CraftingStoreBungee.getInstance().getProxy().getScheduler().schedule(CraftingStoreBungee.getInstance(), new ReloadPluginTimer(CraftingStoreBungee.getInstance()), 600, TimeUnit.SECONDS);

            }

        } catch (Exception e) {
            // Error.
        }
    }

}
