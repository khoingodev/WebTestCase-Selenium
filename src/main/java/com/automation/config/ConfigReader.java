package com.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigReader - Singleton, đọc file config.properties từ classpath.
 * Cung cấp các getter để lấy giá trị cấu hình (URL, credentials, browser,...).
 */
public class ConfigReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = ConfigReader.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new RuntimeException("Khong tim thay file config.properties trong classpath!");
            }
            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Loi doc file config.properties: " + e.getMessage(), e);
        }
    }

    private ConfigReader() {
    }

    public static String get(String key) {
        String value = PROPERTIES.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Khong tim thay key '" + key + "' trong config.properties");
        }
        return value;
    }

    public static String get(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
