package net.craftingstore.sponge.events;

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

import java.util.UUID;

public class DonationReceivedEvent extends AbstractEvent implements Cancellable {

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

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public Cause getCause() {
        return null;
    }
}