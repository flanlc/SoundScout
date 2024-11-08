package org.example.soundscoutfx;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONObject;

public class LocationHelper {
    public static double[] getCoordinates(String location) {
        double[] coordinates = new double[2];
        try {
            if (location == null || location.trim().isEmpty()) {
                System.err.println("Error: Location is null or empty.");
                return coordinates;
            }

            String encodedLocation = URLEncoder.encode(location, "UTF-8");

            //grab the API key from the configuration file using ConfigUtil
            String apiKey = ConfigUtil.getApiKey();
            if (apiKey == null || apiKey.trim().isEmpty()) {
                System.err.println("Error: API key is missing or invalid.");
                return coordinates;
            }

            String urlString = "https://maps.googleapis.com/maps/api/geocode/json?address=" + encodedLocation + "&key=" + apiKey;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject json = new JSONObject(response.toString());

            if (json.getJSONArray("results").length() == 0) {
                return coordinates;
            }

            JSONObject locationObject = json.getJSONArray("results")
                    .getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONObject("location");

            coordinates[0] = locationObject.getDouble("lat");
            coordinates[1] = locationObject.getDouble("lng");
        } catch (Exception e) {
            System.err.println("Error fetching coordinates for location: " + location);
            e.printStackTrace();
        }
        return coordinates;
    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon1 - lon2);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = R * c;
        return distance * 0.621371;
    }
}
