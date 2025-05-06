package com.example.runners;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class RaceAdapter extends RecyclerView.Adapter<RaceAdapter.RaceViewHolder> {

    private List<Race> raceList;

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

        holder.tvDate.setText("Fecha: " + race.getDate());
        holder.tvDistance.setText(String.format("Distancia: %.2f km", race.getDistance()));
        holder.tvDuration.setText("Tiempo: " + race.getFormattedTime());
        holder.tvSpeed.setText(String.format("Velocidad: %.2f km/h", race.getAverageSpeed()));

        // Decodificar imagen base64 y mostrar miniatura
        String encoded = race.getMapSnapshotBase64();
        if (encoded != null && !encoded.isEmpty()) {
            byte[] decoded = Base64.decode(encoded, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
            holder.ivMap.setImageBitmap(bitmap);
        }

        // Eliminar carrera de Firebase y de la lista
        holder.btnDelete.setOnClickListener(v -> {
            FirebaseFirestore.getInstance()
                    .collection("races")
                    .whereEqualTo("date", race.getDate()) // criterio simple
                    .get()
                    .addOnSuccessListener(query -> {
                        for (QueryDocumentSnapshot doc : query) {
                            doc.getReference().delete();
                        }
                        raceList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, raceList.size());
                    });
        });
    }

    @Override
    public int getItemCount() {
        return raceList.size();
    }

    public static class RaceViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvDistance, tvDuration, tvSpeed;
        ImageView ivMap;
        ImageButton btnDelete;

        public RaceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvSpeed = itemView.findViewById(R.id.tvSpeed);
            ivMap = itemView.findViewById(R.id.ivMap);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
