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

    private CraftingStoreAPI() {

    }

    public static final String API_URL = "https://api.craftingstore.net/v4/";

    private Gson gson = new Gson();

    public boolean checkKey(String key) throws Exception {
        String url = API_URL + "validateToken";
        String json = HttpUtils.getJson(url, key);

        Type type = new TypeToken<Root<Result>>() {}.getType();
        Root<Result> result = gson.fromJson(json, type);
        return result.getSuccess();
    }

    public Socket getSocket(String key) throws Exception {
        String url = API_URL + "socket";
        String json = HttpUtils.getJson(url, key);

        Type type = new TypeToken<Root<Socket>>() {}.getType();
        Root<Socket> socket = gson.fromJson(json, type);
        return socket.getResult();
    }

    public Donation[] getQueries(String key) throws Exception {
        String url = API_URL + "queue";
        String json = HttpUtils.getJson(url, key);

        Type type = new TypeToken<Root<Donation[]>>() {}.getType();
        Root<Donation[]> donations = gson.fromJson(json, type);
        return donations.getResult();
    }

    public void completeCommands(String key, String commandIds) throws Exception {
        String url = API_URL + "queue/markComplete";

        HttpUtils.postJson(url, key, commandIds);
    }

    public Payment[] getPayments(String key) throws Exception {
        String url = API_URL + "buyers/recent";
        String json = HttpUtils.getJson(url, key);

        Type type = new TypeToken<Root<Payment[]>>() {}.getType();
        Root<Payment[]> payments = gson.fromJson(json, type);
        return payments.getResult();
    }

    public Package[] getPackages(String key) throws Exception {
        String url = API_URL + "packages";
        String json = HttpUtils.getJson(url, key);

        Type type = new TypeToken<Root<Package[]>>() {}.getType();
        Root<Package[]> packages = gson.fromJson(json, type);
        return packages.getResult();
    }

    public TopDonator[] getTopDonators(String key) throws Exception {
        String url = API_URL + "buyers/top";
        String json = HttpUtils.getJson(url, key);

        Type type = new TypeToken<Root<TopDonator[]>>() {}.getType();
        Root<TopDonator[]> donators = gson.fromJson(json, type);
        return donators.getResult();
    }
}
