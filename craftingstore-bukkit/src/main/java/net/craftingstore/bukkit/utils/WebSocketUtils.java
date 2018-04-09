package net.craftingstore.bukkit.utils;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import net.craftingstore.bukkit.CraftingStoreBukkit;
import net.craftingstore.bukkit.timers.DonationCheckTimer;

public class WebSocketUtils {

    private String apiKey;

    public WebSocketUtils(String apiKey) {
        this.apiKey = apiKey;
        run(); // Run code.
    }


    private void run() {
        try {
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


        } catch (Exception e) {
            // error
        }
    }

}
