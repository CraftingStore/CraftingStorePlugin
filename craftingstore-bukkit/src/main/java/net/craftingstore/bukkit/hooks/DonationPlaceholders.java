package net.craftingstore.bukkit.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import net.craftingstore.Payment;
import net.craftingstore.TopDonator;
import net.craftingstore.bukkit.CraftingStoreBukkit;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DonationPlaceholders extends PlaceholderHook {

    private CraftingStoreBukkit instance;

    public DonationPlaceholders(CraftingStoreBukkit instance) {
        this.instance = instance;
        PlaceholderAPI.registerPlaceholderHook("craftingstore", this);
    }

    public String onPlaceholderRequest(Player player, String s) {
        if (s.startsWith("donator")) {
            return handleDonators(player, s);
        } else if (s.startsWith("payment")) {
            return handlePayments(player, s);
        }

        return null;
    }

    private String handleDonators(Player player, String s) {
        if (instance.getQueryCache().getTopDonators() == null) {
            return null; // Donators are not retrieved yet.
        } else if (s.equalsIgnoreCase("donator")) {
            StringBuilder builder = new StringBuilder();
            for (TopDonator donator : instance.getQueryCache().getTopDonators()) {
                builder.append(donator.getUsername()).append(": ").append(donator.getTotal()).append(", ");
            }
            builder.substring(0, builder.length() - 2); // Remove the last ', ' from the string
            return builder.toString();
        } else if (s.startsWith("donator_")) {
            Pattern pattern = Pattern.compile("donator_([1-5])");
            Matcher matcher = pattern.matcher(s);
            if (matcher.matches()) {
                int id = Integer.parseInt(matcher.group(1));
                if (instance.getQueryCache().getTopDonators().length >= id) {
                    id--; // Zero based array
                    TopDonator donator = instance.getQueryCache().getTopDonators()[id];
                    return donator.getUsername() + ": " + donator.getTotal();
                }
            }
        }
        return null;
    }

    private String handlePayments(Player player, String s) {
        if (instance.getQueryCache().getRecentPayments() == null) {
            return null; // Recent payments are not retrieved yet.
        } else if (s.equalsIgnoreCase("payment")) {
            StringBuilder builder = new StringBuilder();
            for (Payment payment : instance.getQueryCache().getRecentPayments()) {
                builder.append(payment.getUsername()).append(": ").append(payment.getPackageName()).append(", ");
            }
            builder.substring(0, builder.length() - 2); // Remove the last ', ' from the string
            return builder.toString();
        } else if (s.startsWith("payment_")) {
            Pattern pattern = Pattern.compile("payment_([1-5])");
            Matcher matcher = pattern.matcher(s);
            if (matcher.matches()) {
                int id = Integer.parseInt(matcher.group(1));
                if (instance.getQueryCache().getRecentPayments().length >= id) {
                    id--; // Zero based array
                    Payment payment = instance.getQueryCache().getRecentPayments()[id];
                    return payment.getUsername() + ": " + payment.getPackageName();
                }
            }
        }
        return null;
    }
}
