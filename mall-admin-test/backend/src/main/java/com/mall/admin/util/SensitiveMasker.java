package com.mall.admin.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

/**
 * Mask sensitive fields before writing logs / operation_logs.
 */
public final class SensitiveMasker {

    private static final Set<String> SENSITIVE_KEYS = Set.of(
            "password", "oldpassword", "newpassword", "token", "accesstoken",
            "refreshtoken", "authorization", "secret"
    );

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private SensitiveMasker() {
    }

    public static String maskJson(String raw) {
        if (raw == null || raw.isBlank()) {
            return raw;
        }
        try {
            JsonNode node = MAPPER.readTree(raw);
            maskNode(node);
            return MAPPER.writeValueAsString(node);
        } catch (Exception ex) {
            return "[unparseable-body]";
        }
    }

    public static String maskObject(Object body) {
        if (body == null) {
            return null;
        }
        try {
            JsonNode node = MAPPER.valueToTree(body);
            maskNode(node);
            return MAPPER.writeValueAsString(node);
        } catch (Exception ex) {
            return "[unserializable-body]";
        }
    }

    private static void maskNode(JsonNode node) {
        if (node == null || !node.isObject()) {
            return;
        }
        ObjectNode objectNode = (ObjectNode) node;
        Iterator<String> names = objectNode.fieldNames();
        while (names.hasNext()) {
            String name = names.next();
            JsonNode child = objectNode.get(name);
            if (SENSITIVE_KEYS.contains(name.toLowerCase(Locale.ROOT))) {
                objectNode.put(name, "******");
            } else if (child != null && child.isObject()) {
                maskNode(child);
            } else if (child != null && child.isArray()) {
                child.forEach(SensitiveMasker::maskNode);
            }
        }
    }
}
