package net.craftingstore.utils;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketUtils {

    private String apiKey;

    private String socketsURL;

    private Socket socketIo;

    private boolean actionUpdate = false;
    private boolean actionReload = false;
    private boolean actionPolling = false;

    public SocketUtils(String apiKey, String socketsURL) {

        this.apiKey = apiKey;
        this.socketsURL = socketsURL;

        run();
    }


    private void run() {
        try {

            IO.Options options = new IO.Options();
            options.reconnection = false;
            this.socketIo = IO.socket(socketsURL, options);

            // CONNECT
            this.socketIo.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    socketIo.emit("auth-client", apiKey);
                }

            // RECEIVE DONATION
            }).on("receive-donation", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    actionUpdate = true;
                }

            // REMOTE RELOAD
            }).on("reload-plugin", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    actionReload = true;
                }

            // DISCONNECT
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    actionPolling = true;
                }

            });

            this.socketIo.connect();

        } catch (Exception e) {
            // Error.
        }
    }


    /**
     * Disconnect from socket.
     */
    public void disconnect() {
        this.socketIo.disconnect();
    }

    public boolean getActionUpdate() {
        boolean action = this.actionUpdate;
        this.actionUpdate = false;

        return action;
    }

    public boolean getActionReload() {
        boolean action = this.actionReload;
        this.actionReload = false;

        return action;
    }

    public boolean getActionPolling() {
        boolean action = this.actionPolling;
        this.actionPolling = false;

        return action;
    }

    public boolean getConnected() {
        return socketIo.connected();
    }
}
