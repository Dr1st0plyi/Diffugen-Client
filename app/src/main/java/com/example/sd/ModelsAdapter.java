package com.example.sd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

// Адаптер для списка моделей
public class ModelsAdapter extends RecyclerView.Adapter<ModelsAdapter.ViewHolder> {
    private final ArrayList<ModelItem> items;

    public ModelsAdapter(ArrayList<ModelItem> items) {
        this.items = items;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvSubModels, tvDefaultParams;
        ViewHolder(View root) {
            super(root);
            tvCategory = root.findViewById(R.id.tvCategory);
            tvSubModels = root.findViewById(R.id.tvSubModels);
            tvDefaultParams = root.findViewById(R.id.tvDefaultParams);
        }
    }

    @NonNull
    @Override
    public ModelsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_model, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ModelsAdapter.ViewHolder holder, int position) {
        ModelItem mi = items.get(position);
        holder.tvCategory.setText(mi.category);

        // Составляем строку “Подмодели: flux-schnell, flux-dev, ...”
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mi.models.size(); i++) {
            sb.append(mi.models.get(i));
            if (i < mi.models.size() - 1) sb.append(", ");
        }
        holder.tvSubModels.setText("Подмодели: " + sb.toString());

        // Параметры по умолчанию. Из defaultParams вытягиваем нужные поля.
        // Например, defaultParams.getJSONObject("steps").getInt(<имя_модели>)
        StringBuilder dpSb = new StringBuilder();
        try {
            JSONObject stepsObj = mi.defaultParams.getJSONObject("steps");
            JSONObject cfgObj = mi.defaultParams.getJSONObject("cfg_scale");
            JSONObject sampObj = mi.defaultParams.getJSONObject("sampling_method");

            for (String modelName : mi.models) {
                dpSb.append(modelName).append(" → ");
                int st = stepsObj.optInt(modelName, -1);
                double cfg = cfgObj.optDouble(modelName, -1);
                String sm = sampObj.optString(modelName, "-");
                dpSb.append("steps=").append(st)
                        .append(", cfg_scale=").append(cfg)
                        .append(", sampling_method=").append(sm)
                        .append("\n");
            }
        } catch (JSONException e) {
            dpSb.append("Ошибка разбора default_params");
        }
        holder.tvDefaultParams.setText(dpSb.toString().trim());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
