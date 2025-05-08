package com.example.runners.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.runners.LoginActivity;
import com.example.runners.R;
import com.example.runners.TrainingPlanActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private TextView tvEmail, tvUsername, tvName, tvBirthdate;
    private EditText etWeight;
    private Spinner spinnerLevel;
    private Button btnSave;
    private ImageView imgProfilePicture, bgProfile;

    private FirebaseFirestore db;
    private FirebaseUser user;
    private String userId;

    private Uri selectedImageUri;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    private final Handler imageHandler = new Handler();
    private final int[] backgroundImages = {
            R.drawable.pic_login7, R.drawable.pic_login9, R.drawable.pic_login10,
            R.drawable.pic_login11, R.drawable.pic_login12, R.drawable.pic_login13, R.drawable.pic_login14
    };
    private int currentImageIndex = 0;
    private Runnable imageSliderRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        bgProfile = view.findViewById(R.id.bgProfile);
        bgProfile.setImageResource(backgroundImages[0]);

        Button btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        Button btnTrainingPlan = view.findViewById(R.id.btnTrainingPlan);
        btnTrainingPlan.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), TrainingPlanActivity.class);
            intent.putExtra("runnerLevel", spinnerLevel.getSelectedItem().toString());
            startActivity(intent);
        });

        tvEmail = view.findViewById(R.id.tvEmail);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvName = view.findViewById(R.id.tvName);
        tvBirthdate = view.findViewById(R.id.tvBirthdate);
        etWeight = view.findViewById(R.id.etWeight);
        spinnerLevel = view.findViewById(R.id.spinnerNivel);
        btnSave = view.findViewById(R.id.btnSaveProfile);
        imgProfilePicture = view.findViewById(R.id.imgProfilePicture);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        userId = user.getUid();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.runner_levels, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLevel.setAdapter(adapter);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        uploadProfileImage(uri);
                    }
                });

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        imagePickerLauncher.launch("image/*");
                    } else {
                        if (isAdded()) {
                            Toast.makeText(getContext(), "Permiso denegado para acceder a la galería", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        imgProfilePicture.setOnClickListener(v -> {
            String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                    Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;

            if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
                imagePickerLauncher.launch("image/*");
            } else if (shouldShowRequestPermissionRationale(permission)) {
                permissionLauncher.launch(permission);
            } else {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Permiso necesario")
                        .setMessage("Para seleccionar tu foto de perfil, activa el permiso de galería en los ajustes de la app.")
                        .setPositiveButton("Ir a ajustes", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + requireContext().getPackageName()));
                            startActivity(intent);
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });

        btnSave.setOnClickListener(v -> saveProfileData());
        loadProfileData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        imageSliderRunnable = new Runnable() {
            @Override
            public void run() {
                currentImageIndex = (currentImageIndex + 1) % backgroundImages.length;

                AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
                fadeOut.setDuration(500);
                AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                fadeIn.setDuration(500);

                bgProfile.startAnimation(fadeOut);
                bgProfile.setImageResource(backgroundImages[currentImageIndex]);
                bgProfile.startAnimation(fadeIn);

                imageHandler.postDelayed(this, 10000);
            }
        };

        imageHandler.postDelayed(imageSliderRunnable, 10000);
    }

    @Override
    public void onPause() {
        super.onPause();
        imageHandler.removeCallbacks(imageSliderRunnable);
    }

    private void loadProfileData() {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!isAdded() || getContext() == null) return;

                    if (snapshot.exists()) {
                        tvUsername.setText("Nombre de usuario: " + safeText(snapshot.getString("username")));
                        tvEmail.setText("Correo: " + safeText(snapshot.getString("email")));
                        tvName.setText("Nombre: " + safeText(snapshot.getString("fullName")));
                        tvBirthdate.setText("Fecha de nacimiento: " + safeText(snapshot.getString("birthDate")));

                        String peso = snapshot.getString("weight");
                        etWeight.setText(peso != null ? peso : "");

                        String level = snapshot.getString("level");
                        if (level != null && isAdded()) {
                            String[] niveles = getResources().getStringArray(R.array.runner_levels);
                            for (int i = 0; i < niveles.length; i++) {
                                if (niveles[i].equalsIgnoreCase(level)) {
                                    spinnerLevel.setSelection(i);
                                    break;
                                }
                            }
                        }

                        String imageUrl = snapshot.getString("profilePicture");
                        if (imageUrl != null && !imageUrl.isEmpty() && isAdded()) {
                            Glide.with(requireContext())
                                    .load(imageUrl)
                                    .circleCrop()
                                    .into(imgProfilePicture);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (!isAdded() || getContext() == null) return;
                    Toast.makeText(getContext(), "Error al cargar el perfil", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveProfileData() {
        String weight = etWeight.getText().toString().trim();
        String newLevel = spinnerLevel.getSelectedItem().toString();

        if (TextUtils.isEmpty(weight)) {
            if (isAdded()) {
                Toast.makeText(getContext(), "Por favor, introduce tu peso", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmar cambio de nivel")
                .setMessage("¿Seguro que quieres cambiar tu nivel de corredor? ¿Te ves preparado?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("weight", weight);
                    updates.put("level", newLevel);

                    db.collection("users").document(userId)
                            .update(updates)
                            .addOnSuccessListener(unused -> {
                                if (isAdded()) {
                                    Toast.makeText(getContext(), "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                if (isAdded()) {
                                    Toast.makeText(getContext(), "Error al guardar los cambios", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void uploadProfileImage(Uri uri) {
        FirebaseStorage.getInstance().getReference()
                .child("profile_pictures/" + userId + ".jpg")
                .putFile(uri)
                .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl()
                        .addOnSuccessListener(downloadUri -> {
                            db.collection("users").document(userId)
                                    .update("profilePicture", downloadUri.toString())
                                    .addOnSuccessListener(unused -> {
                                        if (isAdded()) {
                                            Glide.with(requireContext())
                                                    .load(downloadUri)
                                                    .circleCrop()
                                                    .into(imgProfilePicture);
                                            Toast.makeText(getContext(), "Imagen de perfil actualizada", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }))
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Error al subir imagen", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String safeText(String value) {
        return (value != null && !value.isEmpty()) ? value : "No indicado";
    }
}
