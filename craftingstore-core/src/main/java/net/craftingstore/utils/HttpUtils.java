package net.craftingstore.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class HttpUtils {

    public static String getJson(String urlString, String apiKey, String Commands) throws Exception {

        String data = URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(apiKey, "UTF-8");
        if (Commands != null) {
            data += "&" + URLEncoder.encode("removeIds", "UTF-8") + "=" + URLEncoder.encode(Commands, "UTF-8");
        }

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(data);
        wr.flush();

        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        StringBuilder buffer = new StringBuilder();
        int read;
        char[] chars = new char[1024];
        while ((read = rd.read(chars)) != -1) {
            buffer.append(chars, 0, read);
        }

        wr.close();
        rd.close();

        return buffer.toString();
    }

}
