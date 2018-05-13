package net.craftingstore;

import com.google.gson.annotations.SerializedName;

public class Donation {

    private String command;

    @SerializedName("mcName")
    private String username;

    @SerializedName("id")
    private Integer id;

    private String uuid;

    @SerializedName("packageName")
    private String packageName;

    @SerializedName("requireOnline")
    private String requireOnline;

    @SerializedName("packagePrice")
    private int packagePrice;

    @SerializedName("couponDiscount")
    private int couponDiscount;

    public String getCommand() {
        return command;
    }

    public Integer getId() {
        return id;
    }

    public String getMcName() {
        return username;
    }

    public String getUuid() {
        return uuid;
    }

    public String getPackageName() {
        return packageName;
    }

    public Boolean getRequireOnline() {
        return Boolean.valueOf(requireOnline);
    }

    public int getPackagePrice() {
        return packagePrice;
    }

    public int getCouponDiscount() {
        return couponDiscount;
    }

}
