package net.craftingstore.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class HttpUtils {


    public static String getJson(String urlString, String apiKey) throws Exception {

        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("token", apiKey);

        BufferedReader in = null;
        String inputLine;
        StringBuilder body;
        try {
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            body = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                body.append(inputLine);
            }
            in.close();

            return body.toString();
        } catch(IOException ioe) {
            // ignore
        } finally {
            try {
                if(in != null) {
                    in.close();
                }
            } catch(IOException ex) {
                // Ignore
            }
        }
        return null;
    }

    public static void postJson(String urlString, String apiKey, String Commands) throws Exception {

        String data = "";
        if (Commands != null) {
            data += "&" + URLEncoder.encode("removeIds", "UTF-8") + "=" + URLEncoder.encode(Commands, "UTF-8");
        }

        URL url = new URL(urlString);
        URLConnection con = url.openConnection();
        con.setDoOutput(true);
        con.setRequestProperty("token", apiKey);

        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
        wr.write(data);
        wr.flush();

        BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));

        StringBuilder buffer = new StringBuilder();
        int read;
        char[] chars = new char[1024];
        while ((read = rd.read(chars)) != -1) {
            buffer.append(chars, 0, read);
        }

        wr.close();
        rd.close();
    }

}
