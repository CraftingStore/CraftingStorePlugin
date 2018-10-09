package net.craftingstore.bukkit.utils;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import net.craftingstore.bukkit.CraftingStoreBukkit;
import net.craftingstore.bukkit.timers.DonationCheckTimer;
import net.craftingstore.bukkit.timers.RecentPaymentsTimer;
import net.craftingstore.bukkit.timers.TopDonatorTimer;

import java.net.URISyntaxException;
import java.util.logging.Level;

public class WebSocketUtils {

    private String apiKey;

    private String socketsURL;
    private int socketsProvider;
    private String pusherApiKey;
    private String pusherLocation;
    private String socketFallbackUrl;
    private int disconnects = 0;

    // Socket connections
    private Pusher pusher;
    private Socket socketIo;

    public WebSocketUtils(String apiKey, String socketsURL, Integer socketsProvider, String pusherApiKey, String pusherLocation, String fallbackSocketUrl) {

        this.apiKey = apiKey;
        this.socketsURL = socketsURL;
        this.socketsProvider = socketsProvider;
        this.pusherApiKey = pusherApiKey;
        this.pusherLocation = pusherLocation;
        this.socketFallbackUrl = fallbackSocketUrl;

        run();
    }


    private void run() {
        try {

            /* Pusher.com */
            if (this.socketsProvider == 1) {

                // Call pusher method.
                modulePusher();


            /* Socket.IO server */
            } else {

                // Call custom server method.
                moduleCustomServer(this.socketsURL);

            }


        } catch (Exception e) {
            // error
        }
    }

    private void modulePusher() {


        if (CraftingStoreBukkit.getInstance().getDebug()) {
            CraftingStoreBukkit.getInstance().getLogger().log(Level.INFO, "Using pusher.com as socket server.");
        }

        PusherOptions options = new PusherOptions().setCluster(this.pusherLocation);
        this.pusher = new Pusher(this.pusherApiKey, options);

        this.pusher.connect(new ConnectionEventListener() {

            public void onConnectionStateChange(ConnectionStateChange change) {
                if (change.getCurrentState() == ConnectionState.CONNECTED) {
                    CraftingStoreBukkit.getInstance().getLogger().log(Level.INFO, "Your server successfully connected to CraftingStore.net.");

                }
            }

            public void onError(String message, String code, Exception e) {

            }

        }, ConnectionState.ALL);

        // Subscribe to API key.
        Channel channel = pusher.subscribe(this.apiKey);

        channel.bind("receive-donation", new SubscriptionEventListener() {

            public void onEvent(String channel, String event, String data) {
                // Donation event received, update commands.
                new DonationCheckTimer(CraftingStoreBukkit.getInstance()).runTaskAsynchronously(CraftingStoreBukkit.getInstance());
                new TopDonatorTimer(CraftingStoreBukkit.getInstance()).runTaskAsynchronously(CraftingStoreBukkit.getInstance());
                new RecentPaymentsTimer(CraftingStoreBukkit.getInstance()).runTaskAsynchronously(CraftingStoreBukkit.getInstance());

                if (CraftingStoreBukkit.getInstance().getDebug()) {
                    CraftingStoreBukkit.getInstance().getLogger().log(Level.INFO, "Donation update request received though socket.");
                }
            }
        });

        // Reload plugin
        channel.bind("reload-plugin", new SubscriptionEventListener() {

            public void onEvent(String channel, String event, String data) {

                // Reload the plugin.
                CraftingStoreBukkit.getInstance().refreshKey();
            }
        });
    }


    private void moduleCustomServer(String socketsURL) throws URISyntaxException {

        if (CraftingStoreBukkit.getInstance().getDebug()) {
            CraftingStoreBukkit.getInstance().getLogger().log(Level.INFO, "Using CraftingStore NodeJs socket server. (" + socketsURL + ")");
        }

        IO.Options options = new IO.Options();
        options.reconnection = false;
        this.socketIo = IO.socket(socketsURL, options);

        this.socketIo.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                if (CraftingStoreBukkit.getInstance().getDebug()) {
                    CraftingStoreBukkit.getInstance().getLogger().log(Level.INFO, "Your server successfully connected to CraftingStore.net.");
                }
                socketIo.emit("auth-client", apiKey);
                socketIo.emit("version", CraftingStoreBukkit.getInstance().getDescription().getVersion());
            }

        }).on("receive-donation", new Emitter.Listener() {

            @Override
            public void call(Object... args) {

                new DonationCheckTimer(CraftingStoreBukkit.getInstance()).runTaskAsynchronously(CraftingStoreBukkit.getInstance());
                new TopDonatorTimer(CraftingStoreBukkit.getInstance()).runTaskAsynchronously(CraftingStoreBukkit.getInstance());
                new RecentPaymentsTimer(CraftingStoreBukkit.getInstance()).runTaskAsynchronously(CraftingStoreBukkit.getInstance());

                if (CraftingStoreBukkit.getInstance().getDebug()) {
                    CraftingStoreBukkit.getInstance().getLogger().log(Level.INFO, "Donation update request received though socket.");
                }
            }

        }).on("reload-plugin", new Emitter.Listener() {

            @Override
            public void call(Object... args) {

                // Reload the plugin.
                CraftingStoreBukkit.getInstance().refreshKey();
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                disconnects++;
                if (CraftingStoreBukkit.getInstance().getDebug()) {
                    CraftingStoreBukkit.getInstance().getLogger().log(Level.INFO, "Your server disconnected from CraftingStore.net, falling back to polling.");
                }
                CraftingStoreBukkit.getInstance().startTimers(1700, 60 * 50 * 20);
            }

        });
        this.socketIo.connect();
    }

    /**
     * Disconnect from all socket servers.
     */
    public void disconnectSocketServers()
    {
        /* Pusher.com */
        if (this.socketsProvider == 1) {

            // Call pusher method.
            if (this.pusher != null) {
                this.pusher.disconnect();
            }


            /* Socket.IO server */
        } else {

            // Call custom server method.
            if (this.socketIo != null) {
                this.socketIo.disconnect();
            }

        }
    }

}
