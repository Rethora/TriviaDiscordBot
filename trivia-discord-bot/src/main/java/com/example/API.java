package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class API {
    /**
     * Fetches a session token from the API.
     *
     * @return The session token as a String.
     * @throws IOException        If an I/O error occurs while making the API
     *                            request.
     * @throws URISyntaxException If the API URL is not a valid URI.
     */
    private static String fetchSessionToken() throws IOException, URISyntaxException {
        String apiUrl = "https://opentdb.com/api_token.php?command=request";
        URL url = new URI(apiUrl).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String jsonResponse = reader.readLine();
            reader.close();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            String token = jsonNode.get("token").asText();

            App.setSessionToken(token);
            return token;
        } else {
            throw new IOException("API request failed with response code: " + responseCode);
        }
    }

    /**
     * Fetches trivia data from the API and returns it as a JsonNode object.
     *
     * @param args a HashMap containing optional arguments for the API request
     * @return a JsonNode object representing the fetched trivia data
     * @throws URISyntaxException if the URL is invalid
     * @throws IOException        if an I/O error occurs during the API request
     */
    public static JsonNode fetchWithRefresh(HashMap<String, String> args)
            throws URISyntaxException, IOException {
        String token = null;
        StringBuilder stringUrl = new StringBuilder("https://opentdb.com/api.php?amount=1&encode=base64&type=multiple");

        if (App.getSessionToken() == null) {
            try {
                token = fetchSessionToken();
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }

        if (args.containsKey("difficulty")) {
            String difficulty = args.get("difficulty");
            stringUrl.append("&difficulty=").append(difficulty);
        }

        if (args.containsKey("category")) {
            String category = args.get("category");
            stringUrl.append("&category=").append(category);
        }

        if (token != null) {
            stringUrl.append("&token=").append(token);
        }

        URL url = new URI(stringUrl.toString()).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String jsonResponse = reader.readLine();
            reader.close();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);

            return jsonNode;
        } else {
            throw new IOException("API request failed with response code: " + responseCode);
        }
    }

    /**
     * Retrieves the categories from the API and returns them as a formatted string.
     *
     * @return A string containing the categories and their corresponding IDs.
     */
    public static String getCategories() {
        String apiUrl = "https://opentdb.com/api_category.php";
        try {
            URL url = new URI(apiUrl).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String jsonResponse = reader.readLine();
                reader.close();

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(jsonResponse);
                JsonNode categories = jsonNode.get("trivia_categories");

                StringBuilder sb = new StringBuilder();
                for (JsonNode category : categories) {
                    sb.append(category.get("name").asText()).append(" - ").append(category.get("id")).append("\n");
                }

                return sb.toString();
            } else {
                throw new IOException("API request failed with response code: " + responseCode);
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return "Problem occurred getting categories";
        }
    }
}