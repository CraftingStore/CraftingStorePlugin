package net.craftingstore.bukkit.models;

import net.craftingstore.Category;
import net.craftingstore.Package;
import net.craftingstore.Payment;
import net.craftingstore.TopDonator;

import java.util.ArrayList;

public class QueryCache {

    private TopDonator[] topDonators;
    private Payment[] recentPayments;
    private Category[] categories;
    private Package[] packages;
    private ArrayList<String> inventories = new ArrayList<String>();

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

    public void addInventory(String playerName) {
        if (this.inventories.contains(playerName)) {
            return;
        }

        this.inventories.add(playerName);
    }

    public Boolean hasInventory(String playerName) {
        return this.inventories.contains(playerName);
    }

    public void removeInventory(String inventory) {
        this.inventories.remove(inventory);
    }

    public void setRecentPayments(Payment[] recentPayments) {
        this.recentPayments = recentPayments;
    }
}
