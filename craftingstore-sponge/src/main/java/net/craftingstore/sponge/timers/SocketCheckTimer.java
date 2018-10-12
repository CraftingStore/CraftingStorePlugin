package net.craftingstore.sponge.timers;

import net.craftingstore.sponge.CraftingStoreSponge;
import org.spongepowered.api.Sponge;

public class SocketCheckTimer implements Runnable {

    private CraftingStoreSponge instance;

    public SocketCheckTimer(CraftingStoreSponge instance) {
        this.instance = instance;
    }

    public void run() {

        try {

            boolean actionUpdate = CraftingStoreSponge.getInstance().getWebSocketUtils().getActionUpdate();
            boolean actionReload = CraftingStoreSponge.getInstance().getWebSocketUtils().getActionReload();
            boolean actionPolling = CraftingStoreSponge.getInstance().getWebSocketUtils().getActionPolling();
            boolean isConnected = CraftingStoreSponge.getInstance().getWebSocketUtils().getConnected();

            // Update (Donation received)
            if (actionUpdate) {
                if (CraftingStoreSponge.getInstance().getDebug()) {
                    CraftingStoreSponge.getInstance().getLogger().info("Donation received.");
                }

                Sponge.getScheduler().createTaskBuilder().async().delayTicks(20).execute(new DonationCheckTimer(CraftingStoreSponge.getInstance())).submit(CraftingStoreSponge.getInstance());
            }

            // Reload plugin
            if (actionReload) {
                if (CraftingStoreSponge.getInstance().getDebug()) {
                    CraftingStoreSponge.getInstance().getLogger().info("Reloading plugin.");
                }

                CraftingStoreSponge.getInstance().refreshKey();
            }

            // Enable polling.
            if (actionPolling || !isConnected) {

                if (CraftingStoreSponge.getInstance().getDebug()) {
                    CraftingStoreSponge.getInstance().getLogger().info("Switching to polling.");
                }

                CraftingStoreSponge.getInstance().getWebSocketUtils().disconnect();
                CraftingStoreSponge.getInstance().setSocketEnabled(false);
                CraftingStoreSponge.getInstance().startTimers(CraftingStoreSponge.getInstance().getIntervalDonationTimer());

                // Reload plugin after 10 mins (reconnect)
                Sponge.getScheduler().createTaskBuilder().async().delayTicks(600 * 20).execute(new ReloadPluginTimer(CraftingStoreSponge.getInstance())).submit(CraftingStoreSponge.getInstance());

            }

        } catch (Exception e) {
            instance.getLogger().error("An error occurred while checking for donations.", e);
        }
    }

}
