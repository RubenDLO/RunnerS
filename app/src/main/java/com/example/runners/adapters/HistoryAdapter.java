package com.example.runners.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.runners.R;
import com.example.runners.Race;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private final List<Race> raceList;

    public HistoryAdapter(List<Race> raceList) {
        this.raceList = raceList;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_race, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        Race race = raceList.get(position);
        holder.tvDate.setText("Fecha: " + race.getDate());
        holder.tvDistance.setText("Distancia: " + String.format("%.2f km", race.getDistance()));
        holder.tvTime.setText("Tiempo: " + race.getFormattedTime());
        holder.tvSpeed.setText("Velocidad media: " + String.format("%.2f km/h", race.getAverageSpeed()));
    }

    @Override
    public int getItemCount() {
        return raceList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvDistance, tvTime, tvSpeed;

        HistoryViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvTime = itemView.findViewById(R.id.tvTimer);
            tvSpeed = itemView.findViewById(R.id.tvSpeed);
        }
    }
}
