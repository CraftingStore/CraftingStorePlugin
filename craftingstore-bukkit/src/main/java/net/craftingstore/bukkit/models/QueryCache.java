package net.craftingstore.bukkit.models;

import net.craftingstore.Category;
import net.craftingstore.Package;
import net.craftingstore.Payment;
import net.craftingstore.TopDonator;

public class QueryCache {

    private TopDonator[] topDonators;
    private Payment[] recentPayments;
    private Category[] categories;
    private Package[] packages;

    public TopDonator[] getTopDonators() {
        return topDonators;
    }

    public Payment[] getRecentPayments() {
        return recentPayments;
    }

    public Category[] getCategories() {
        return categories;
    }

    public Package[] getPackages() {
        return packages;
    }

    public void setTopDonators(TopDonator[] topDonators) {
        this.topDonators = topDonators;
    }

    public void setCategories(Category[] category) {
        this.categories = category;
    }

    public void setPackages(Package[] packages) {
        this.packages = packages;
    }

    public void setRecentPayments(Payment[] recentPayments) {
        this.recentPayments = recentPayments;
    }
}
