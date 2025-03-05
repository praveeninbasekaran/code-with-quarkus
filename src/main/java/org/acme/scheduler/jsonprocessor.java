import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

public class CascadingDropdownHelper {

    private final ObjectMapper mapper;

    public CascadingDropdownHelper() {
        this.mapper = new ObjectMapper();
    }

    /**
     * Reads a JSON string and returns a Map of dropdown options for the level based on the provided selection.
     * If selectedValue is null or empty, it returns the top-level array values.
     *
     * @param jsonString    The JSON content as a String.
     * @param selectedValue The currently selected value (from the previous level), or null/empty for the first level.
     * @return A Map where the key is the array node name (like "risk_types", "ratings", etc.) and the value is a List of item names.
     * @throws IOException If JSON parsing fails.
     */
    public Map<String, List<String>> getDropdownItems(String jsonString, String selectedValue) throws IOException {
        JsonNode root = mapper.readTree(jsonString);

        if (selectedValue == null || selectedValue.trim().isEmpty()) {
            // No selection provided: return the first level options from any top-level array nodes.
            return extractDropdownFromRoot(root);
        } else {
            // A selection has been provided: find the node with that name and return its child arrays (if any)
            JsonNode target = findNodeByName(root, selectedValue);
            if (target != null) {
                return extractDropdownFromNode(target);
            }
        }
        return Collections.emptyMap();
    }

    /**
     * Extracts dropdown items from the top-level of the JSON.
     */
    private Map<String, List<String>> extractDropdownFromRoot(JsonNode root) {
        Map<String, List<String>> result = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            if (entry.getValue().isArray()) {
                List<String> names = new ArrayList<>();
                for (JsonNode node : entry.getValue()) {
                    if (node.has("name")) {
                        names.add(node.get("name").asText());
                    }
                }
                if (!names.isEmpty()) {
                    result.put(entry.getKey(), names);
                }
            }
        }
        return result;
    }

    /**
     * Extracts dropdown items from the given node by looking for its array fields.
     */
    private Map<String, List<String>> extractDropdownFromNode(JsonNode node) {
        Map<String, List<String>> result = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            if (entry.getValue().isArray()) {
                List<String> names = new ArrayList<>();
                for (JsonNode child : entry.getValue()) {
                    if (child.has("name")) {
                        names.add(child.get("name").asText());
                    }
                }
                if (!names.isEmpty()) {
                    result.put(entry.getKey(), names);
                }
            }
        }
        return result;
    }

    /**
     * Recursively searches for a JSON node that has a "name" field matching the provided value.
     *
     * @param node  The current node to search.
     * @param value The value to search for.
     * @return The matching JsonNode or null if not found.
     */
    private JsonNode findNodeByName(JsonNode node, String value) {
        if (node.isObject()) {
            if (node.has("name") && node.get("name").asText().equalsIgnoreCase(value)) {
                return node;
            }
            Iterator<JsonNode> children = node.elements();
            while (children.hasNext()) {
                JsonNode found = findNodeByName(children.next(), value);
                if (found != null) {
                    return found;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode item : node) {
                JsonNode found = findNodeByName(item, value);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    // Demo main method to show usage.
    public static void main(String[] args) {
        String json = "{\n" +
                "  \"risk_types\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"name\": \"Market Risk\",\n" +
                "      \"ratings\": [\n" +
                "        {\n" +
                "          \"id\": 101,\n" +
                "          \"name\": \"High\",\n" +
                "          \"factors\": [ /* factors omitted for brevity */ ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": 102,\n" +
                "          \"name\": \"Medium\",\n" +
                "          \"factors\": [ /* factors omitted for brevity */ ]\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 2,\n" +
                "      \"name\": \"Credit Risk\",\n" +
                "      \"ratings\": [\n" +
                "        {\n" +
                "          \"id\": 103,\n" +
                "          \"name\": \"Low\",\n" +
                "          \"factors\": [ /* factors omitted for brevity */ ]\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        CascadingDropdownHelper helper = new CascadingDropdownHelper();

        try {
            // 1. Get first level dropdown values.
            Map<String, List<String>> firstLevel = helper.getDropdownItems(json, null);
            System.out.println("First Level Dropdown Values:");
            firstLevel.forEach((k, v) -> System.out.println(k + ": " + v));

            // Suppose user selects "Market Risk" from the first dropdown.
            String userSelection = "Market Risk";

            // 2. Get next level dropdown values based on user selection.
            Map<String, List<String>> secondLevel = helper.getDropdownItems(json, userSelection);
            System.out.println("\nNext Level Dropdown Values for '" + userSelection + "':");
            secondLevel.forEach((k, v) -> System.out.println(k + ": " + v));

            // You can further cascade by taking one of the next-level values as input and calling getDropdownItems again.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}