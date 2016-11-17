package net.craftingstore.bungee;

import net.craftingstore.CraftingStoreAPI;
import net.craftingstore.bungee.config.Config;
import net.craftingstore.bungee.timers.DonationCheckTimer;
import net.craftingstore.utils.HttpUtils;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class CraftingStoreBungee extends Plugin {

    private static CraftingStoreBungee instance;

    public static CraftingStoreBungee getInstance() {
        return instance;
    }

    private Config config;
    private String key;

    @Override
    public void onEnable() {
        instance = this;

        String key = getConfig().getString("api-key");
        this.key = key;
        if (key.length() == 0) {
            getLogger().log(Level.SEVERE, "Your API key is not set. The plugin will not work until your API key is set.");
            return;
        }

        try {
            if (!CraftingStoreAPI.getInstance().checkKey(key)) {
                getLogger().log(Level.SEVERE, "Your API key is invalid. The plugin will not work until your API key is valid.");
                return;
            }
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "An error occurred while checking the API key.", e);
            return;
        }

        int interval = getConfig().getInt("interval");
        getProxy().getScheduler().schedule(this, new DonationCheckTimer(this), interval, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public Configuration getConfig() {
        return config.getConfig();
    }

    public void saveConfig() {
        config.saveConfig();
    }

    public String getKey() {
        return key;
    }

}
