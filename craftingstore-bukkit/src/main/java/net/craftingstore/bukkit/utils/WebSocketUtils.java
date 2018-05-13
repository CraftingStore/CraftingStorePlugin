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

import java.util.logging.Level;

public class WebSocketUtils {

    private String apiKey;

    private String socketsURL;
    private Integer socketsProvider;

    public WebSocketUtils(String apiKey, String socketsURL, Integer socketsProvider) {
        this.apiKey = apiKey;
        this.socketsURL = socketsURL;
        this.socketsProvider = socketsProvider;
        run(); // Run code.
    }


    private void run() {
        try {

            /* Pusher.com */
            if (this.socketsProvider == 1) {

                if (CraftingStoreBukkit.getInstance().getDebug()) {
                    CraftingStoreBukkit.getInstance().getLogger().log(Level.INFO, "Using pusher.com as socket server.");
                }

                PusherOptions options = new PusherOptions().setCluster("eu");
                Pusher pusher = new Pusher("97b09fa4cdd340b8b26d", options);

                pusher.connect(new ConnectionEventListener() {

                    public void onConnectionStateChange(ConnectionStateChange change) {
                        if (change.getCurrentState() == ConnectionState.CONNECTED) {
                            CraftingStoreBukkit.getInstance().getLogger().log(Level.INFO, "Your server successfully connected to CraftingStore.net.");

                        }
                    }

                    public void onError(String message, String code, Exception e) {
                        CraftingStoreBukkit.getInstance().getLogger().log(Level.SEVERE, "An error occurred when connecting to the socket server (Node: Pusher.com).");
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


            /* Socket.IO server */
            } else {

                if (CraftingStoreBukkit.getInstance().getDebug()) {
                    CraftingStoreBukkit.getInstance().getLogger().log(Level.INFO, "Using CraftingStore NodeJs socket server.");
                }

                final Socket socket = IO.socket(this.socketsURL);
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


        } catch (Exception e) {
            // error
        }
    }

}
