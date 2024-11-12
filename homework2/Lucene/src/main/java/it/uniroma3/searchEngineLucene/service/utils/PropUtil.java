package it.uniroma3.searchEngineLucene.service.utils;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public class PropUtil {
    private static Properties properties = new Properties();

    // Static block to load the properties file when the class is loaded
    static {
        try (InputStream inputStream = PropUtil.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);  // Load the properties from the file
            } else {
                System.err.println("Properties file not found!");
            }
        } catch (IOException e) {
            e.printStackTrace();  // Handle exceptions properly in production code
        }
    }

    // Method to retrieve property value by key
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}