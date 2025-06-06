package com.example.sd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private final ArrayList<String> urls;

    public GalleryAdapter(ArrayList<String> urls) {
        this.urls = urls;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        ViewHolder(View v) {
            super(v);
            ivImage = v.findViewById(R.id.ivGalleryItem);
        }
    }

    @NonNull
    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery_image, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryAdapter.ViewHolder holder, int position) {
        String url = urls.get(position);
        Picasso.get()
                .load(url)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .fit().centerCrop()
                .into(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }
}
