package net.craftingstore;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.craftingstore.utils.HttpUtils;

import java.lang.reflect.Type;

public class CraftingStoreAPI {

    private static CraftingStoreAPI instance = new CraftingStoreAPI();

    public static CraftingStoreAPI getInstance() {
        return instance;
    }

    protected CraftingStoreAPI() {

    }

    public static final String API_URL = "https://api.craftingstore.net/v3/";

    private Gson gson = new Gson();

    public boolean checkKey(String key) throws Exception {
        String url = API_URL + "check";
        String json = HttpUtils.getJson(url, key, null);

        Type type = new TypeToken<Root<Result>>() {}.getType();
        Root<Result> result = gson.fromJson(json, type);
        return result.getResult().getSuccess();
    }

    public boolean storePremium(String key) throws Exception {
        String url = API_URL + "check";
        String json = HttpUtils.getJson(url, key, null);

        Type type = new TypeToken<Root<Result>>() {}.getType();
        Root<Result> result = gson.fromJson(json, type);
        return result.getResult().getPremium();
    }

    public Donation[] getQueries(String key) throws Exception {
        return getQueries(key, false);
    }

    public Donation[] getQueries(String key, boolean remove) throws Exception {
        String url = API_URL + "queries";
        if (remove) {
            url = url + "/remove";
        }
        String json = HttpUtils.getJson(url, key, null);

        Type type = new TypeToken<Root<Donation[]>>() {}.getType();
        Root<Donation[]> donations = gson.fromJson(json, type);
        return donations.getResult();
    }

    public void completeCommands(String key, String commandIds) throws Exception {
        String url = API_URL + "queries/complete";

        String json = HttpUtils.getJson(url, key, commandIds);
    }

    public Payment[] getPayments(String key) throws Exception {
        String url = API_URL + "payments";
        String json = HttpUtils.getJson(url, key, null);

        Type type = new TypeToken<Root<Payment[]>>() {}.getType();
        Root<Payment[]> payments = gson.fromJson(json, type);
        return payments.getResult();
    }

    public Package[] getPackages(String key) throws Exception {
        String url = API_URL + "packages";
        String json = HttpUtils.getJson(url, key, null);

        Type type = new TypeToken<Root<Package[]>>() {}.getType();
        Root<Package[]> packages = gson.fromJson(json, type);
        return packages.getResult();
    }

    public TopDonator[] getTopDonators(String key) throws Exception {
        String url = API_URL + "toppayments";
        String json = HttpUtils.getJson(url, key, null);

        Type type = new TypeToken<Root<TopDonator[]>>() {}.getType();
        Root<TopDonator[]> donators = gson.fromJson(json, type);
        return donators.getResult();
    }
}
