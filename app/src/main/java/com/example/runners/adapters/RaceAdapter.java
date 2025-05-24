package com.example.runners.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.runners.R;
import com.example.runners.Race;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class RaceAdapter extends RecyclerView.Adapter<RaceAdapter.RaceViewHolder> {

    private final List<Race> raceList;

    public RaceAdapter(List<Race> raceList) {
        this.raceList = raceList;
    }

    @NonNull
    @Override
    public RaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_race, parent, false);
        return new RaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RaceViewHolder holder, int position) {
        Race race = raceList.get(position);
        Context context = holder.itemView.getContext();

        holder.tvRaceDate.setText("Fecha: " + race.getDate());
        holder.tvRaceDistance.setText(String.format("Distancia: %.2f km", race.getDistance()));
        holder.tvRaceStartTime.setText("Inicio: " + race.getStartTimeFormatted());
        holder.tvRaceDuration.setText("Duración: " + race.getFormattedTime());
        holder.tvRaceCalories.setText(String.format("Calorías: %.0f kcal", race.getCaloriesBurned()));
        holder.tvRaceTemp.setText(String.format("Temperatura: %.1f ºC", race.getTemperature()));

        double distance = race.getDistance();
        long elapsedTime = race.getElapsedTime();
        if (distance > 0 && elapsedTime > 0) {
            double totalMinutes = elapsedTime / 1000.0 / 60.0;
            double pace = totalMinutes / distance;
            int minutes = (int) pace;
            int seconds = (int) ((pace - minutes) * 60);
            String formattedPace = String.format("Ritmo medio: %d:%02d min/km", minutes, seconds);
            holder.tvRaceSpeed.setText(formattedPace);
        } else {
            holder.tvRaceSpeed.setText("Ritmo medio: -");
        }

        String encoded = race.getMapSnapshotBase64();
        if (encoded != null && !encoded.isEmpty()) {
            byte[] decoded = Base64.decode(encoded, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
            holder.ivMap.setImageBitmap(bitmap);
        }

        // 🟠 Confirmación antes de eliminar
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Estás seguro de borrar este registro?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        FirebaseFirestore.getInstance()
                                .collection("races")
                                .whereEqualTo("date", race.getDate())
                                .get()
                                .addOnSuccessListener(query -> {
                                    for (QueryDocumentSnapshot doc : query) {
                                        doc.getReference().delete();
                                    }
                                    raceList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, raceList.size());
                                    Toast.makeText(context, "Carrera eliminada", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                );
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        holder.itemView.setOnClickListener(v -> {
            if (holder.expandableLayout.getVisibility() == View.GONE) {
                holder.expandableLayout.setVisibility(View.VISIBLE);
            } else {
                holder.expandableLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return raceList.size();
    }

    public static class RaceViewHolder extends RecyclerView.ViewHolder {
        TextView tvRaceDate, tvRaceDistance, tvRaceStartTime, tvRaceDuration, tvRaceSpeed, tvRaceCalories, tvRaceTemp;
        ImageView ivMap;
        ImageButton btnDelete;
        LinearLayout expandableLayout;

        public RaceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRaceDate = itemView.findViewById(R.id.tvRaceDate);
            tvRaceDistance = itemView.findViewById(R.id.tvRaceDistance);
            tvRaceStartTime = itemView.findViewById(R.id.tvRaceStartTime);
            tvRaceDuration = itemView.findViewById(R.id.tvRaceDuration);
            tvRaceSpeed = itemView.findViewById(R.id.tvRaceSpeed);
            tvRaceCalories = itemView.findViewById(R.id.tvRaceCalories);
            tvRaceTemp = itemView.findViewById(R.id.tvRaceTemp);
            ivMap = itemView.findViewById(R.id.ivMap);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
        }
    }
}
