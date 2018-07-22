package net.craftingstore.sponge.timers;

import net.craftingstore.CraftingStoreAPI;
import net.craftingstore.Donation;
import net.craftingstore.sponge.CraftingStoreSponge;
import net.craftingstore.sponge.events.DonationReceivedEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class DonationCheckTimer implements Runnable {

    private CraftingStoreSponge instance;

    public DonationCheckTimer(CraftingStoreSponge instance) {
        this.instance = instance;
    }

    public void run() {

        try {

            if (CraftingStoreSponge.getInstance().getDebug()) {
                CraftingStoreSponge.getInstance().getLogger().info("Checking for donations.");
            }

            ArrayList<Integer> commands = new ArrayList<Integer>();

            Donation[] donations = CraftingStoreAPI.getInstance().getQueries(CraftingStoreSponge.getInstance().getKey());
            for (Donation donation : donations) {
                String plainUuid = donation.getUuid();
                Boolean requireOnline = donation.getRequireOnline();
                Boolean donationProcessed = false;
                UUID uuid = null;
                if (plainUuid != null && !plainUuid.isEmpty()) {
                    String formattedUuid = plainUuid.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5");
                    uuid = UUID.fromString(formattedUuid);
                }


                if (CraftingStoreSponge.getInstance().getDebug()) {
                    CraftingStoreSponge.getInstance().getLogger().info("Processing command: '" + donation.getCommand() + "'");
                }

                Optional<Player> player = Sponge.getServer().getPlayer(donation.getMcName());


                if (requireOnline && player.isPresent()) {
                    commands.add(donation.getId());

                    Sponge.getScheduler().createTaskBuilder()
                            .execute(
                                    t -> Sponge.getCommandManager()
                            .process(Sponge.getServer().getConsole(), donation.getCommand()))
                            .submit(CraftingStoreSponge.getInstance());
                    donationProcessed = true;


                } else if(!requireOnline) {
                    commands.add(donation.getId());
                    Sponge.getScheduler().createTaskBuilder()
                            .execute(
                                    t -> Sponge.getCommandManager()
                                            .process(Sponge.getServer().getConsole(), donation.getCommand()))
                            .submit(CraftingStoreSponge.getInstance());
                    donationProcessed = true;

                }

                // Call custom event
                if (donationProcessed) {
                    Sponge.getEventManager().post(new DonationReceivedEvent(donation.getCommand(), donation.getMcName(), uuid, donation.getPackageName(), donation.getPackagePrice(), donation.getCouponDiscount()));
                }
            }

            // Register commands as done.
            if (commands.size() > 0) {
                if (CraftingStoreSponge.getInstance().getDebug()) {
                    CraftingStoreSponge.getInstance().getLogger().info("Setting commands to completed...");
                }
                CraftingStoreAPI.getInstance().completeCommands(CraftingStoreSponge.getInstance().getKey(), commands.toString());
            }


        } catch (Exception e) {
            instance.getLogger().error("An error occurred while checking for donations.", e);
        }
    }

}
