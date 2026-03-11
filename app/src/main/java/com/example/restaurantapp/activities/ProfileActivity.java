package com.example.restaurantapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.restaurantapp.R;
import com.example.restaurantapp.database.DatabaseHelper;
import com.example.restaurantapp.models.User;
import com.example.restaurantapp.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

/**
 * ProfileActivity - Allows users to view and update their profile information.
 * Also provides a logout button.
 */
public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText editName, editEmail, editPhone, editAddress;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.profile));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        editAddress = findViewById(R.id.editAddress);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Load user data
        currentUser = dbHelper.getUserById(sessionManager.getUserId());
        if (currentUser != null) {
            editName.setText(currentUser.getName());
            editEmail.setText(currentUser.getEmail());
            editPhone.setText(currentUser.getPhone());
            editAddress.setText(currentUser.getAddress());
        }

        btnSave.setOnClickListener(v -> saveProfile());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void saveProfile() {
        String name = editName.getText() != null ? editName.getText().toString().trim() : "";
        String email = editEmail.getText() != null ? editEmail.getText().toString().trim() : "";
        String phone = editPhone.getText() != null ? editPhone.getText().toString().trim() : "";
        String address = editAddress.getText() != null ? editAddress.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name)) {
            editName.setError(getString(R.string.error_name_required));
            editName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError(getString(R.string.error_email_invalid));
            editEmail.requestFocus();
            return;
        }

        currentUser.setName(name);
        currentUser.setEmail(email);
        currentUser.setPhone(phone);
        currentUser.setAddress(address);

        int rows = dbHelper.updateUser(currentUser);
        if (rows > 0) {
            sessionManager.saveSession(currentUser.getId(), name, email);
            Toast.makeText(this, getString(R.string.profile_updated), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.profile_update_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        sessionManager.clearSession();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
