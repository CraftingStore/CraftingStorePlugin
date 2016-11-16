package net.craftingstore.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpUtils {

    public static String getJson(String urlString) throws Exception {
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(urlString);
            inputStreamReader = new InputStreamReader(url.openStream());
            bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = bufferedReader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }

            return buffer.toString();
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }

            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
        }
    }

    public static boolean checkApiKey(Logger logger, String apiUrl) {
        try {
            String json = HttpUtils.getJson(apiUrl + "/check");
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(json);
            return (Boolean) object.get("success");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred while checking the API key.", e);
        }
        return false;
    }

}
