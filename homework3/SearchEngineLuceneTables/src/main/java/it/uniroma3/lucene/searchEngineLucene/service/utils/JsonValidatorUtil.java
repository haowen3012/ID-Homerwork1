package it.uniroma3.lucene.searchEngineLucene.service.utils;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.IOException;

public class JsonValidatorUtil {

    public static boolean isValidJson(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            JSONTokener tokener = new JSONTokener(fis);
            new JSONObject(tokener);
            return true;
        } catch (IOException | org.json.JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
}