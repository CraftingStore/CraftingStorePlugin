package net.craftingstore.bungee;

import net.craftingstore.CraftingStoreAPI;
import net.craftingstore.Socket;
import net.craftingstore.bungee.commands.CraftingStoreCommand;
import net.craftingstore.bungee.timers.SocketCheckTimer;
import net.craftingstore.bungee.config.Config;
import net.craftingstore.bungee.timers.DonationCheckTimer;
import net.craftingstore.utils.SocketUtils;
import net.md_5.bungee.api.ChatColor;
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
    private Boolean debug;
    private int intervalDonationTimer = 60;
    public String prefix = ChatColor.GRAY + "[" + ChatColor.RED + "CraftingStore" + ChatColor.GRAY + "] ";

    // SOCKET: Custom
    private boolean socketEnabled;
    private String socketCustomUrl;

    private SocketUtils webSocketUtils = null;

    @Override
    public void onEnable() {
        instance = this;
        config = new Config(this, "config.yml");

        getProxy().getPluginManager().registerCommand(this, new CraftingStoreCommand());

        this.debug = getConfig().getBoolean("debug");

        refreshKey();
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

    public Boolean getDebug() {
        return debug;
    }

    public SocketUtils getWebSocketUtils() {
        return webSocketUtils;
    }

    public int getIntervalDonationTimer() {
        return intervalDonationTimer;
    }

    public void setSocketEnabled(boolean enabled) {
        this.socketEnabled = enabled;
    }

    public void refreshKey() {

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

        if (this.key != null) {

            getLogger().log(Level.INFO, "Your key is valid, and you are ready to accept donations!");

            intervalDonationTimer = getConfig().getInt("interval");
            if (intervalDonationTimer < 60) {
                getLogger().log(Level.WARNING, "The interval cannot be lower than 60 seconds. An interval of 60 seconds will be used.");
                intervalDonationTimer = 60;
            }

            this.startTimers(intervalDonationTimer);

            // SOCKETS: Connect
            this.getSocket();
            this.connectToSocket();
        }
    }


    public void getSocket() {

        String apiKey = this.key;
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

            // Enable socket connection.
            webSocketUtils = new SocketUtils(key, socketCustomUrl);

            // Set interval to 35 minutes, as backup method.
            intervalDonationTimer = 60 * 35;
        }
    }

    public void startTimers(int interval) {

        getProxy().getScheduler().cancel(this);
        getProxy().getScheduler().schedule(this, new DonationCheckTimer(this), 10, interval, TimeUnit.SECONDS);

        if (socketEnabled) {
            getProxy().getScheduler().schedule(this, new SocketCheckTimer(this), 40, 6, TimeUnit.SECONDS);
        }
    }

}
