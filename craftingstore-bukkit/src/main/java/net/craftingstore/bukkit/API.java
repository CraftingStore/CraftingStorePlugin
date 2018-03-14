package net.craftingstore.bukkit;

import net.craftingstore.CraftingStoreAPI;

import java.util.logging.Level;

/**
 * @deprecated Replaced by {@link net.craftingstore.CraftingStoreAPI}
 */
@Deprecated
public class API {

    public static String getFullUrl() {
        return CraftingStoreAPI.API_URL;
    }

    public static boolean checkKey(String apikey) {
        try {
            return CraftingStoreAPI.getInstance().checkKey(apikey);
        } catch (Exception e) {
            CraftingStoreBukkit.getInstance().getLogger().log(Level.SEVERE, "An error occurred while checking the API key.", e);
        }
        return false;
    }

}
