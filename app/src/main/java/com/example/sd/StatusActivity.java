package com.example.sd;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import java.io.IOException;

public class StatusActivity extends AppCompatActivity {
    private Button btnHealth, btnSystem, btnGenerate, btnModels, btnGallery;
    private TextView tvStatusResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        btnHealth = findViewById(R.id.btnHealth);
        btnSystem = findViewById(R.id.btnSystem);
        btnGenerate = findViewById(R.id.btnGoGenerate);
        btnModels = findViewById(R.id.btnGoModels);
        btnGallery = findViewById(R.id.btnGoGallery);
        tvStatusResult = findViewById(R.id.tvStatusResult);

        btnHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvStatusResult.setText("Запрос к /health...");
                NetworkClient.getInstance(StatusActivity.this)
                        .doGet("health", new Callback() {
                            @Override public void onFailure(Call call, IOException e) {
                                runOnUiThread(() ->
                                        tvStatusResult.setText("Ошибка: " + e.getMessage()));
                            }
                            @Override public void onResponse(Call call, Response response) throws IOException {
                                String body = response.body() != null ? response.body().string() : "Нет ответа";
                                runOnUiThread(() -> tvStatusResult.setText("Health: " + body));
                            }
                        });
            }
        });

        btnSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvStatusResult.setText("Запрос к /system...");
                NetworkClient.getInstance(StatusActivity.this)
                        .doGet("system", new Callback() {
                            @Override public void onFailure(Call call, IOException e) {
                                runOnUiThread(() ->
                                        tvStatusResult.setText("Ошибка: " + e.getMessage()));
                            }
                            @Override public void onResponse(Call call, Response response) throws IOException {
                                String body = response.body() != null ? response.body().string() : "Нет ответа";
                                runOnUiThread(() -> tvStatusResult.setText("System: " + body));
                            }
                        });
            }
        });

        btnGenerate.setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, GenerateActivity.class));
        });
        btnModels.setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, ModelsActivity.class));
        });
        btnGallery.setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, GalleryActivity.class));
        });
    }
}
