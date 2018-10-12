package net.craftingstore.sponge;

import net.craftingstore.CraftingStoreAPI;
import net.craftingstore.Socket;
import net.craftingstore.sponge.commands.CraftingStoreCommand;
import net.craftingstore.sponge.config.Config;
import net.craftingstore.sponge.models.QueryCache;
import net.craftingstore.sponge.timers.DonationCheckTimer;
import net.craftingstore.sponge.timers.SocketCheckTimer;
import net.craftingstore.utils.SocketUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;


import org.slf4j.Logger;
import java.nio.file.Path;

import com.google.inject.Inject;

@Plugin(id = "craftingstore", name = "CraftingStore", version = "1.5")
public class CraftingStoreSponge {

    private static CraftingStoreSponge instance;

    public static CraftingStoreSponge getInstance() {
        return instance;
    }

    private String key;
    private Boolean debug;
    private int intervalDonationTimer = 1200;
    private Config config;

    // SOCKET: Custom
    private boolean socketEnabled;
    private String socketCustomUrl;

    private SocketUtils webSocketUtils = null;

    @Inject
    private Logger logger;

    private QueryCache queryCache;

    public LiteralText.Builder prefix = Text.builder("[").color(TextColors.GRAY)
            .append(Text.builder("CraftingStore").color(TextColors.RED).build())
            .append(Text.builder("] ").color(TextColors.GRAY).build());

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path defaultConfig;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        instance = this;
        queryCache = new QueryCache();

        // Register command.
        CommandSpec mainCommand = CommandSpec.builder()
                .description(Text.of("CraftingStore main command."))
                .permission("craftingstore.admin")
                .arguments(
                        GenericArguments.optional(GenericArguments.string(Text.of("arg1"))),
                        GenericArguments.optional(GenericArguments.string(Text.of("arg2")))
                )
                .executor(new CraftingStoreCommand())
                .build();

        Sponge.getCommandManager().register(this, mainCommand, "craftingstore", "cs");

        getLogger().info("We're ready to accept donations!");

        // Reload key, connect to socket, start tasks.
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

    public void refreshKey() {

        // Create config.yml
        this.config = new Config(defaultConfig.toFile(), "config.yml");

        // Set config items.
        this.debug = config.getConfig().getNode("debug").getBoolean();
        this.key = config.getConfig().getNode("api-key").getString();

        // Check if the key is empty.
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


        if (this.key != null) {
            getLogger().info("Your key is valid, and you are ready to accept donations!");

            intervalDonationTimer = config.getConfig().getNode("interval").getInt() * 20;

            if (intervalDonationTimer < 1200) {
                getLogger().warn("The interval cannot be lower than 60 seconds. An interval of 60 seconds will be used.");
                intervalDonationTimer = 1200;
            }

            // SOCKETS: Connect
            this.getSocket();
            this.connectToSocket();

            // Start timers.
            this.startTimers(intervalDonationTimer);

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

    public Config getConfig()
    {
        return this.config;
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


    public void getSocket() {

        String apiKey = key;
        try {
            Socket socket = CraftingStoreAPI.getInstance().getSocket(apiKey);

            // GLOBAL
            this.socketEnabled = socket.getSocketAllowed();
            this.socketCustomUrl = socket.getSocketUrl();

        } catch (Exception e) {
            // ERROR
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
            intervalDonationTimer = 60 * 35 * 20;
        }
    }

    public void startTimers(int interval) {

        // Cancel tasks.
        for (Task task : Sponge.getScheduler().getScheduledTasks(this)) {
            task.cancel();
        }

        Sponge.getScheduler().createTaskBuilder().async().delayTicks(20).intervalTicks(interval).execute(new DonationCheckTimer(this)).submit(this);

        if (socketEnabled) {
            Sponge.getScheduler().createTaskBuilder().async().delayTicks(160).intervalTicks(interval).execute(new SocketCheckTimer(this)).submit(this);
        }
    }
}
