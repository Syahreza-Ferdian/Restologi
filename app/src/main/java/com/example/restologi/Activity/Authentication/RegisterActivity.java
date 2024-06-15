package com.example.restologi.Activity.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.restologi.Activity.MainActivity;
import com.example.restologi.Domain.User;
import com.example.restologi.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9002;
    private EditText et_Email, et_Password, et_nama, et_phone;
    private Button btn_register;
    private ImageView btn_register_google;
    private TextView tv_login;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_Email = findViewById(R.id.email_register);
        et_Password = findViewById(R.id.password_register);
        et_nama = findViewById(R.id.fullname_input);
        et_phone = findViewById(R.id.phone_input);
        btn_register = findViewById(R.id.btn_register);
        btn_register_google = findViewById(R.id.btn_register_google);
        tv_login = findViewById(R.id.TVLogin);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        btn_register.setOnClickListener(v -> {
            registerUser();
        });

        btn_register_google.setOnClickListener(v -> {
            signInWithGoogle();
        });

        tv_login.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("268437593039-0b67f26hafjpk7hq4r7c3v4277mabjuo.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void registerUser() {
        String email = et_Email.getText().toString();
        String password = et_Password.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification();
                                saveUserToDatabase(user);
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signInWithGoogle() {
        mGoogleSignInClient.signOut();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d("R", "signInWithCredential:success");
                FirebaseUser user = mAuth.getCurrentUser();
                saveUserToDatabase(user);
            } else {
                Log.w("R", "signInWithCredential:failure", task.getException());
                Toast.makeText(RegisterActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserToDatabase(FirebaseUser user) {
        User newUser;
        if (et_phone.getText().toString().equals("")) {
            newUser = new User(user.getUid(), et_nama.getText().toString(), user.getEmail());
        } else {
            newUser = new User(user.getUid(), et_nama.getText().toString(), user.getEmail(), et_phone.getText().toString());
        }

        mDatabase.child(user.getUid()).setValue(newUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(RegisterActivity.this, "Registration successful. Please verify your email.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                Log.w("R", "Failed to save user in database.", task.getException());
                Toast.makeText(RegisterActivity.this, "Failed to save user in database.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("R", "Google sign in failed", e);
            }
        }
    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.putExtra("NAMA_USER", user.getDisplayName());
            startActivity(intent);
        }
    }
}
