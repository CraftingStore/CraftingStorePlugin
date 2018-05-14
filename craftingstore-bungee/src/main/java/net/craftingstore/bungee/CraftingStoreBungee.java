package net.craftingstore.bungee;

import net.craftingstore.CraftingStoreAPI;
import net.craftingstore.Socket;
import net.craftingstore.bungee.commands.CraftingStoreCommand;
import net.craftingstore.bungee.utils.WebSocketUtils;
import net.craftingstore.bungee.config.Config;
import net.craftingstore.bungee.timers.DonationCheckTimer;
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
    public String prefix = ChatColor.GRAY + "[" + ChatColor.RED + "CraftingStore" + ChatColor.GRAY + "] ";

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

            int interval = getConfig().getInt("interval");
            if (interval < 60) {
                getLogger().log(Level.WARNING, "The interval cannot be lower than 60 seconds. An interval of 60 seconds will be used.");
                interval = 60;
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

            // Use check if we should use realtime sockets (only if this is a premium store)
            if (socketsEnabled) {

                // Enable socket connection.
                new WebSocketUtils(key, socketsUrl, socketsProvider);

                // Set interval to 35 minutes, as backup method.
                interval = 60 * 35;

                if (this.debug) {
                    getLogger().log(Level.INFO, "Instant payments enabled, using sockets. [URL: " + socketsUrl + " | Provider: " + socketsProvider + "]");
                }
            }

            getProxy().getScheduler().cancel(this);
            getProxy().getScheduler().schedule(this, new DonationCheckTimer(this), 10, interval, TimeUnit.SECONDS);
        }
    }

}
