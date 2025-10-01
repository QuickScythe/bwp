package com.bwp.data;

import com.bwp.Main;
import com.bwp.data.config.DefaultConfig;
import com.bwp.utils.Utils;
import com.quiptmc.core.config.ConfigManager;
import com.quiptmc.core.config.ConfigMap;
import com.quiptmc.core.config.ConfigObject;
import com.quiptmc.core.exceptions.QuiptApiException;
import com.quiptmc.core.utils.net.HttpConfig;
import com.quiptmc.core.utils.net.HttpHeaders;
import com.quiptmc.core.utils.net.NetworkUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

public class Actor extends ConfigObject {

    public String first_name;
    public String last_name;
    public String about;

    public Actor() {

    }

    public Actor(int apiId) {
        this.id = String.valueOf(apiId);
        String url = "https://api.themoviedb.org/3/person/" + id;
        String token = ConfigManager.getConfig(Main.INTEGRATION, DefaultConfig.class).api_token;
        HttpConfig get = HttpConfig.config(
                Duration.ofSeconds(10),
                10,
                false,
                true,
                "application/json",
                "UTF-8",
                HttpHeaders.AUTHORIZATION_BEARER(token)
        );
        HttpResponse<String> response = NetworkUtils.get(get, url);
        JSONObject data = new JSONObject(response.body());
        if(data.has("success") && !data.getBoolean("success")){
            throw new QuiptApiException("Failed to fetch actor data: " + data.optString("status_message", "Unknown error"));
        }
//        System.out.println(data.toString(2));
        if (!data.has("biography"))
            throw new JSONException("Missing data (biography) in actor response");
        about = data.getString("biography");
        if (!data.has("name"))
            throw new JSONException("Missing data (name) in actor response");
        first_name = data.getString("name").split(" ")[0];
        last_name = data.getString("name").substring(first_name.length()).trim();
}


ConfigMap<Credit> credits = new ConfigMap<>();

public String fullName() {
    return first_name + " " + last_name;
}

public void sync() {
    String url = "https://api.themoviedb.org/3/person/" + id + "?append_to_response=combined_credits";

    HttpResponse<String> response = NetworkUtils.get(Utils.GET(), url);
    JSONObject data = new JSONObject(response.body());
    if (data.has("success") && !data.getBoolean("success")) {
        System.out.println("Failed to fetch actor data: " + data.optString("status_message", "Unknown error"));
        return;
    }
    if (!data.has("combined_credits")) {
        System.out.println("No combined credits found for actor.");
        return;
    }
    JSONObject combinedCredits = data.getJSONObject("combined_credits");
//        System.out.println(combinedCredits.toString(2));
    if (combinedCredits.has("cast")) {
        try {
            Files.write(Path.of("last_response.json"), combinedCredits.toString(2).getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (Object obj : combinedCredits.getJSONArray("cast")) {
            JSONObject creditJson = (JSONObject) obj;
//                System.out.println(creditJson.toString(2));
            Credit credit = new Credit();
            try {
                credit.fromJson(creditJson);
                System.out.println("Parsed credit: " + credit.name + " as " + credit.character + " in " + credit.first_credit_air_date + " (" + credit.media_type + ")");
            } catch (Exception e) {
                System.out.println("Failed to parse credit: " + e.getMessage());
                System.err.println(creditJson.toString(2));
                e.printStackTrace();

                throw new RuntimeException(e);
            }
//                credit.id = String.valueOf(creditJson.getInt("id"));
//                credit.name = creditJson.optString("title", creditJson.optString("name", "Unknown"));
//                credit.character = creditJson.optString("character", "Unknown");
//                credit.first_credit_air_date = creditJson.optString("release_date", creditJson.optString("first_air_date", "Unknown"));
            credits.put(credit);
        }
    }
    if (combinedCredits.has("crew")) {
        for (Object obj : combinedCredits.getJSONArray("crew")) {
            JSONObject creditJson = (JSONObject) obj;
//                System.out.println(creditJson.toString(2));

            Credit credit = new Credit();
            credit.id = String.valueOf(creditJson.getInt("id"));
            credit.name = creditJson.optString("title", creditJson.optString("name", "Unknown"));
            credit.character = creditJson.optString("job", "Unknown");
            credit.first_credit_air_date = creditJson.optString("release_date", creditJson.optString("first_air_date", "Unknown"));
            credits.put(credit);
        }
    }

//        System.out.println(response.body());


}


public static class Credit extends ConfigObject {

    String first_credit_air_date;
    String name;
    String character;
    String media_type;
    public double popularity;
    public double vote_average;
    public int vote_count;
    public int episode_count;

    @Override
    public void fromJson(JSONObject json) {
//            System.out.println(json.toString(2));
        System.out.print("Parsing new credit..");
        if (!(json.get("id") instanceof String)) {
            json.put("id", String.valueOf(json.get("id")));
        }
        System.out.println(" ID: " + json.getString("id"));

        if (!json.has("episode_count")) {
            json.put("episode_count", 1);
        }
        if (!json.has("first_credit_air_date") || json.get("first_credit_air_date") == JSONObject.NULL) {
            System.out.println("Did not contain first_credit_air_date, checking release_date and first_air_date");
            json.put("first_credit_air_date", !json.has("release_date") ? json.optString("first_air_date", "Unknown Date") : json.getString("release_date"));

        }

        if (!json.has("name")) {
            System.out.println("Did not contain name, checking title");
            json.put("name", json.optString("title", "Unknown"));
        }
        if (!json.has("character")) {
            System.out.println("Did not contain character, checking job");
            json.put("character", json.optString("job", "Unknown"));
        }
        if (!json.has("media_type")) {
            System.out.println("Did not contain media_type, setting to unknown");
            json.put("media_type", "unknown");
        }

        super.fromJson(json);
    }

    public static class Factory implements ConfigObject.Factory<Credit> {

        @Override
        public String getClassName() {
            return Credit.class.getName();
        }
    }
}
}
