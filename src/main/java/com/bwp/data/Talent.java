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

import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Represents a Talent (person) sourced from TheMovieDB and persisted via QuiptMC Config.
 * <p>
 * A Talent stores basic profile information and a collection of credits (cast/crew).
 * The class can construct itself by calling TMDB and can also perform a sync that
 * loads combined credits. See TalentConfig for persistence and filesystem side-effects
 * such as headshot downloads.
 */
public class Talent extends ConfigObject {

    public String first_name;
    public String last_name;
    public String about;

    /**
     * Default no-arg constructor for frameworks and deserialization.
     */
    public Talent() {
        
    }

    /**
     * Constructs a Talent by fetching profile data from TMDB using the given person id.
     * Populates name and biography fields; does not persist automatically.
     *
     * @param apiId TMDB person id
     * @throws QuiptApiException if the remote API reports a failure
     * @throws org.json.JSONException if required fields are missing in the response
     */
    public Talent(int apiId) {
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

/**
 * Returns the display name built from first and last name.
 *
 * @return full name string
 */
public String fullName() {
    return first_name + " " + last_name;
}

/**
 * Synchronizes this Talent's credits by calling TMDB's combined_credits API and
 * updating the credits map. This method is network-bound and may throw runtime
 * exceptions if the response is malformed.
 */
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
//        try {
//            Files.write(Path.of("last_response.json"), combinedCredits.toString(2).getBytes());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
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


/**
 * Represents a single credit entry (cast or crew) associated with a Talent.
 */
public static class Credit extends ConfigObject {

    /** First known air or release date for this credit. */
    String first_credit_air_date;
    /** Title or name associated with the credit. */
    String name;
    /** Character (cast) or job (crew) associated with the credit. */
    String character;
    /** Media type (movie, tv, or unknown). */
    String media_type;
    public double popularity;
    public double vote_average;
    public int vote_count;
    public int episode_count;

    /**
     * Populates the credit from a TMDB JSON object, applying default values and
     * normalizing field names to the local schema.
     */
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

    /**
     * Factory for Credit objects used by the QuiptMC configuration system.
     */
    public static class Factory implements ConfigObject.Factory<Credit> {

        /**
         * Returns the binary class name recognized by the config factory registry.
         */
        @Override
        public String getClassName() {
            return Credit.class.getName();
        }
    }
}
}
