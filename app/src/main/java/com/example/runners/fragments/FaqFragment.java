package com.example.runners.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.runners.R;
import com.example.runners.adapters.FaqAdapter;
import com.example.runners.models.FaqItem;

import java.util.ArrayList;
import java.util.List;

public class FaqFragment extends Fragment {

    private RecyclerView rvFaq;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faq, container, false);

        rvFaq = view.findViewById(R.id.rvFaq);
        rvFaq.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFaq.setAdapter(new FaqAdapter(getFaqList()));

        return view;
    }

    private List<FaqItem> getFaqList() {
        List<FaqItem> list = new ArrayList<>();

        // 🧩 Bloque: Funcionamiento de la app
        list.add(new FaqItem("1. FUNCIONAMIENTO DE LA APP", "", FaqItem.TYPE_HEADER));
        list.add(new FaqItem(getString(R.string.faq_q_start_race), getString(R.string.faq_a_start_race)));
        list.add(new FaqItem(getString(R.string.faq_q_pause_race), getString(R.string.faq_a_pause_race)));
        list.add(new FaqItem(getString(R.string.faq_q_stop_race), getString(R.string.faq_a_stop_race)));
        list.add(new FaqItem(getString(R.string.faq_q_view_history), getString(R.string.faq_a_view_history)));
        list.add(new FaqItem(getString(R.string.faq_q_edit_profile), getString(R.string.faq_a_edit_profile)));
        list.add(new FaqItem(getString(R.string.faq_q_change_name_dob), getString(R.string.faq_a_change_name_dob)));
        list.add(new FaqItem(getString(R.string.faq_q_temperature), getString(R.string.faq_a_temperature)));
        list.add(new FaqItem(getString(R.string.faq_q_login_method), getString(R.string.faq_a_login_method)));
        list.add(new FaqItem(getString(R.string.faq_q_offline), getString(R.string.faq_a_offline)));
        list.add(new FaqItem(getString(R.string.faq_q_support), getString(R.string.faq_a_support)));

        // 🏃‍♂️ Bloque: Antes de correr
        list.add(new FaqItem("2. ANTES DE CORRER", "", FaqItem.TYPE_HEADER));
        list.add(new FaqItem(getString(R.string.faq_q_warmup), getString(R.string.faq_a_warmup)));
        list.add(new FaqItem(getString(R.string.faq_q_food_before), getString(R.string.faq_a_food_before)));
        list.add(new FaqItem(getString(R.string.faq_q_hydration_before), getString(R.string.faq_a_hydration_before)));
        list.add(new FaqItem(getString(R.string.faq_q_route_check), getString(R.string.faq_a_route_check)));
        list.add(new FaqItem(getString(R.string.faq_q_clothing), getString(R.string.faq_a_clothing)));
        list.add(new FaqItem(getString(R.string.faq_q_hydration_during), getString(R.string.faq_a_hydration_during)));
        list.add(new FaqItem(getString(R.string.faq_q_food_during), getString(R.string.faq_a_food_during)));
        list.add(new FaqItem(getString(R.string.faq_q_pace), getString(R.string.faq_a_pace)));
        list.add(new FaqItem(getString(R.string.faq_q_pain), getString(R.string.faq_a_pain)));
        list.add(new FaqItem(getString(R.string.faq_q_mental_fatigue), getString(R.string.faq_a_mental_fatigue)));

        // 🧘‍♂️ Bloque: Después de correr
        list.add(new FaqItem("3. DESPUÉS DE CORRER", "", FaqItem.TYPE_HEADER));
        list.add(new FaqItem(getString(R.string.faq_q_stretch_post), getString(R.string.faq_a_stretch_post)));
        list.add(new FaqItem(getString(R.string.faq_q_food_after), getString(R.string.faq_a_food_after)));
        list.add(new FaqItem(getString(R.string.faq_q_hydration_after), getString(R.string.faq_a_hydration_after)));
        list.add(new FaqItem(getString(R.string.faq_q_cold_bath), getString(R.string.faq_a_cold_bath)));
        list.add(new FaqItem(getString(R.string.faq_q_rest_time), getString(R.string.faq_a_rest_time)));
        list.add(new FaqItem(getString(R.string.faq_q_fatigue_after_run), getString(R.string.faq_a_fatigue_after_run)));
        list.add(new FaqItem(getString(R.string.faq_q_best_post_workout_meal), getString(R.string.faq_a_best_post_workout_meal)));
        list.add(new FaqItem(getString(R.string.faq_q_post_run_massage), getString(R.string.faq_a_post_run_massage)));
        list.add(new FaqItem(getString(R.string.faq_q_when_train_again), getString(R.string.faq_a_when_train_again)));
        list.add(new FaqItem(getString(R.string.faq_q_muscle_soreness), getString(R.string.faq_a_muscle_soreness)));

        // 🛌 Bloque: Descanso activo
        list.add(new FaqItem("4. DESCANSO ACTIVO", "", FaqItem.TYPE_HEADER));
        list.add(new FaqItem(getString(R.string.faq_q_what_is_active_rest), getString(R.string.faq_a_what_is_active_rest)));
        list.add(new FaqItem(getString(R.string.faq_q_why_active_rest), getString(R.string.faq_a_why_active_rest)));
        list.add(new FaqItem(getString(R.string.faq_q_how_often_rest), getString(R.string.faq_a_how_often_rest)));
        list.add(new FaqItem(getString(R.string.faq_q_activities), getString(R.string.faq_a_activities)));
        list.add(new FaqItem(getString(R.string.faq_q_replace_training), getString(R.string.faq_a_replace_training)));
        list.add(new FaqItem(getString(R.string.faq_q_cross_training), getString(R.string.faq_a_cross_training)));
        list.add(new FaqItem(getString(R.string.faq_q_signs_rest_needed), getString(R.string.faq_a_signs_rest_needed)));
        list.add(new FaqItem(getString(R.string.faq_q_prevent_injury), getString(R.string.faq_a_prevent_injury)));
        list.add(new FaqItem(getString(R.string.faq_q_nutrition_rest_day), getString(R.string.faq_a_nutrition_rest_day)));
        list.add(new FaqItem(getString(R.string.faq_q_stretch_on_rest), getString(R.string.faq_a_stretch_on_rest)));

        return list;
    }
}
