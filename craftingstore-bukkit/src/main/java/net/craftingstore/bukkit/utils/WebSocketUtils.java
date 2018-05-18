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
    private Integer socketsProvider;
    private String pusherApiKey;
    private String pusherLocation;
    private String socketFallbackUrl;

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
        Pusher pusher = new Pusher(this.pusherApiKey, options);

        pusher.connect(new ConnectionEventListener() {

            public void onConnectionStateChange(ConnectionStateChange change) {
                if (change.getCurrentState() == ConnectionState.CONNECTED) {
                    CraftingStoreBukkit.getInstance().getLogger().log(Level.INFO, "Your server successfully connected to CraftingStore.net.");

                }
            }

            public void onError(String message, String code, Exception e) {

                // Pusher gave us an error.. try our custom socket (fallback) server instead.
                try {
                    moduleCustomServer(socketFallbackUrl);
                } catch (URISyntaxException e1) {
                    e1.printStackTrace();
                }
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
    }


    private void moduleCustomServer(String socketsURL) throws URISyntaxException {

        if (CraftingStoreBukkit.getInstance().getDebug()) {
            CraftingStoreBukkit.getInstance().getLogger().log(Level.INFO, "Using CraftingStore NodeJs socket server. (" + socketsURL + ")");
        }

        final Socket socket = IO.socket(socketsURL);
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                CraftingStoreBukkit.getInstance().getLogger().log(Level.INFO, "Your server successfully connected to CraftingStore.net.");
            }

        }).on(this.apiKey, new Emitter.Listener() {

            @Override
            public void call(Object... args) {

                new DonationCheckTimer(CraftingStoreBukkit.getInstance()).runTaskAsynchronously(CraftingStoreBukkit.getInstance());
                new TopDonatorTimer(CraftingStoreBukkit.getInstance()).runTaskAsynchronously(CraftingStoreBukkit.getInstance());
                new RecentPaymentsTimer(CraftingStoreBukkit.getInstance()).runTaskAsynchronously(CraftingStoreBukkit.getInstance());

                if (CraftingStoreBukkit.getInstance().getDebug()) {
                    CraftingStoreBukkit.getInstance().getLogger().log(Level.INFO, "Donation update request received though socket.");
                }
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                CraftingStoreBukkit.getInstance().getLogger().log(Level.INFO, "Your server disconnected from CraftingStore.net.");
            }

        });
        socket.connect();
    }

}
