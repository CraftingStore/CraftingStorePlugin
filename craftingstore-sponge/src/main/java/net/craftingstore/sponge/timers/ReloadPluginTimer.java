package net.craftingstore.sponge.timers;

import net.craftingstore.sponge.CraftingStoreSponge;

public class ReloadPluginTimer implements Runnable {

    private CraftingStoreSponge instance;

    public ReloadPluginTimer(CraftingStoreSponge instance) {
        this.instance = instance;
    }

    public void run() {

        try {

            if (CraftingStoreSponge.getInstance().getDebug()) {
                CraftingStoreSponge.getInstance().getLogger().info("Reloading plugin by timer.");
            }

            CraftingStoreSponge.getInstance().getSocket();
            CraftingStoreSponge.getInstance().connectToSocket();
            CraftingStoreSponge.getInstance().startTimers(CraftingStoreSponge.getInstance().getIntervalDonationTimer());

        } catch (Exception e) {
            instance.getLogger().error("An error occurred while checking for donations.", e);
        }
    }

}
