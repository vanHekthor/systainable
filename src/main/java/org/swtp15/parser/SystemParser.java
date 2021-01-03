package org.swtp15.parser;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Set;

public class SystemParser {

    /**
     * Converts the SystemNames to String.
     *
     * @param systemNames Set of system names
     *
     * @return JSON representation of SystemNames as String
     */
    @SuppressWarnings("unchecked")
    public static String parseSystemNamesToJson(Set<String> systemNames) {
        JSONObject root = new JSONObject();
        JSONArray names = new JSONArray();

        names.addAll(systemNames);
        root.put("systemNames", names);

        return root.toJSONString();
    }
}
