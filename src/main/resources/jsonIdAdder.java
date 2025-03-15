import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonIdGenerator {

    // Map to track ID counters for each hierarchy level
    private static final Map<Integer, Integer> idCounters = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // Load JSON from file or string
        JsonNode rootNode = mapper.readTree(new File("input.json"));

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
                    ((ObjectNode) child).put("id", idStart++);
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
}