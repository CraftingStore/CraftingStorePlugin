package net.craftingstore.bukkit;

import net.craftingstore.CraftingStoreAPI;
import net.craftingstore.bukkit.commands.CraftingStoreCommand;
import net.craftingstore.bukkit.config.Config;
import net.craftingstore.bukkit.timers.DonationCheckTimer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class CraftingStoreBukkit extends JavaPlugin {

    private static CraftingStoreBukkit instance;

    public static CraftingStoreBukkit getInstance() {
        return instance;
    }

    private Config config;
    private String key;

    @Override
    public void onEnable() {
        instance = this;

        // Register commands
        this.getCommand("craftingstore").setExecutor(new CraftingStoreCommand());

        refreshKey();

        if (this.key == null) {
            return;
        }

        int interval = getConfig().getInt("interval") * 20;
        if (interval < 1200) {
            getLogger().log(Level.WARNING, "The interval cannot be lower than 60 seconds. An interval of 60 seconds will be used.");
            interval = 1200;
        }

        new DonationCheckTimer(this).runTaskTimerAsynchronously(this, interval, interval);
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

    public void refreshKey() {

        config = new Config("config.yml", this);

        String key = getConfig().getString("api-key");
        this.key = key;

        if (key.length() == 0) {
            getLogger().log(Level.SEVERE, "Your API key is not set. The plugin will not work until your API key is set.");
            this.key = null;
            return;
        }

        try {
            if (!CraftingStoreAPI.getInstance().checkKey(key)) {
                getLogger().log(Level.SEVERE, "Your API key is invalid. The plugin will not work until your API key is valid.");
                this.key = null;
                return;
            }
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "An error occurred while checking the API key.", e);
            this.key = null;
            return;
        }
    }

    public String getKey() {
        return key;
    }

}
