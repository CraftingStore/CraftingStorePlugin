package net.craftingstore.bungee.timers;

import com.google.gson.Gson;
import net.craftingstore.Donation;
import net.craftingstore.bungee.CraftingStoreBungee;
import net.craftingstore.bungee.events.DonationReceivedEvent;
import net.craftingstore.utils.HttpUtils;
import net.md_5.bungee.api.plugin.Plugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.UUID;
import java.util.logging.Level;

public class DonationCheckTimer implements Runnable {

    private Plugin instance;

    public DonationCheckTimer(Plugin instance) {
        this.instance = instance;
    }

    public void run() {
        try {
            String json = HttpUtils.getJson(CraftingStoreBungee.getInstance().getApiUrl() + "queries/remove");
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(json);
            json = object.get("result").toString();

            Gson gson = new Gson();
            Donation[] donations = gson.fromJson(json, Donation[].class);

            for (Donation donation : donations) {
                String plainUuid = donation.getUuid();
                String formattedUuid = plainUuid.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5");
                UUID uuid = UUID.fromString(formattedUuid);

                final DonationReceivedEvent event = new DonationReceivedEvent(donation.getCommand(), donation.getMcName(), uuid, donation.getPackageName(), donation.getPackagePrice(), donation.getCouponDiscount());
                instance.getProxy().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    instance.getProxy().getPluginManager().dispatchCommand(instance.getProxy().getConsole(), event.getCommand());
                }
            }
        } catch (Exception e) {
            instance.getLogger().log(Level.SEVERE, "An error occurred while checking for donations.", e);
        }
    }

}
