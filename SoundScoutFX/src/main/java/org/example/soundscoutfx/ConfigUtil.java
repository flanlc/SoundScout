package org.example.soundscoutfx;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {
    private static Properties properties = new Properties();

    //load properties when the class is first used
    static {
        try (InputStream input = new FileInputStream("SoundScoutFX/config.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //get the API key from properties
    public static String getApiKey() {
        return properties.getProperty("GOOGLE_API_KEY");
    }
}
