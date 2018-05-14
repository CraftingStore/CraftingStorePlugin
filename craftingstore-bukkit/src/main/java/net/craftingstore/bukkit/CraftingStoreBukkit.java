package net.craftingstore.bukkit;

import net.craftingstore.CraftingStoreAPI;
import net.craftingstore.Socket;
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

    private QueryCache queryCache;
    public String prefix = ChatColor.GRAY + "[" + ChatColor.RED + "CraftingStore" + ChatColor.GRAY + "] ";

    @Override
    public void onEnable() {
        instance = this;
        config = new Config("config.yml", this);
        queryCache = new QueryCache();

        // Get config items.
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
        Integer additionalTimerInterval =  60 * 10 * 20; // minutes.

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

        // Check if we should enable sockets.
        Integer socketsProvider;
        Boolean socketsEnabled;
        String socketsUrl;

        try {
            Socket socket = CraftingStoreAPI.getInstance().getSocket(key);

            socketsUrl = socket.getSocketUrl();
            socketsEnabled = socket.getSocketAllowed();
            socketsProvider = socket.getSocketProvider();

        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "An error occurred while checking the store status.", e);
            return;
        }

        if (this.key != null) {
            getLogger().log(Level.INFO, "Your key is valid, and you are ready to accept donations!");

            int interval = getConfig().getInt("interval") * 20;
            if (interval < 1200) {
                getLogger().log(Level.WARNING, "The interval cannot be lower than 60 seconds. An interval of 60 seconds will be used.");
                interval = 1200;
            }

            // Use check if we should use realtime sockets (only if this is a premium store)
            if (socketsEnabled) {

                // Enable socket connection.
                new WebSocketUtils(key, socketsUrl, socketsProvider);

                // Set interval to 35 minutes, as backup method.
                interval = 60 * 35 * 20;

                // Set interval to 50 minutes, as backup method.
                additionalTimerInterval = 60 * 50 * 20;

                if (this.debug) {
                    getLogger().log(Level.INFO, "Instant payments enabled, using sockets. [URL: " + socketsUrl + " | Provider: " + socketsProvider + "]");
                }
            }

            Bukkit.getScheduler().cancelTasks(this);

            new DonationCheckTimer(this).runTaskTimerAsynchronously(this, 6 * 20, interval);
            new TopDonatorTimer(this).runTaskTimerAsynchronously(this, 20, additionalTimerInterval);
            new RecentPaymentsTimer(this).runTaskTimerAsynchronously(this, 20, additionalTimerInterval);
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
