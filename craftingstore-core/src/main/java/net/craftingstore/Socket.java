package net.craftingstore;

import com.google.gson.annotations.SerializedName;

public class Socket {

    private boolean socketEnabled;
    private Integer socketSupplier;
    private String socketConnectUrl;
    private String socketFallbackUrl;

    private String pusherApi;
    private String pusherLocation;


    public boolean getSocketAllowed() {
        return socketEnabled;
    }

    public String getSocketUrl() {
        return socketConnectUrl;
    }

    public Integer getSocketProvider() {
        return socketSupplier;
    }

    @SerializedName("pusherLocation")
    public String getPusherLocation() {
        return pusherLocation;
    }

    @SerializedName("pusherApi")
    public String getPusherApi() {
        return pusherApi;
    }

    @SerializedName("socketFallbackUrl")
    public String getSocketFallbackUrl() {
        return socketFallbackUrl;
    }

}
