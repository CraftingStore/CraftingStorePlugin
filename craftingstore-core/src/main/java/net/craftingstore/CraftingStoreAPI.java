package net.craftingstore;

import com.google.gson.Gson;
import net.craftingstore.utils.HttpUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class CraftingStoreAPI {

    private static CraftingStoreAPI instance = new CraftingStoreAPI();

    public static CraftingStoreAPI getInstance() {
        return instance;
    }

    protected CraftingStoreAPI() {

    }

    public static final String API_URL = "http://api.craftingstore.net/v2/";

    private Gson gson = new Gson();

    public boolean checkKey(String key) throws Exception {
        String url = API_URL + key + "/check";
        String jsonString = HttpUtils.getJson(url);
        JSONObject json = (JSONObject) new JSONParser().parse(jsonString);
        JSONObject result = (JSONObject) json.get("result");
        return (Boolean) result.get("success");
    }

    public Donation[] getQueries(String key) throws Exception {
        return getQueries(key, true);
    }

    public Donation[] getQueries(String key, boolean remove) throws Exception {
        String url = API_URL + key + "/queries";
        if (remove) {
            url = url + "/remove";
        }

        String jsonString = HttpUtils.getJson(url);
        JSONObject json = (JSONObject) new JSONParser().parse(jsonString);
        JSONObject result = (JSONObject) json.get("result");

        return gson.fromJson(result.toString(), Donation[].class);
    }

    public Payment[] getPayments(String key) throws Exception {
        String url = API_URL + key + "/payments";

        String jsonString = HttpUtils.getJson(url);
        JSONObject json = (JSONObject) new JSONParser().parse(jsonString);
        JSONObject result = (JSONObject) json.get("result");

        return gson.fromJson(result.toJSONString(), Payment[].class);
    }

    public Package[] getPackages(String key) throws Exception {
        String url = API_URL + key + "/packages";

        String jsonString = HttpUtils.getJson(url);
        JSONObject json = (JSONObject) new JSONParser().parse(jsonString);
        JSONObject result = (JSONObject) json.get("result");

        return gson.fromJson(result.toJSONString(), Package[].class);
    }

}
