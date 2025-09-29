package com.bwp.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiptmc.core.config.ConfigManager;
import com.quiptmc.core.config.ConfigTemplate;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Iterator;

public class ConfigTests {

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JSONObject root = ConfigManager.loadJson(new File("data/talents.json"), ConfigTemplate.Extension.JSON);
        JSONObject credits = root.getJSONObject("talents").getJSONObject("25451").getJSONObject("credits");

        try (PrintWriter writer = new PrintWriter(new FileWriter("credits.csv"))) {
            writer.println("id,character,media_type,name,first_credit_air_date,popularity,vote_average,vote_count,episode_count");
            for (String key : credits.keySet()) {
                JSONObject credit = credits.getJSONObject(key);
                writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                        escape(credit.getString("id")),
                        escape(credit.getString("character")),
                        escape(credit.getString("media_type")),
                        escape(credit.getString("name")),
                        escape(credit.getString("first_credit_air_date")),
                        escape(String.valueOf(credit.getDouble("popularity"))),
                        escape(String.valueOf(credit.getDouble("vote_average"))),
                        escape(String.valueOf(credit.getInt("vote_count"))),
                        escape(String.valueOf(credit.getInt("episode_count")))

                );
            }

        }
    }

    private static String escape(String value) {
        if (value.contains(",") || value.contains("\"")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
    }

}
