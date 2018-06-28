package net.craftingstore.sponge;

import net.craftingstore.CraftingStoreAPI;
import net.craftingstore.Socket;
import net.craftingstore.sponge.config.Config;
import net.craftingstore.sponge.models.QueryCache;
import net.craftingstore.sponge.timers.DonationCheckTimer;
import net.craftingstore.sponge.utils.WebSocketUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.format.TextColors;


import org.slf4j.Logger;
import java.nio.file.Path;
import com.google.inject.Inject;

@Plugin(id = "craftingstore", name = "CraftingStore", version = "1.3")
public class CraftingStoreSponge {

    private static CraftingStoreSponge instance;

    public static CraftingStoreSponge getInstance() {
        return instance;
    }

    private String key;
    private Boolean debug;
    private Config config;

    @Inject
    private Logger logger;

    private QueryCache queryCache;

    public String prefix = TextColors.GRAY + "[" + TextColors.RED + "CraftingStore" + TextColors.GRAY + "] ";

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path defaultConfig;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        instance = this;
        queryCache = new QueryCache();

        getLogger().info("Plugin is now enabled!");

        refreshKey();

    }

    @Listener
    public void onGameStoppingServerEvent(GameStoppingServerEvent e) {
        // cancel all our tasks.
        Sponge.getEventManager().unregisterPluginListeners(this);
        for (Task task : Sponge.getScheduler().getScheduledTasks(this)) {
            task.cancel();
        }
    }

    private void refreshKey() {

        config = new Config(defaultConfig.toFile(), "config.yml");
        this.debug = config.getConfig().getNode("debug").getBoolean();
        this.key = config.getConfig().getNode("api_key").getString();

        if (key == null || key.length() == 0) {
            getLogger().info("Your API key is not set. The plugin will not work until your API key is set.");
            this.key = null;
            return;
        }

        // Check key
        try {
            if (!CraftingStoreAPI.getInstance().checkKey(key)) {
                getLogger().info("Your API key is invalid. The plugin will not work until your API key is valid.");
                this.key = null;
                return;
            }
        } catch (Exception e) {
            getLogger().info("An error occurred while checking the API key.", e);
            this.key = null;
            return;
        }

        // Cancel tasks.
        for (Task task : Sponge.getScheduler().getScheduledTasks(this)) {
            task.cancel();
        }

        // Check if we should enable sockets.
        Integer socketsProvider;
        Boolean socketsEnabled;
        String socketsUrl;

        String socketPusherApi;
        String socketPusherLocation;
        String socketFallbackUrl;

        try {
            Socket socket = CraftingStoreAPI.getInstance().getSocket(key);

            socketsUrl = socket.getSocketUrl();
            socketsEnabled = socket.getSocketAllowed();
            socketsProvider = socket.getSocketProvider();
            socketPusherApi = socket.getPusherApi();
            socketPusherLocation = socket.getPusherLocation();
            socketFallbackUrl = socket.getSocketFallbackUrl();

        } catch (Exception e) {
            getLogger().info("An error occurred while checking the store status.", e);
            return;
        }

        if (this.key != null) {
            getLogger().info("Your key is valid, and you are ready to accept donations!");

            int interval = config.getConfig().getNode("interval").getInt() * 20;

            if (interval < 1200) {
                getLogger().warn("The interval cannot be lower than 60 seconds. An interval of 60 seconds will be used.");
                interval = 1200;
            }

            // Use check if we should use realtime sockets (only if this is a premium store)
            if (socketsEnabled) {

                // Enable socket connection.
                new WebSocketUtils(key, socketsUrl, socketsProvider, socketPusherApi, socketPusherLocation, socketFallbackUrl);

                // Set interval to 35 minutes, as backup method.
                interval = 60 * 35 * 20;

                if (this.debug) {
                    getLogger().info("Instant payments enabled, using sockets. [URL: " + socketsUrl + " | Provider: " + socketsProvider + "]");
                }
            }


            Sponge.getScheduler().createTaskBuilder().async().delayTicks(20).intervalTicks(interval).execute(new DonationCheckTimer(this)).submit(this);

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

    public Logger getLogger() {
        return this.logger;
    }
}
