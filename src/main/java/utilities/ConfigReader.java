package utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class to read and manage configuration properties from config.properties.
 * Supports command-line / System property overrides (e.g., -Dbrowser=chrome -Dheadless=true).
 */
public final class ConfigReader {

    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE_PATH = "config.properties";

    static {
        try (InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE_PATH)) {
            if (inputStream == null) {
                throw new RuntimeException("Configuration file '" + CONFIG_FILE_PATH + "' not found in classpath.");
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration properties from " + CONFIG_FILE_PATH, e);
        }
    }

    private ConfigReader() {
        // Prevent instantiation
    }

    /**
     * Gets property value by key, checking System properties first for runtime override.
     *
     * @param key property key
     * @return property value as String, or null if not found
     */
    public static String getProperty(String key) {
        String systemProperty = System.getProperty(key);
        if (systemProperty != null && !systemProperty.trim().isEmpty()) {
            return systemProperty.trim();
        }
        String value = properties.getProperty(key);
        return (value != null) ? value.trim() : null;
    }

    /**
     * Gets property value with fallback default value.
     */
    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return (value != null) ? value : defaultValue;
    }

    public static String getBrowser() {
        return getProperty("browser", "edge");
    }

    public static String getUrl() {
        return getProperty("url", "https://www.saucedemo.com/");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless", "false"));
    }

    public static int getExplicitWait() {
        return Integer.parseInt(getProperty("explicitWait", "15"));
    }

    public static boolean isScreenshotOnFailure() {
        return Boolean.parseBoolean(getProperty("screenshotOnFailure", "true"));
    }

    public static boolean isVideoRecordingEnabled() {
        return Boolean.parseBoolean(getProperty("videoRecording", "true"));
    }
}
