package net.craftingstore.bungee.timers;

import net.craftingstore.CraftingStoreAPI;
import net.craftingstore.Donation;
import net.craftingstore.bukkit.CraftingStoreBukkit;
import net.craftingstore.bungee.CraftingStoreBungee;
import net.craftingstore.bungee.events.DonationReceivedEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

public class DonationCheckTimer implements Runnable {

    private Plugin instance;

    public DonationCheckTimer(Plugin instance) {
        this.instance = instance;
    }

    public void run() {
        try {
            ArrayList<Integer> commands = new ArrayList<Integer>();

            Donation[] donations = CraftingStoreAPI.getInstance().getQueries(CraftingStoreBungee.getInstance().getKey());

            for (Donation donation : donations) {
                String plainUuid = donation.getUuid();
                Boolean requireOnline = donation.getRequireOnline();
                UUID uuid = null;
                if (plainUuid != null && !plainUuid.isEmpty()) {
                    String formattedUuid = plainUuid.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5");
                    uuid = UUID.fromString(formattedUuid);
                }

                final DonationReceivedEvent event = new DonationReceivedEvent(donation.getCommand(), donation.getMcName(), uuid, donation.getPackageName(), donation.getPackagePrice(), donation.getCouponDiscount());
                instance.getProxy().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {

                    if (requireOnline && ProxyServer.getInstance().getPlayer(donation.getMcName()) != null && ProxyServer.getInstance().getPlayer(donation.getMcName()).isConnected()) {

                        commands.add(donation.getId());
                        instance.getProxy().getPluginManager().dispatchCommand(instance.getProxy().getConsole(), event.getCommand());
                    } else if (!requireOnline) {

                        commands.add(donation.getId());
                        instance.getProxy().getPluginManager().dispatchCommand(instance.getProxy().getConsole(), event.getCommand());

                    }
                }

            }

            if (commands.size() > 0) {
                CraftingStoreAPI.getInstance().completeCommands(CraftingStoreBungee.getInstance().getKey(), commands.toString());
            }

        } catch (Exception e) {
            instance.getLogger().log(Level.SEVERE, "An error occurred while checking for donations.", e);
        }
    }

}
