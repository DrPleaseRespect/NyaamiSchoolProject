package com.drpleaserespect.nyaamii;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MemoryCacheSettings;
import com.google.firebase.firestore.PersistentCacheSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "MainActivity";
    private static final String TEST_USER = "DrPleaseRespect";

    private static ListenerRegistration user_snapshot_listener = null;
    private static ListenerRegistration store_snapshot_listener = null;
    private final Map<String, Boolean> Loader = new HashMap<>();
    private SharedPreferences sharedPref = null;

    private final List<MaterialButton> CategoryButtons = new ArrayList<>();

    private boolean AllLoaded() {
        for (Map.Entry<String, Boolean> entry : Loader.entrySet()) {
            if (!entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    private void CreateDataListener(StoreItemViewModel viewModel, String SearchQuery) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        Query db_collection = db.collectionGroup("Items");


        if (!SearchQuery.equals("")) {
            db_collection = db_collection.whereGreaterThanOrEqualTo("searchName", SearchQuery)
                    .whereLessThan("searchName", SearchQuery + "\uf8ff");
        }


        if (store_snapshot_listener != null) {
            store_snapshot_listener.remove();
        }
        store_snapshot_listener = db_collection.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            List<StoreItem> storeItems = new ArrayList<>();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                if (doc != null && doc.exists()) {
                    Map<String, Object> data = doc.getData();
                    Log.d(TAG, "Data Obtained From Firebase: " + data);
                    if (data != null) {
                        StoreItem item = new StoreItem(doc);
                        storeItems.add(item);
                    }
                }
            }
            viewModel.setStoreItems(storeItems);
        });

    }

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
                        Loader.put("Profile", true);
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
        user_snapshot_listener = queryRef.addSnapshotListener((query_snapshots, e) -> {
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
                        Loader.put("Profile", true);
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

        // Get the shared preferences
        sharedPref = getSharedPreferences("com.drpleaserespect.nyaamii", MODE_PRIVATE);

        // Setup Loader States
        Loader.put("Profile", false);
        //Loader.put("Store", false);
        Loader.put("Categories", false);

        // Setup Firebase Firestore
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
                        if (AllLoaded()) {
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


        // Category Data Setup
        db.collection("StoreData").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("Tag", document.getData().toString());
                    MaterialButton materialbutton = new MaterialButton(this);
                    materialbutton.setText((CharSequence) document.getId());
                    CategoryButtons.add(materialbutton);
                }
                LinearLayout buttonstuff = findViewById(R.id.CategoryLayout);
                LinearLayout.LayoutParams layouts = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
                layouts.setMargins(margin, margin, 0, 0);
                for (MaterialButton button : CategoryButtons) {
                    button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                    button.setCornerRadius(20);
                    button.setOnClickListener(v -> {
                        Log.d(TAG, button.getText() + " Category Button Clicked");
                        // Start CategoryActivity Activity
                        Intent intent = new Intent(this, CategoryActivity.class);
                        intent.putExtra("Category", button.getText());
                        startActivity(intent);
                    });
                    buttonstuff.addView(button, layouts);
                }
                Loader.put("Categories", true);
            }
        });

        // Profile Button
        findViewById(R.id.Profile).setOnClickListener(v -> {
            // Open the Profile Activity
            Log.d(TAG, "Profile Button Clicked");
            // Start DebuggingPage Activity
            Intent intent = new Intent(this, ProfilePage.class);
            startActivity(intent);
        });

        // Initialize Main Store Fragment
        StoreItemViewModel store_viewModel = new ViewModelProvider(this).get(StoreItemViewModel.class);


        // Get the store items from DB and set it to the view model
        CreateDataListener(store_viewModel, "");

        // Search Functionality
        EditText SearchBar = findViewById(R.id.SearchBar);
        SearchBar.setImeOptions(EditorInfo.IME_ACTION_DONE);
        SearchBar.setOnClickListener(v -> SearchBar.setCursorVisible(true));
        SearchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                v.setCursorVisible(false);
                CreateDataListener(store_viewModel, v.getText().toString().toLowerCase());
            }

            return false;
        });

        // Carousel Data
        // Get the store items from DB and set it to the view model
        StoreItemsCarouselViewModel carousel_viewmodel = new ViewModelProvider(this).get(StoreItemsCarouselViewModel.class);
        db.collection("FeaturedData").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    DocumentReference data = (DocumentReference) document.getData().get("Document");
                    Log.d(TAG, data.toString());
                    tasks.add(data.get());
                }
                Tasks.whenAllComplete(tasks).addOnCompleteListener(task1 -> {
                    List<StoreItem> storeItems = new ArrayList<>();
                    for (Task<?> document : task1.getResult()) {
                        DocumentSnapshot document2 = (DocumentSnapshot) document.getResult();
                        if (document2 != null && document2.exists()) {
                            Map<String, Object> data = document2.getData();
                            Log.d(TAG, "Data Obtained From Firebase: " + data);
                            if (data != null) {
                                StoreItem item = new StoreItem(document2);
                                storeItems.add(item);
                            }
                        }
                    }
                    Log.d(TAG, "CarouselData : " + storeItems.toString());
                    carousel_viewmodel.setStoreItems(storeItems);
                });
            }
        });


    }
}