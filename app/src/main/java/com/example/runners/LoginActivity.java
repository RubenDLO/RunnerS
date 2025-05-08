package com.example.runners;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private android.widget.Button btnLogin;
    private SignInButton btnGoogle;
    private TextView btnRegister, btnForgotPassword, tvVersion;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, MainNavigationActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        // Fondo aleatorio
        int[] backgrounds = {
                R.drawable.pic_login1, R.drawable.pic_login2, R.drawable.pic_login3,
                R.drawable.pic_login4, R.drawable.pic_login5, R.drawable.pic_login6,
                R.drawable.pic_login8
        };
        int randomIndex = new Random().nextInt(backgrounds.length);
        Drawable randomBg = ContextCompat.getDrawable(this, backgrounds[randomIndex]);
        ImageView background = findViewById(R.id.imgBackground);
        background.setImageDrawable(randomBg);

        // Referencias UI
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnRegister = findViewById(R.id.btnRegister);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        tvVersion = findViewById(R.id.tvVersion);
        tvVersion.setText("Versión 1.0.0");

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Login con usuario o email
        btnLogin.setOnClickListener(v -> {
            String input = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (input.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Introduce usuario y contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            if (input.contains("@")) {
                loginWithEmail(input, password);
            } else {
                db.collection("users")
                        .whereEqualTo("username", input)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (queryDocumentSnapshots.isEmpty()) {
                                Toast.makeText(this, "Nombre de usuario no encontrado", Toast.LENGTH_SHORT).show();
                            } else {
                                String email = queryDocumentSnapshots.getDocuments().get(0).getString("email");
                                loginWithEmail(email, password);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error al buscar usuario: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }
        });

        // Registro
        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Recuperar contraseña
        btnForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        // Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogle.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    private void loginWithEmail(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Bienvenido, " + email, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainNavigationActivity.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Error en login con Google: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Sesión iniciada con Google", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainNavigationActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Fallo en autenticación con Google", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
