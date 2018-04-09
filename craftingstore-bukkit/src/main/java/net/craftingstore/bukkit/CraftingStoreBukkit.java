package net.craftingstore.bukkit;

import net.craftingstore.CraftingStoreAPI;
import net.craftingstore.bukkit.commands.CraftingStoreCommand;
import net.craftingstore.bukkit.config.Config;
import net.craftingstore.bukkit.hooks.DonationPlaceholders;
import net.craftingstore.bukkit.models.QueryCache;
import net.craftingstore.bukkit.timers.DonationCheckTimer;
import net.craftingstore.bukkit.timers.RecentPaymentsTimer;
import net.craftingstore.bukkit.timers.TopDonatorTimer;
import net.craftingstore.bukkit.utils.WebSocketUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    private Boolean debug;
    private Boolean useRealTimeSockets;
    private Boolean premiumStore = false;
    private QueryCache queryCache;
    public String prefix = ChatColor.GRAY + "[" + ChatColor.RED + "CraftingStore" + ChatColor.GRAY + "] ";

    @Override
    public void onEnable() {
        instance = this;
        config = new Config("config.yml", this);
        queryCache = new QueryCache();

        // Get config items.
        this.useRealTimeSockets = getConfig().getBoolean("instant-payments");
        this.debug = getConfig().getBoolean("debug");

        // Register commands
        this.getCommand("craftingstore").setExecutor(new CraftingStoreCommand());

        refreshKey();

        if (this.key == null) {
            return;
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new DonationPlaceholders(instance);
            instance.getLogger().log(Level.INFO, "Hooked with PlaceholderAPI");
        }
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

        String key = getConfig().getString("api-key");

        this.key = key;

        if (key.length() == 0) {
            getLogger().log(Level.SEVERE, "Your API key is not set. The plugin will not work until your API key is set.");
            this.key = null;
            return;
        }

        // Check key
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

        // Check store type.
        try {
            if (CraftingStoreAPI.getInstance().storePremium(key)) {
                premiumStore = true;
            }
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "An error occurred while checking the store status.", e);
            this.key = null;
            return;
        }


        if (this.key != null) {
            getLogger().log(Level.INFO, "The API key is valid, your store will now accept new commands.");

            int interval = getConfig().getInt("interval") * 20;
            if (interval < 1200) {
                getLogger().log(Level.WARNING, "The interval cannot be lower than 60 seconds. An interval of 60 seconds will be used.");
                interval = 1200;
            }

            // Use check if we should use realtime sockets (only if this is a premium store)
            if (this.useRealTimeSockets) {
                if (this.premiumStore) {
                    new WebSocketUtils(key);
                    interval = 60 * 25 * 20; // Set interval to 25 minutes, as backup method.
                    if (this.debug) {
                        System.out.println("[CraftingStore-Debug] realtime payment socket enabled!");
                    }
                } else {
                    getLogger().log(Level.SEVERE, "You enabled the realtime socket connection, but your store is on the free plan. Please disable this option!");
                }
            }

            Bukkit.getScheduler().cancelTasks(this);
            new DonationCheckTimer(this).runTaskTimerAsynchronously(this, 6 * 20, interval); // Run after 6 seconds
            new TopDonatorTimer(this).runTaskTimerAsynchronously(this, 20, 60 * 8 * 20); // Run every 5 minutes
            new RecentPaymentsTimer(this).runTaskTimerAsynchronously(this, 20, 60 * 8 * 20); // Run every 5 minutes
        }
    }

    public String getKey() {
        return key;
    }

    public Boolean getDebug() {
        return debug;
    }

    public QueryCache getQueryCache() {
        return queryCache;
    }
}
