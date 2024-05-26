package com.drpleaserespect.nyaamii.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drpleaserespect.nyaamii.R;

public class ProfileActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPref = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        sharedPref = getSharedPreferences(getString(R.string.ProfileState), MODE_PRIVATE);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        SetData(sharedPref);

        // For back button use finish()
        LinearLayout back = findViewById(R.id.CartButtonLinearLayout);
        back.setOnClickListener(v -> finish());

        Button DebugButton = findViewById(R.id.DebugButton);
        DebugButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, DebuggingActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        SetData(sharedPreferences);
    }

    private void SetData(SharedPreferences sharedPreferences) {
        TextView NameView = findViewById(R.id.ProfileNameText);
        TextView EmailView = findViewById(R.id.ProfileEmailText);

        String User = sharedPreferences.getString("User", "Null");
        String Email = sharedPreferences.getString("Email", "Null");

        // Set the user's data
        NameView.setText(User);
        EmailView.setText(Email);
    }
}