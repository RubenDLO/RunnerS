package com.example.runners;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etBirthDate, etEmailRegister, etUsernameRegister, etPasswordRegister, etConfirmPassword;
    private Spinner spinnerSex, spinnerLevel;
    private Button btnCreateAccount;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.etFullName);
        etBirthDate = findViewById(R.id.etBirthDate);
        etEmailRegister = findViewById(R.id.etEmailRegister);
        etUsernameRegister = findViewById(R.id.etUsernameRegister);
        etPasswordRegister = findViewById(R.id.etPasswordRegister);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        spinnerSex = findViewById(R.id.spinnerSex);
        spinnerLevel = findViewById(R.id.spinnerNivel);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ArrayAdapter<String> sexAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                Arrays.asList("Selecciona sexo", "Hombre", "Mujer", "Otro")
        );
        sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSex.setAdapter(sexAdapter);

        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                Arrays.asList("Selecciona nivel", "Amateur", "Intermedio", "Avanzado")
        );
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLevel.setAdapter(levelAdapter);

        btnCreateAccount.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String birthDate = etBirthDate.getText().toString().trim();
            String email = etEmailRegister.getText().toString().trim();
            String username = etUsernameRegister.getText().toString().trim();
            String password = etPasswordRegister.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();
            String sex = spinnerSex.getSelectedItem().toString();
            String level = spinnerLevel.getSelectedItem().toString();

            if (fullName.isEmpty() || birthDate.isEmpty() || email.isEmpty() || username.isEmpty()
                    || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Introduce un correo válido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            if (sex.equals("Selecciona sexo") || level.equals("Selecciona nivel")) {
                Toast.makeText(this, "Selecciona tu sexo y nivel", Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("users")
                    .whereEqualTo("username", username)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                            Toast.makeText(this, "Ese nombre de usuario ya existe", Toast.LENGTH_SHORT).show();
                        } else {
                            auth.createUserWithEmailAndPassword(email, password)
                                    .addOnSuccessListener(authResult -> {
                                        FirebaseUser user = authResult.getUser();
                                        if (user == null) {
                                            Toast.makeText(this, "Error al crear cuenta", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        Map<String, Object> userMap = new HashMap<>();
                                        userMap.put("fullName", fullName);
                                        userMap.put("birthDate", birthDate);
                                        userMap.put("email", email);
                                        userMap.put("username", username);
                                        userMap.put("sex", sex);
                                        userMap.put("level", level);

                                        db.collection("users")
                                                .document(user.getUid())
                                                .set(userMap)
                                                .addOnSuccessListener(unused -> {
                                                    Log.d("RegistroDebug", "Usuario guardado correctamente");

                                                    if (!isFinishing()) {
                                                        Toast.makeText(this, "Cuenta creada correctamente. Inicia sesión para continuar.", Toast.LENGTH_LONG).show();
                                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("RegistroDebug", "Error guardando usuario: " + e.getMessage());
                                                    Toast.makeText(this, "Error al guardar usuario: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        if (e instanceof FirebaseAuthUserCollisionException) {
                                            Toast.makeText(this, "Ese correo ya está registrado", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(this, "Error al registrar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    });
        });
    }
}
