package com.example.sd;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;

public class GenerateActivity extends AppCompatActivity {
    private EditText etPrompt, etWidth, etHeight, etSteps, etCfg, etSeed;
    private Spinner spinnerModel, spinnerSample;
    private Button btnGenerate;
    private ProgressBar progressBar;

    private ArrayList<String> allModels = new ArrayList<>();
    private ArrayAdapter<String> modelAdapter;
    private String samplingMethods[] = {"euler", "dpm2", "dpm2_ancestral", "heun", "euler_a"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);

        etPrompt = findViewById(R.id.etPrompt);
        etWidth = findViewById(R.id.etWidth);
        etHeight = findViewById(R.id.etHeight);
        etSteps = findViewById(R.id.etSteps);
        etCfg = findViewById(R.id.etCfg);
        etSeed = findViewById(R.id.etSeed);
        spinnerModel = findViewById(R.id.spinnerModel);
        spinnerSample = findViewById(R.id.spinnerSample);
        btnGenerate = findViewById(R.id.btnGenerate);
        progressBar = findViewById(R.id.pbGenerate);

        // Инициализируем Spinner для sampling_method
        ArrayAdapter<String> sampleAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, samplingMethods);
        sampleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSample.setAdapter(sampleAdapter);

        // Инициализируем адаптер для моделей (пока пустой)
        modelAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, allModels);
        modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModel.setAdapter(modelAdapter);

        // Сначала грузим список моделей, чтобы можно было выбрать в Spinner
        loadModelsIntoSpinner();

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptGenerate();
            }
        });
    }

    private void loadModelsIntoSpinner() {
        progressBar.setVisibility(View.VISIBLE);
        NetworkClient.getInstance(this).doGet("models", new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(GenerateActivity.this, "Ошибка при загрузке моделей", Toast.LENGTH_SHORT).show();
                });
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                String body = response.body() != null ? response.body().string() : "";
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(GenerateActivity.this, "Ошибка сервера: " + response.code(), Toast.LENGTH_SHORT).show();
                    });
                    return;
                }
                try {
                    JSONObject root = new JSONObject(body);
                    JSONObject modelsObj = root.getJSONObject("models");
                    ArrayList<String> temp = new ArrayList<>();
                    // Пробежимся по ключам categories → затем по их массивам
                    JSONArray categories = modelsObj.names();
                    for (int i = 0; i < categories.length(); i++) {
                        String cat = categories.getString(i);
                        JSONArray arr = modelsObj.getJSONArray(cat);
                        for (int j = 0; j < arr.length(); j++) {
                            temp.add(arr.getString(j));
                        }
                    }
                    runOnUiThread(() -> {
                        allModels.clear();
                        allModels.addAll(temp);
                        modelAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    });
                } catch (JSONException ex) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(GenerateActivity.this, "JSON ошибка", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void attemptGenerate() {
        String prompt = etPrompt.getText().toString().trim();
        String widthStr = etWidth.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();
        String stepsStr = etSteps.getText().toString().trim();
        String cfgStr = etCfg.getText().toString().trim();
        String seedStr = etSeed.getText().toString().trim();
        String model = (String) spinnerModel.getSelectedItem();
        String sampling = (String) spinnerSample.getSelectedItem();

        if (TextUtils.isEmpty(prompt) || TextUtils.isEmpty(widthStr) || TextUtils.isEmpty(heightStr)
                || TextUtils.isEmpty(stepsStr) || TextUtils.isEmpty(cfgStr) || TextUtils.isEmpty(seedStr)
                || model == null) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        int width = Integer.parseInt(widthStr);
        int height = Integer.parseInt(heightStr);
        int steps = Integer.parseInt(stepsStr);
        double cfg = Double.parseDouble(cfgStr);
        int seed = Integer.parseInt(seedStr);

        JSONObject json = new JSONObject();
        try {
            json.put("model", "placeholder");
            json.put("prompt", prompt);
            json.put("width", width);
            json.put("height", height);
            json.put("steps", steps);
            json.put("cfg_scale", cfg);
            json.put("sampling_method", sampling);
            json.put("seed", seed);
        } catch (JSONException ex) {
            Toast.makeText(this, "Ошибка формирования JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        NetworkClient.getInstance(this)
                .doPostJson("generate/placeholder", json.toString(), new Callback() {
                    @Override public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(GenerateActivity.this, "Ошибка сети: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override public void onResponse(Call call, Response response) throws IOException {
                        String body = response.body() != null ? response.body().string() : "";
                        if (!response.isSuccessful()) {
                            runOnUiThread(() -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(GenerateActivity.this, "Ошибка сервера: " + response.code(), Toast.LENGTH_SHORT).show();
                            });
                            return;
                        }
                        try {
                            JSONObject root = new JSONObject(body);
                            boolean success = root.optBoolean("success", false);
                            if (!success) {
                                runOnUiThread(() -> {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(GenerateActivity.this, "Сервер вернул success=false", Toast.LENGTH_SHORT).show();
                                });
                                return;
                            }
                            // Вытаскиваем image_url и параметры
                            String imageUrl = root.optString("image_url", "");
                            JSONObject params = root.optJSONObject("parameters");
                            String modelName = root.optString("model", "");
                            String promptResp = root.optString("prompt", "");
                            runOnUiThread(() -> {
                                progressBar.setVisibility(View.GONE);
                                // Переходит на ResultActivity
                                Intent intent = new Intent(GenerateActivity.this, ResultActivity.class);
                                intent.putExtra("image_url", imageUrl);
                                intent.putExtra("model", modelName);
                                intent.putExtra("prompt", promptResp);
                                if (params != null) {
                                    intent.putExtra("width", params.optInt("width", 0));
                                    intent.putExtra("height", params.optInt("height", 0));
                                    intent.putExtra("steps", params.optInt("steps", 0));
                                    intent.putExtra("cfg_scale", params.optDouble("cfg_scale", 0));
                                    intent.putExtra("seed", params.optInt("seed", 0));
                                    intent.putExtra("sampling_method", params.optString("sampling_method", ""));
                                }
                                // Сохраняем URL картинки в SharedPreferences для галереи
                                GalleryStorage.addImageUrl(GenerateActivity.this, imageUrl);
                                startActivity(intent);
                            });
                        } catch (JSONException ex) {
                            runOnUiThread(() -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(GenerateActivity.this, "Ошибка разбора JSON", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                });
    }
}
