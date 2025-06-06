package com.example.sd;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText etBaseUrl;
    private Button btnSaveConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etBaseUrl = findViewById(R.id.etBaseUrl);
        btnSaveConnect = findViewById(R.id.btnSaveConnect);

        // Если уже в SharedPreferences есть URL, подставляем его
        String saved = NetworkClient.getInstance(this).getBaseUrl();
        if (!TextUtils.isEmpty(saved)) {
            etBaseUrl.setText(saved);
        }

        btnSaveConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String entered = etBaseUrl.getText().toString().trim();
                if (TextUtils.isEmpty(entered)) {
                    Toast.makeText(MainActivity.this, "Введите базовый URL сервера", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Сохраняем
                NetworkClient.getInstance(MainActivity.this).setBaseUrl(entered);

                // Переходим на экран статуса (или любое меню)
                Intent intent = new Intent(MainActivity.this, StatusActivity.class);
                startActivity(intent);
            }
        });
    }
}
