package net.craftingstore;

import com.google.gson.annotations.SerializedName;

public class TopDonator {

    @SerializedName("mc_name")
    private String username;

    private float total;

    private String uuid;

    public String getUsername() {
        return username;
    }

    public float getTotal() {
        return total;
    }

    public String getUuid() {
        return uuid;
    }
}
