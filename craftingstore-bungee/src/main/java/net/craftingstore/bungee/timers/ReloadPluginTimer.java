package net.craftingstore.bungee.timers;

import net.craftingstore.bungee.CraftingStoreBungee;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.logging.Level;

public class ReloadPluginTimer implements Runnable {

    private Plugin instance;

    public ReloadPluginTimer(Plugin instance) {
        this.instance = instance;
    }

    public void run() {
        try {

            if (CraftingStoreBungee.getInstance().getDebug()) {
                CraftingStoreBungee.getInstance().getLogger().log(Level.INFO, "Reloading plugin by timer.");
            }

            CraftingStoreBungee.getInstance().getSocket();
            CraftingStoreBungee.getInstance().connectToSocket();
            CraftingStoreBungee.getInstance().startTimers(CraftingStoreBungee.getInstance().getIntervalDonationTimer());

        } catch (Exception e) {
            // Error.
        }
    }

}
