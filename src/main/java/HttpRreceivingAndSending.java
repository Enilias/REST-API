import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpRreceivingAndSending {
    @SuppressWarnings("unused")
    public static JSONArray httpGetJson(String url) {
        JSONArray jsonArray = null;
        try {
            jsonArray = httpGetJsonAndCookie(url).getJSONArray(0);
        } catch (JSONException e) {
            e.getStackTrace();
        }
        return jsonArray;
    }

    @SuppressWarnings("unchecked")
    public static List<HttpCookie> httpGetCookie(String url) {
        List<HttpCookie> httpCookieList = null;
        try {
            httpCookieList = (List<HttpCookie>) httpGetJsonAndCookie(url).get(1);
        } catch (JSONException e) {
            e.getStackTrace();
        }
        return httpCookieList;
    }


    @SneakyThrows
    public static List<String> httpPostPutObject(String url, String method, List<HttpCookie> cookies, Identifiable object) {
        List<String> list = new ArrayList<>();
        HttpURLConnection connection;
        if (method.equalsIgnoreCase("DELETE")) {
            connection = (HttpURLConnection) new URL(url + object.getId()).openConnection();
        } else {
            connection = (HttpURLConnection) new URL(url).openConnection();
        }
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json");
        for (HttpCookie httpCookie : cookies) {
            connection.addRequestProperty("Cookie", String.valueOf(httpCookie));
        }
        connection.setDoOutput(true);
        String jsonUserString = new ObjectMapper().writeValueAsString(object);


        byte[] input = jsonUserString.getBytes(StandardCharsets.UTF_8);
        connection.getOutputStream().write(input, 0, input.length);

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        connection.disconnect();
        list.add(new String(response));
        list.add(String.valueOf(connection.getResponseCode()));

        return list;
    }

    @SneakyThrows
    private static JSONArray httpGetJsonAndCookie(String url) {
        JSONArray jsonArray = null;
        StringBuilder response = new StringBuilder();
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                }

                List<HttpCookie> cookies = HttpCookie.parse(connection.getHeaderField("Set-Cookie"));
                jsonArray = new JSONArray();
                jsonArray.put(0, response.toString());
                jsonArray.put(1, cookies);
                jsonArray.put(2, connection.getResponseCode());
            } else {
                System.out.printf("Error code: " + responseCode);
            }
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return jsonArray;
    }
}


