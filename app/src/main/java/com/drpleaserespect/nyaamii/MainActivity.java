package com.drpleaserespect.nyaamii;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MemoryCacheSettings;
import com.google.firebase.firestore.PersistentCacheSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "MainActivity";
    private static final String TEST_USER = "DrPleaseRespect";


    private boolean Loaded = false;
    private static ListenerRegistration snapshot_listener = null;

    private SharedPreferences sharedPref = null;


    private void SetUserInfo(String ImageURL, String Username, String Email) {


        // Set the user's Image
        ImageView imageView = findViewById(R.id.UserAvatar);
        Glide.with(this).load(ImageURL).into(imageView);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("User", Username);
        editor.putString("Image", ImageURL);
        editor.putString("Email", Email);
        editor.apply();

        // Set the user's Username
        TextView username_view = findViewById(R.id.UserName);
        Log.d(TAG, String.format("SetUserInfo: {ImageURL: %s, Username: %s}", ImageURL, Username));
        username_view.setText(Username);
    }

    private void RegisterProfileListener(Query queryRef) {

        // Register Once
        queryRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> data = document.getData();
                    Log.d(TAG, sharedPref.getString("User", "DrPleaseRespect"));
                    Log.d(TAG, "Data Obtained From Firebase: " + data);
                    if (data != null) {
                        SetUserInfo((String) data.get("Image"), (String) data.get("Username"), (String) data.get("Email"));
                        Loaded = true;
                    } else {
                        Log.d(TAG, "Data is null");
                        Log.d(TAG, "RESETTING OPTIONS");
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("User", TEST_USER);
                        editor.apply();
                    }
                }
            }
        });
        // Listener for Future Changes
        snapshot_listener = queryRef.addSnapshotListener((query_snapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            for (QueryDocumentSnapshot doc : query_snapshots) {
                if (doc != null && doc.exists()) {
                    Map<String, Object> data = doc.getData();
                    Log.d(TAG, sharedPref.getString("User", "DrPleaseRespect"));
                    Log.d(TAG, "Data Obtained From Firebase: " + data);
                    if (data != null) {
                        SetUserInfo((String) data.get("Image"), (String) data.get("Username"), (String) data.get("Email"));
                        Loaded = true;
                    } else {
                        Log.d(TAG, "Data is null");
                        Log.d(TAG, "RESETTING OPTIONS");
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("User", TEST_USER);
                        editor.apply();
                    }
                }

            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        if (key != null && key.equals("User")) {
            Log.d(TAG, "User Changed: " + sharedPreferences.getString("User", "DrPleaseRespect"));
            Query query = FirebaseFirestore.getInstance().collection("UserData").whereEqualTo("Username", sharedPreferences.getString("User", "DrPleaseRespect"));
            RegisterProfileListener(query);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = getSharedPreferences(getString(R.string.ProfileState), MODE_PRIVATE);


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Enable offline data persistence
        FirebaseFirestoreSettings settings =
                new FirebaseFirestoreSettings.Builder(db.getFirestoreSettings())
                        // Use memory-only cache
                        .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
                        // Use persistent disk cache (default)
                        .setLocalCacheSettings(PersistentCacheSettings.newBuilder()
                                .build())
                        .build();
        db.setFirestoreSettings(settings);

        // Prevent the UI from rendering until the data is loaded
        // Set up an OnPreDrawListener to the root view.
        final View content = findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        // Check whether the initial data is ready.
                        if (Loaded) {
                            // The content is ready. Start drawing.
                            content.getViewTreeObserver().removeOnPreDrawListener(this);
                            return true;
                        } else {
                            // The content isn't ready. Suspend.
                            return false;
                        }
                    }
                });


        // Initialize Debug Preferences

        //while (sharedPref == null) {
        //    Log.d(TAG, "SOMETHING HAS GONE ABSOLUTELY FUCKED!");
        //}
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("User", sharedPref.getString("User", "DrPleaseRespect"));
        editor.apply();


        // Get the user's data from the Firebase Firestore
        sharedPref.registerOnSharedPreferenceChangeListener(this);

        Query query = FirebaseFirestore.getInstance().collection("UserData").whereEqualTo("Username", sharedPref.getString("User", "DrPleaseRespect"));

        RegisterProfileListener(query);


        // Profile Button
        findViewById(R.id.Profile).setOnClickListener(v -> {
            // Open the Profile Activity
            Log.d(TAG, "Profile Button Clicked");
            // Start DebuggingPage Activity
            Intent intent = new Intent(this, ProfilePage.class);
            startActivity(intent);
        });


    }
}