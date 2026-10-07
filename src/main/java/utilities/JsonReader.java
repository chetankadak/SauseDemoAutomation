package utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Reusable JSON Test Data Reader using Jackson Databind.
 * Allows retrieving test data directly via dot-notation path (e.g., "validUser.username").
 * Eliminates the need for DataProviders and Excel sheets while keeping test code clean and decoupled.
 */
public final class JsonReader {

    private static final String TEST_DATA_FILE = "testdata.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final JsonNode rootNode;

    static {
        try (InputStream inputStream = JsonReader.class.getClassLoader().getResourceAsStream(TEST_DATA_FILE)) {
            if (inputStream == null) {
                throw new RuntimeException("Test data file '" + TEST_DATA_FILE + "' not found in classpath.");
            }
            rootNode = objectMapper.readTree(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON test data from " + TEST_DATA_FILE, e);
        }
    }

    private JsonReader() {
        // Prevent instantiation
    }

    /**
     * Traverses the JSON tree using a dot-delimited path (e.g., "validUser.username").
     *
     * @param path dot-delimited path to the target node
     * @return JsonNode at the given path
     */
    public static JsonNode getNode(String path) {
        if (path == null || path.trim().isEmpty()) {
            return rootNode;
        }

        String[] tokens = path.split("\\.");
        JsonNode currentNode = rootNode;

        for (String token : tokens) {
            if (currentNode == null) {
                break;
            }
            currentNode = currentNode.get(token);
        }

        if (currentNode == null || currentNode.isMissingNode()) {
            throw new IllegalArgumentException("JSON path '" + path + "' was not found in " + TEST_DATA_FILE);
        }

        return currentNode;
    }

    /**
     * Retrieves a String value from the JSON test data file given its dot-delimited path.
     *
     * @param path dot-delimited path (e.g., "validUser.password", "products.backpack.price")
     * @return String value
     */
    public static String getTestData(String path) {
        return getNode(path).asText();
    }

    /**
     * Alias for getTestData.
     */
    public static String getString(String path) {
        return getTestData(path);
    }

    /**
     * Retrieves an Integer value from the JSON test data file.
     */
    public static int getInt(String path) {
        return getNode(path).asInt();
    }

    /**
     * Retrieves a Double value from the JSON test data file.
     */
    public static double getDouble(String path) {
        return getNode(path).asDouble();
    }

    /**
     * Retrieves a Boolean value from the JSON test data file.
     */
    public static boolean getBoolean(String path) {
        return getNode(path).asBoolean();
    }
}
