package com.example.sd;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;

// Модель для отображения: имя категории (flux или stable_diffusion) → список подмоделей
class ModelItem {
    String category;
    ArrayList<String> models;
    JSONObject defaultParams; // JSON-объект с default_params из сервера
    ModelItem(String category, ArrayList<String> models, JSONObject defaultParams) {
        this.category = category;
        this.models = models;
        this.defaultParams = defaultParams;
    }
}

// Адаптер для RecyclerView: показываем категорию и внутри — её подмодели + параметры
public class ModelsActivity extends AppCompatActivity {
    private RecyclerView rvModels;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_models);

        rvModels = findViewById(R.id.rvModels);
        progressBar = findViewById(R.id.pbModels);
        rvModels.setLayoutManager(new LinearLayoutManager(this));

        fetchModels();
    }

    private void fetchModels() {
        progressBar.setVisibility(View.VISIBLE);
        NetworkClient.getInstance(this)
                .doGet("models", new Callback() {
                    @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ModelsActivity.this, "Ошибка сети: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                    @Override public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String body = response.body() != null ? response.body().string() : "";
                        if (!response.isSuccessful()) {
                            runOnUiThread(() -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(ModelsActivity.this, "Ошибка сервера: HTTP " + response.code(), Toast.LENGTH_SHORT).show();
                            });
                            return;
                        }
                        try {
                            JSONObject root = new JSONObject(body);
                            JSONObject modelsObj = root.getJSONObject("models");
                            JSONObject defaultParamsObj = root.getJSONObject("default_params");

                            ArrayList<ModelItem> items = new ArrayList<>();

                            // Бежим по ключам categories: "flux" и "stable_diffusion"
                            JSONArray categories = modelsObj.names();
                            for (int i = 0; i < categories.length(); i++) {
                                String cat = categories.getString(i);
                                JSONArray arr = modelsObj.getJSONArray(cat);
                                ArrayList<String> subModels = new ArrayList<>();
                                for (int j = 0; j < arr.length(); j++) {
                                    subModels.add(arr.getString(j));
                                }
                                items.add(new ModelItem(cat, subModels, defaultParamsObj));
                            }

                            runOnUiThread(() -> {
                                progressBar.setVisibility(View.GONE);
                                ModelsAdapter adapter = new ModelsAdapter(items);
                                rvModels.setAdapter(adapter);
                            });
                        } catch (JSONException ex) {
                            Log.e("ModelsActivity", "JSON parse error", ex);
                            runOnUiThread(() -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(ModelsActivity.this, "Ошибка разбора JSON", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                });
    }
}
