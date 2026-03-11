package com.example.restaurantapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantapp.R;
import com.example.restaurantapp.database.DatabaseHelper;
import com.example.restaurantapp.models.User;
import com.example.restaurantapp.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

/**
 * LoginActivity - Handles user authentication via email and password.
 * Validates input and stores the session on successful login.
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editEmail, editPassword;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // If already logged in, go to main
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> attemptLogin());

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void attemptLogin() {
        String email = editEmail.getText() != null ? editEmail.getText().toString().trim() : "";
        String password = editPassword.getText() != null ? editPassword.getText().toString().trim() : "";

        // Validate inputs
        if (TextUtils.isEmpty(email)) {
            editEmail.setError(getString(R.string.error_email_required));
            editEmail.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError(getString(R.string.error_email_invalid));
            editEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editPassword.setError(getString(R.string.error_password_required));
            editPassword.requestFocus();
            return;
        }

        // Authenticate user
        User user = dbHelper.loginUser(email, password);
        if (user != null) {
            sessionManager.saveSession(user.getId(), user.getName(), user.getEmail());
            Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, getString(R.string.error_invalid_credentials), Toast.LENGTH_LONG).show();
        }
    }
}
