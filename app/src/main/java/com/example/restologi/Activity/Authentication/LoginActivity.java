package com.example.restologi.Activity.Authentication;

import static android.content.ContentValues.TAG;

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

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private EditText et_Email, et_Password;
    private Button btn_login;
    private ImageView btn_login_google;
    private TextView tv_register;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_Email = findViewById(R.id.email_input_login);
        et_Password = findViewById(R.id.password_input_login);
        btn_login = findViewById(R.id.BLogin);
        btn_login_google = findViewById(R.id.btn_login_google);
        tv_register = findViewById(R.id.TVRegister);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        btn_login.setOnClickListener(v -> {
            loginUser();
        });

        btn_login_google.setOnClickListener(v -> {
            signInWithGoogle();
        });

        tv_register.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("268437593039-0b67f26hafjpk7hq4r7c3v4277mabjuo.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void loginUser() {
        String email = et_Email.getText().toString();
        String password = et_Password.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                    if (isNewUser) {
                        Toast.makeText(LoginActivity.this, "Email is not registered. Please sign up first.", Toast.LENGTH_SHORT).show();
                    } else {
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            if (user != null && user.isEmailVerified()) {
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else if (user != null && !user.isEmailVerified()) {
                                                Toast.makeText(LoginActivity.this, "Please verify your email address.", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
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
                Log.d(TAG, "signInWithCredential:success");
                FirebaseUser user = mAuth.getCurrentUser();
                checkIfUserExistsInDatabase(user);
            } else {
                Log.w(TAG, "signInWithCredential:failure", task.getException());
                Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        });
    }

    private void checkIfUserExistsInDatabase(FirebaseUser user) {
        DatabaseReference userRef = mDatabase.child(user.getUid());
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    updateUI(user);
                } else {
                    // User does not exist, create a new record in the database
                    createUserInDatabase(user);
                }
            } else {
                Log.w(TAG, "User existence check failed.", task.getException());
                Toast.makeText(LoginActivity.this, "Failed to check user existence.", Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        });
    }

    private void createUserInDatabase(FirebaseUser user) {
        User newUser = new User(user.getUid(), user.getDisplayName(), user.getEmail());
        mDatabase.child(user.getUid()).setValue(newUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updateUI(user);
            } else {
                Log.w(TAG, "Failed to create user in database.", task.getException());
                Toast.makeText(LoginActivity.this, "Failed to create user in database.", Toast.LENGTH_SHORT).show();
                updateUI(null);
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
                Log.w(TAG, "Google sign in failed", e);
                updateUI(null);
            }
        }
    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("NAMA_USER", user.getDisplayName());
            startActivity(intent);
        } else {
            Toast.makeText(LoginActivity.this, "Log In First", Toast.LENGTH_SHORT).show();
        }
    }
}