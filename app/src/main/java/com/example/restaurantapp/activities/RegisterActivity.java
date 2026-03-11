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
import com.google.android.material.textfield.TextInputEditText;

/**
 * RegisterActivity - Allows new users to create an account.
 * Validates all fields before inserting into the database.
 */
public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText editName, editEmail, editPhone, editPassword, editConfirmPassword;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        editPassword = findViewById(R.id.editPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(v -> attemptRegister());
        tvLogin.setOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        String name = editName.getText() != null ? editName.getText().toString().trim() : "";
        String email = editEmail.getText() != null ? editEmail.getText().toString().trim() : "";
        String phone = editPhone.getText() != null ? editPhone.getText().toString().trim() : "";
        String password = editPassword.getText() != null ? editPassword.getText().toString().trim() : "";
        String confirmPassword = editConfirmPassword.getText() != null ? editConfirmPassword.getText().toString().trim() : "";

        // Validate name
        if (TextUtils.isEmpty(name)) {
            editName.setError(getString(R.string.error_name_required));
            editName.requestFocus();
            return;
        }
        // Validate email
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
        // Validate phone
        if (TextUtils.isEmpty(phone)) {
            editPhone.setError(getString(R.string.error_phone_required));
            editPhone.requestFocus();
            return;
        }
        // Validate password
        if (TextUtils.isEmpty(password)) {
            editPassword.setError(getString(R.string.error_password_required));
            editPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editPassword.setError(getString(R.string.error_password_short));
            editPassword.requestFocus();
            return;
        }
        // Validate confirm password
        if (!password.equals(confirmPassword)) {
            editConfirmPassword.setError(getString(R.string.error_password_mismatch));
            editConfirmPassword.requestFocus();
            return;
        }

        // Check if email already exists
        if (dbHelper.isEmailExists(email)) {
            editEmail.setError(getString(R.string.error_email_exists));
            editEmail.requestFocus();
            return;
        }

        // Register user
        User user = new User(name, email, phone, password);
        long result = dbHelper.registerUser(user);
        if (result != -1) {
            Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, getString(R.string.register_failed), Toast.LENGTH_LONG).show();
        }
    }
}
