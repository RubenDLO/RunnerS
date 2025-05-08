package com.example.runners.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.runners.R;
import com.example.runners.Race;
import com.example.runners.RaceAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvNoRaces;
    private List<Race> raceList;
    private RaceAdapter adapter;

    public HistoryFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerHistory);
        tvNoRaces = view.findViewById(R.id.tvNoRaces);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        raceList = new ArrayList<>();
        adapter = new RaceAdapter(raceList);
        recyclerView.setAdapter(adapter);

        cargarHistorial();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarHistorial(); // Refresca cada vez que se vuelve a mostrar
    }

    private void cargarHistorial() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            tvNoRaces.setText("Debes iniciar sesión.");
            tvNoRaces.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("races")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    raceList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Race race = doc.toObject(Race.class);
                        raceList.add(race);
                    }
                    adapter.notifyDataSetChanged();

                    if (raceList.isEmpty()) {
                        tvNoRaces.setText("Aún no has registrado ninguna carrera 🏃‍♂️");
                        tvNoRaces.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvNoRaces.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    tvNoRaces.setText("Error al cargar carreras: " + e.getMessage());
                    tvNoRaces.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                });
    }
}
