package com.example.sd;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {
    private RecyclerView rvGallery;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        rvGallery = findViewById(R.id.rvGallery);
        tvEmpty = findViewById(R.id.tvGalleryEmpty);

        ArrayList<String> urls = GalleryStorage.getImageUrls(this);

        if (urls.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvGallery.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvGallery.setVisibility(View.VISIBLE);
            rvGallery.setLayoutManager(new GridLayoutManager(this, 2));
            GalleryAdapter adapter = new GalleryAdapter(urls);
            rvGallery.setAdapter(adapter);
        }
    }
}
