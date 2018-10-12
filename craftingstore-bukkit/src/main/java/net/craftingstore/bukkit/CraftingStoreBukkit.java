package net.craftingstore.bukkit;

import net.craftingstore.CraftingStoreAPI;
import net.craftingstore.Socket;
import net.craftingstore.bukkit.commands.BuyCommand;
import net.craftingstore.bukkit.commands.CraftingStoreCommand;
import net.craftingstore.bukkit.config.Config;
import net.craftingstore.bukkit.hooks.DonationPlaceholders;
import net.craftingstore.bukkit.listeners.InventoryClickListener;
import net.craftingstore.bukkit.models.QueryCache;
import net.craftingstore.bukkit.timers.*;
import net.craftingstore.utils.SocketUtils;
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
    private int intervalDonationTimer = 1200;
    private int intervalOtherTimers =  60 * 10 * 20;
    private Boolean disableBuyCommand;
    private QueryCache queryCache;

    // SOCKET: Custom
    private boolean socketEnabled;
    private String socketCustomUrl;

    private SocketUtils webSocketUtils = null;


    public String prefix = ChatColor.GRAY + "[" + ChatColor.RED + "CraftingStore" + ChatColor.GRAY + "] ";

    @Override
    public void onEnable() {
        instance = this;
        config = new Config("config.yml", this);
        queryCache = new QueryCache();

        // Get config items.
        this.debug = getConfig().getBoolean("debug");
        this.disableBuyCommand = getConfig().getBoolean("disable-buy-command");

        // Register commands
        this.getCommand("craftingstore").setExecutor(new CraftingStoreCommand());

        if (!this.disableBuyCommand) {
            this.getCommand("buy").setExecutor(new BuyCommand());
        }

        // Register listeners
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);

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

        // Set variables.
        this.key = key;
        this.prefix = getConfig().getString("prefix").replace("&", "§") + " ";

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


        if (this.key != null) {
            getLogger().log(Level.INFO, "Your key is valid, and you are ready to accept donations!");

            intervalDonationTimer = getConfig().getInt("interval") * 20;

            if (intervalDonationTimer < 1200) {
                getLogger().log(Level.WARNING, "The interval cannot be lower than 60 seconds. An interval of 60 seconds will be used.");
                intervalDonationTimer = 1200;
            }

            // SOCKETS: Connect
            this.getSocket();
            this.connectToSocket();

            // Start timers.
            this.startTimers(intervalDonationTimer, intervalOtherTimers);
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

    public SocketUtils getWebSocketUtils() {
        return webSocketUtils;
    }

    public int getIntervalDonationTimer() {
        return intervalDonationTimer;
    }

    public int getIntervalOtherTimers() {
        return intervalOtherTimers;
    }

    public void setSocketEnabled(boolean enabled) {
        this.socketEnabled = enabled;
    }

    public void getSocket() {

        String apiKey = CraftingStoreBukkit.getInstance().getKey();
        try {
            Socket socket = CraftingStoreAPI.getInstance().getSocket(apiKey);

            // GLOBAL
            this.socketEnabled = socket.getSocketAllowed();
            this.socketCustomUrl = socket.getSocketUrl();

        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "An error occurred while checking the store status.", e);
        }
    }

    public void connectToSocket()
    {
        // Disconnect.
        if (webSocketUtils != null) {
            webSocketUtils.disconnect();
        }

        // Socket connection.
        if (socketEnabled) {

            if (debug) {
                getLogger().log(Level.INFO, "Socket connection enabled, tyring to connect with: " + socketCustomUrl);
            }

            // Enable socket connection.
            webSocketUtils = new SocketUtils(key, socketCustomUrl);

            if (webSocketUtils.getConnected()) {

                if (debug) {
                    getLogger().log(Level.INFO, "Socket is now connected, this is NOT validated.");
                }

                // Set interval to 35 minutes, as backup method.
                intervalDonationTimer = 60 * 35 * 20;

                // Set interval to 50 minutes, as backup method.
                intervalOtherTimers = 60 * 50 * 20;

            }
        }
    }

    public void startTimers(int interval, int additionalTimerInterval) {
        Bukkit.getScheduler().cancelTasks(this);

        new DonationCheckTimer(this).runTaskTimerAsynchronously(this, 6 * 20, interval);
        new TopDonatorTimer(this).runTaskTimerAsynchronously(this, 20, additionalTimerInterval);
        new RecentPaymentsTimer(this).runTaskTimerAsynchronously(this, 20, additionalTimerInterval);

        if (socketEnabled) {
            new SocketCheckTimer(this).runTaskTimerAsynchronously(this, 120, 150);
        }

        // Get packages & categories, every 50 minutes.
        if (!disableBuyCommand) {
            new CategoriesTimer(this).runTaskTimerAsynchronously(this, 10, 60 * 50 * 20);
        }
    }
}
