package com.example.sd;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import okhttp3.*;

import java.io.IOException;

public class NetworkClient {
    private static NetworkClient instance;
    private final OkHttpClient client;
    private final SharedPreferences prefs;
    private static final String PREFS_NAME = "MyDiffuGenPrefs";
    private static final String KEY_BASE_URL = "base_url";

    private NetworkClient(Context ctx) {
        client = new OkHttpClient();
        prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized NetworkClient getInstance(Context ctx) {
        if (instance == null) {
            instance = new NetworkClient(ctx.getApplicationContext());
        }
        return instance;
    }
    public void setBaseUrl(String url) {
        prefs.edit().putString(KEY_BASE_URL, url).apply();
    }

    public String getBaseUrl() {
        return prefs.getString(KEY_BASE_URL, "");
    }
    private String makeUrl(String path) {
        String base = getBaseUrl();
        if (!base.endsWith("/")) base += "/";
        if (path.startsWith("/")) path = path.substring(1);
        return base + path;
    }
    // GET запрос, callback в UI-потоке не реализован (вы сами должны вызывающий код запускать в другом потоке)
    public void doGet(String endpoint, Callback callback) {
        String url = makeUrl(endpoint);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }
    // POST с JSON-телом
    public void doPostJson(String endpoint, String jsonBody, Callback callback) {
        Log.d("REQUEST_JSON", "POST " + makeUrl(endpoint) + "  BODY:\n" + jsonBody);

        String url = makeUrl(endpoint);
        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json; charset=utf-8")
        );
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
