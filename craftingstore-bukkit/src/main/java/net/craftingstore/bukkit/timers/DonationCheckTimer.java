package net.craftingstore.bukkit.timers;

import net.craftingstore.CraftingStoreAPI;
import net.craftingstore.Donation;
import net.craftingstore.bukkit.CraftingStoreBukkit;
import net.craftingstore.bukkit.events.DonationReceivedEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

public class DonationCheckTimer extends BukkitRunnable {

    private Plugin instance;

    public DonationCheckTimer(Plugin instance) {
        this.instance = instance;
    }

    public void run() {
        try {

            if (CraftingStoreBukkit.getInstance().getDebug()) {
                CraftingStoreBukkit.getInstance().getLogger().log(Level.INFO, "Checking for donations.");
            }

            ArrayList<Integer> commands = new ArrayList<Integer>();

            Donation[] donations = CraftingStoreAPI.getInstance().getQueries(CraftingStoreBukkit.getInstance().getKey());
            for (Donation donation : donations) {
                String plainUuid = donation.getUuid();
                Boolean requireOnline = donation.getRequireOnline();
                UUID uuid = null;
                if (plainUuid != null && !plainUuid.isEmpty()) {
                    String formattedUuid = plainUuid.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5");
                    uuid = UUID.fromString(formattedUuid);
                }

                final DonationReceivedEvent event = new DonationReceivedEvent(donation.getCommand(), donation.getMcName(), uuid, donation.getPackageName(), donation.getPackagePrice(), donation.getCouponDiscount());
                instance.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {

                    if (requireOnline && Bukkit.getPlayerExact(donation.getMcName()) != null) {

                        commands.add(donation.getId());
                        instance.getServer().getScheduler().runTask(instance, new Runnable() {
                            public void run() {
                                Bukkit.dispatchCommand(instance.getServer().getConsoleSender(), event.getCommand());
                            }
                        });

                    } else if(!requireOnline) {

                        commands.add(donation.getId());
                        instance.getServer().getScheduler().runTask(instance, new Runnable() {
                            public void run() {
                                Bukkit.dispatchCommand(instance.getServer().getConsoleSender(), event.getCommand());
                            }
                        });
                    }

                }
            }

            // Register commands as done.
            if (commands.size() > 0) {
                CraftingStoreAPI.getInstance().completeCommands(CraftingStoreBukkit.getInstance().getKey(), commands.toString());
            }


        } catch (Exception e) {
            instance.getLogger().log(Level.SEVERE, "An error occurred while checking for donations.", e);
        }
    }

}
