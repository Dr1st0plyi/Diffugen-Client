package com.example.sd;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

// Утилита для хранения/извлечения списка URL в SharedPreferences
public class GalleryStorage {
    private static final String PREFS_NAME = "MyDiffuGenPrefs";
    private static final String KEY_GALLERY = "gallery_urls";

    public static void addImageUrl(Context ctx, String url) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String existing = prefs.getString(KEY_GALLERY, "[]");
        try {
            JSONArray arr = new JSONArray(existing);
            arr.put(url);
            prefs.edit().putString(KEY_GALLERY, arr.toString()).apply();
        } catch (JSONException ignored) {}
    }

    public static ArrayList<String> getImageUrls(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String existing = prefs.getString(KEY_GALLERY, "[]");
        ArrayList<String> out = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(existing);
            for (int i = 0; i < arr.length(); i++) {
                out.add(arr.getString(i));
            }
        } catch (JSONException ignored) {}
        return out;
    }
}
