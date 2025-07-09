package ca.hajjar.drivefree.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * Created by bilal on 2017-01-19.
 */

public class Config {
    private static final String APP_CONFIG = "DRIVE_FREE_CONFIG";
    private static final boolean DEFAULT_ENABLED = true;
    private boolean isEnabled;
    public static final int DEFAULT_RATION_CHECK = 15000;


    public Config() {
        this.isEnabled = DEFAULT_ENABLED;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public static final void saveConfig(Context context, Config config) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_CONFIG, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(config);
        editor.putString("Config", json);
        editor.commit();

    }

    public static final Config loadConfig(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_CONFIG, 0);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Config", "");
        if (json.isEmpty()) {
            return new Config();
        }
        Config config = gson.fromJson(json, Config.class);
        return config;


    }


}
