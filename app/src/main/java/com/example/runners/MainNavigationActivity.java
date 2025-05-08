package com.example.runners;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.runners.adapters.MainPagerAdapter;
import com.example.runners.databinding.ActivityMainNavigationBinding;


public class MainNavigationActivity extends AppCompatActivity {

    private ActivityMainNavigationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MainPagerAdapter adapter = new MainPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);

        // Desactivamos el swipe manual para controlar con el menú inferior
        binding.viewPager.setUserInputEnabled(false);

        binding.bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                binding.viewPager.setCurrentItem(0, true);
                return true;
            } else if (itemId == R.id.menu_profile) {
                binding.viewPager.setCurrentItem(1, true);
                return true;
            } else if (itemId == R.id.menu_history) {
                binding.viewPager.setCurrentItem(2, true);
                return true;
            } else if (itemId == R.id.menu_faq) {
                binding.viewPager.setCurrentItem(3, true);
                return true;
            }
            return false;
        });

        if (getIntent().getBooleanExtra("goToHistory", false)) {
            binding.viewPager.setCurrentItem(2, false); // índice del HistoryFragment
            binding.bottomNav.setSelectedItemId(R.id.menu_history); // seleccionamos el icono del historial
        }

    }
}
