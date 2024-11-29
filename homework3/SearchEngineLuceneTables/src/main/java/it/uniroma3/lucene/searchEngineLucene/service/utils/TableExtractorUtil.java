package it.uniroma3.lucene.searchEngineLucene.service.utils;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TableExtractorUtil {

    public static JSONObject extractJsonObject(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            JSONTokener tokener = new JSONTokener(fis);
            return new JSONObject(tokener);
        } catch (IOException e) {
            System.out.println("Error while reading file: " + filePath);
            return null;
        }
    }

    public static List<JSONObject> getTableObjects(String filePath) {
        List<JSONObject> tableObjects = new ArrayList<>();
        JSONObject jsonObject = extractJsonObject(filePath);
        if (jsonObject != null) {
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                if (key.matches("S\\d+\\.T\\d+")  || key.matches("A\\d+\\.T\\d+")|| key.matches("id_table_\\d+")) {
                    tableObjects.add(jsonObject.getJSONObject(key));
                }
            }
        }
        return tableObjects;
    }

}