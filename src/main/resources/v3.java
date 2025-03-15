import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonIdGenerator {

    // Map to track ID counters for each hierarchy level
    private static final Map<Integer, Integer> idCounters = new HashMap<>();

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // Static JSON String (without ID values)
        String jsonString = """
        {
          "risk_types": [
            {
              "name": "AML",
              "ratings": [
                {
                  "name": "IR",
                  "factors": [
                    {
                      "name": "Liquidity Risk",
                      "countries": [
                        {
                          "name": "USA",
                          "assessment_units": [
                            {
                              "name": "Investment Banking",
                              "segments": [
                                {
                                  "name": "Large Corporations",
                                  "subsegments": [
                                    {
                                      "name": "Corporate Bonds"
                                    },
                                    {
                                      "name": "Equity Funds"
                                    }
                                  ]
                                }
                              ]
                            }
                          ]
                        }
                      ]
                    }
                  ]
                }
              ]
            }
          ]
        }
        """;

        // Parse JSON from string
        JsonNode rootNode = mapper.readTree(jsonString);

        // Initialize counters for each level
        for (int i = 0; i < 10; i++) {
            idCounters.put(i, i * 100 + 1);
        }

        // Process JSON and add IDs
        processNode(rootNode, 0);

        // Convert back to JSON string and print
        String updatedJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
        System.out.println(updatedJson);
    }

    private static void processNode(JsonNode node, int level) {
        if (node.isArray()) {
            int idStart = idCounters.get(level);
            for (JsonNode child : node) {
                if (child.isObject()) {
                    ObjectNode objNode = (ObjectNode) child;
                    int newId = idStart++;

                    // Create a new object with "id" first
                    ObjectNode reorderedNode = createReorderedObject(objNode, newId);
                    
                    // Replace child node with reordered node
                    ((ArrayNode) node).set(node.indexOf(child), reorderedNode);
                }
                processNode(child, level + 1);
            }
            idCounters.put(level, idStart); // Update counter
        } else if (node.isObject()) {
            for (JsonNode child : node) {
                processNode(child, level);
            }
        }
    }

    private static ObjectNode createReorderedObject(ObjectNode originalNode, int idValue) {
        ObjectNode newNode = originalNode.objectNode();

        // Add ID first
        newNode.put("id", idValue);

        // Copy other fields
        Iterator<Map.Entry<String, JsonNode>> fields = originalNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            newNode.set(field.getKey(), field.getValue());
        }

        return newNode;
    }
}