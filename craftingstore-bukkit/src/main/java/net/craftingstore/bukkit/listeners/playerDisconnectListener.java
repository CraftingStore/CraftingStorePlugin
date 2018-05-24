package net.craftingstore.bukkit.listeners;

import net.craftingstore.bukkit.CraftingStoreBukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class playerDisconnectListener implements Listener {

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent e) {

        // Get player
        Player player = e.getPlayer();

        // Remove inventory.
        CraftingStoreBukkit.getInstance().getQueryCache().removeInventory(player.getName());

    }

}
