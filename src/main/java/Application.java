import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Application {
    private static final String API_URL = "http://94.198.50.185:7081/api/users/";

    public static void main(String[] args) {
        List<HttpCookie> cookies;
        ////////////GET
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            String cookiesHeader = connection.getHeaderField("Set-Cookie");
            cookies = HttpCookie.parse(cookiesHeader);
            System.out.println(cookies.get(0));
            System.out.println(connection.getResponseCode());
            connection.disconnect();
            ///////////////////////////////POST
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            User user = new User(3L, "James", "Brown", (byte) 23);
            inputOutObjectJson(connection, user, cookies);

            /////////////////////////////////PUT
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            User updateUser = new User(3L, "Thomas", "Shelby", (byte) 23);
            inputOutObjectJson(connection, updateUser, cookies);
            ///////////////////DELETE
            URL urlDelete = new URL("http://94.198.50.185:7081/api/users/" + updateUser.getId());
            connection = (HttpURLConnection) urlDelete.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("Cookie", String.valueOf(cookies.get(0)));
            connection.setRequestMethod("DELETE");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(connection.getResponseCode());
            connection.disconnect();


        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    private static void inputOutObjectJson(HttpURLConnection connection, Object object, List<HttpCookie> cookies) {
        connection.setRequestProperty("Content-Type", "application/json");
        for (HttpCookie httpCookie : cookies) {
            connection.addRequestProperty("Cookie", String.valueOf(httpCookie));
        }
        connection.setDoOutput(true);
        ObjectMapper mapper = new ObjectMapper();
        StringBuilder response;
        String jsonUserString = null;
        try {
            jsonUserString = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            System.out.println("Not Objects and null");
            throw new RuntimeException(e);
        }
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonUserString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response);
            System.out.println(connection.getResponseCode());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        connection.disconnect();
    }
}
