package net.craftingstore.sponge;

import net.craftingstore.CraftingStoreAPI;
import net.craftingstore.Socket;
import net.craftingstore.sponge.commands.CraftingStoreCommand;
import net.craftingstore.sponge.config.Config;
import net.craftingstore.sponge.models.QueryCache;
import net.craftingstore.sponge.timers.DonationCheckTimer;
import net.craftingstore.sponge.utils.WebSocketUtils;
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

        // Get socket information.
        try {
            Socket socket = CraftingStoreAPI.getInstance().getSocket(key);

            socketsUrl = socket.getSocketUrl();
            socketsEnabled = socket.getSocketAllowed();
            socketsProvider = socket.getSocketProvider();
            socketPusherApi = socket.getPusherApi();
            socketPusherLocation = socket.getPusherLocation();
            socketFallbackUrl = socket.getSocketFallbackUrl();

        } catch (Exception e) {
            getLogger().error("An error occurred while checking the store status.", e);
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

    public Config getConfig()
    {
        return this.config;
    }
}
