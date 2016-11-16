package net.craftingstore.bukkit;

import net.craftingstore.bukkit.config.Config;
import net.craftingstore.bukkit.timers.DonationCheckTimer;
import net.craftingstore.utils.HttpUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.logging.Level;

public class CraftingStoreBukkit extends JavaPlugin {

    private static CraftingStoreBukkit instance;

    public static CraftingStoreBukkit getInstance() {
        return instance;
    }

    private Config config;
    private String apiUrl;

    @Override
    public void onEnable() {
        instance = this;
        config = new Config("config.yml", this);

        String key = getConfig().getString("api-key");
        if (key.length() == 0) {
            getLogger().log(Level.SEVERE, "Your API key is not set. The plugin will not work until your API key is set.");
            return;
        }

        apiUrl = "http://api.craftingstore.net/v2/" + key + "/";

        if (!checkApiKey(apiUrl)) {
            getLogger().log(Level.SEVERE, "Your API key is invalid. The plugin will not work until your API key is valid.");
            return;
        }

        new DonationCheckTimer(this).runTaskTimerAsynchronously(this, 0L, 3200L);
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    @Override
    public FileConfiguration getConfig() {
        return config.getConfig();
    }

    @Override
    public void saveConfig() {
        config.saveConfig();
    }

    public String getApiUrl() {
        return apiUrl;
    }

    private boolean checkApiKey(String apiUrl) {
        try {
            String json = HttpUtils.getJson(apiUrl + "/check");
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(json);
            return (Boolean) object.get("success");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "An error occurred while checking the API key.", e);
        }
        return false;
    }

}
