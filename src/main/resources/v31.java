private static void processNode(JsonNode node, int level) {
    if (node.isArray()) {
        int idStart = idCounters.get(level);

        // Convert to ArrayNode for modification
        ArrayNode arrayNode = (ArrayNode) node;
        for (int i = 0; i < arrayNode.size(); i++) {
            JsonNode child = arrayNode.get(i);
            if (child.isObject()) {
                ObjectNode objNode = (ObjectNode) child;
                int newId = idStart++;

                // Create a new object with "id" first
                ObjectNode reorderedNode = createReorderedObject(objNode, newId);

                // Replace child node with reordered node
                arrayNode.set(i, reorderedNode);
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