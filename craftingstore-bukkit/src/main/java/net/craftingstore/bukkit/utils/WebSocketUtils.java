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

public class WebSocketUtils {

    private String apiKey;
    private String socketURL;

    public WebSocketUtils(String apiKey, String socketURL) {
        this.apiKey = apiKey;
        this.socketURL = socketURL;
        run(); // Run code.
    }


    private void run() {
        try {

            /* Pusher.com */
            if (this.socketURL.equalsIgnoreCase("https://socket01.craftingstore.cloudprotected.net")) {


                PusherOptions options = new PusherOptions().setCluster("eu");
                Pusher pusher = new Pusher("97b09fa4cdd340b8b26d", options);

                pusher.connect(new ConnectionEventListener() {

                    public void onConnectionStateChange(ConnectionStateChange change) {
                        if (change.getCurrentState() == ConnectionState.CONNECTED) {
                            System.out.println("[CraftingStore] Your server successfully connected to CraftingStore.net.");
                        }
                    }

                    public void onError(String message, String code, Exception e) {
                        System.out.println("[CraftingStore] There was a problem connecting!");
                    }

                }, ConnectionState.ALL);

                // Subscribe to API key.
                Channel channel = pusher.subscribe(this.apiKey);

                channel.bind("receive-donation", new SubscriptionEventListener() {

                    public void onEvent(String channel, String event, String data) {
                        // Donation event received, update commands.
                        new DonationCheckTimer(CraftingStoreBukkit.getInstance()).runTaskAsynchronously(CraftingStoreBukkit.getInstance());

                        if (CraftingStoreBukkit.getInstance().getDebug()) {
                            System.out.println("[CraftingStore-Debug] Received socket donation request!");
                        }
                    }
                });


            /* Socket.IO server */
            } else {


                final Socket socket = IO.socket(this.socketURL);
                socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        System.out.println("[CraftingStore] Your server successfully connected to CraftingStore.net.");
                    }

                }).on(this.apiKey, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {

                        new DonationCheckTimer(CraftingStoreBukkit.getInstance()).runTaskAsynchronously(CraftingStoreBukkit.getInstance());

                        if (CraftingStoreBukkit.getInstance().getDebug()) {
                            System.out.println("[CraftingStore-Debug] Received socket donation request!");
                        }
                    }

                }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        System.out.println("[CraftingStore] Disconnected from CraftingStore.");
                    }

                });
                socket.connect();

            }


        } catch (Exception e) {
            // error
        }
    }

}
