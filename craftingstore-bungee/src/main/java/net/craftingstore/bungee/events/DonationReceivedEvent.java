package net.craftingstore.bungee.events;

import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

public class DonationReceivedEvent extends Event implements Cancellable {

    private String command;
    private String username;
    private UUID uuid;
    private String packageName;
    private int packagePrice;
    private int couponDiscount;

    private boolean cancelled = false;

    public DonationReceivedEvent(String command, String username, UUID uuid, String packageName, int packagePrice, int couponDiscount) {
        this.command = command;
        this.username = username;
        this.uuid = uuid;
        this.packageName = packageName;
        this.packagePrice = packagePrice;
        this.couponDiscount = couponDiscount;
    }

    public String getCommand() {
        return command;
    }

    public String getUsername() {
        return username;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getPackageName() {
        return packageName;
    }

    public int getPackagePrice() {
        return packagePrice;
    }

    public int getCouponDiscount() {
        return couponDiscount;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
