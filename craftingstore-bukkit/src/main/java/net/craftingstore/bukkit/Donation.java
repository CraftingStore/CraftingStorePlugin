package net.craftingstore.bukkit;

public class Donation {

    private String command;
    private String mc_name;
    private String uuid;
    private String package_name;
    private int package_price;
    private int coupon_discount;

    public String getCommand() {
        return command;
    }

    public String getMcName() {
        return mc_name;
    }

    public String getUuid() {
        return uuid;
    }

    public String getPackageName() {
        return package_name;
    }

    public int getPackagePrice() {
        return package_price;
    }

    public int getCouponDiscount() {
        return coupon_discount;
    }

    @Override
    public String toString() {
        return command + " - " + mc_name + " - " + uuid + " - " + package_name + " - " + package_price + " - " + coupon_discount;
    }

}
