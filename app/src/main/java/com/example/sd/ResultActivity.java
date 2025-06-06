package com.example.sd;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

public class ResultActivity extends AppCompatActivity {
    private ImageView ivResult;
    private TextView tvDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ivResult = findViewById(R.id.ivResult);
        tvDetails = findViewById(R.id.tvResultDetails);

        // Получаем данные из intent
        String imageUrl = getIntent().getStringExtra("image_url");
        String model = getIntent().getStringExtra("model");
        String prompt = getIntent().getStringExtra("prompt");
        int width = getIntent().getIntExtra("width", 0);
        int height = getIntent().getIntExtra("height", 0);
        int steps = getIntent().getIntExtra("steps", 0);
        double cfg = getIntent().getDoubleExtra("cfg_scale", 0);
        int seed = getIntent().getIntExtra("seed", 0);
        String sampling = getIntent().getStringExtra("sampling_method");

        // Загрузим картинку через Picasso
        Picasso.get()
                .load(imageUrl)
                .fit().centerInside()
                .placeholder(R.drawable.ic_placeholder) // опционально
                .error(R.drawable.ic_error)
                .into(ivResult);

        // Сформируем строку с параметрами
        StringBuilder sb = new StringBuilder();
        sb.append("Model: ").append(model).append("\n");
        sb.append("Prompt: ").append(prompt).append("\n");
        sb.append("Resolution: ").append(width).append("×").append(height).append("\n");
        sb.append("Steps: ").append(steps).append("\n");
        sb.append("CFG Scale: ").append(cfg).append("\n");
        sb.append("Sampling: ").append(sampling).append("\n");
        sb.append("Seed: ").append(seed == -1 ? "random" : seed).append("\n");
        sb.append("Image URL:\n").append(imageUrl);

        tvDetails.setText(sb.toString());
    }
}
