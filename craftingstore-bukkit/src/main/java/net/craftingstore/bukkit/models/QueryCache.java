package net.craftingstore.bukkit.models;

import net.craftingstore.Payment;
import net.craftingstore.TopDonator;

public class QueryCache {

    private TopDonator[] topDonators;
    private Payment[] recentPayments;


    public TopDonator[] getTopDonators() {
        return topDonators;
    }

    public Payment[] getRecentPayments() {
        return recentPayments;
    }

    public void setTopDonators(TopDonator[] topDonators) {
        this.topDonators = topDonators;
    }

    public void setRecentPayments(Payment[] recentPayments) {
        this.recentPayments = recentPayments;
    }
}
