package com.drpleaserespect.nyaamii;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "CategoryActivity";
    private ListenerRegistration DataListener;

    private SharedPreferences sharedPref = null;


    private void CreateDataListener(String Category, StoreItemViewModel viewModel, String SearchQuery) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        Query db_collection = db.collection("StoreData")
                .document(Category)
                .collection("Items");


        if (!SearchQuery.equals("")) {
            db_collection = db_collection.whereGreaterThanOrEqualTo("searchName", SearchQuery)
                    .whereLessThan("searchName", SearchQuery + "\uf8ff");
        }


        if (DataListener != null) {
            DataListener.remove();
        }
        DataListener = db_collection.addSnapshotListener((queryDocumentSnapshots, e) -> {
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

    private void SetUserAvatar(String ImageURL) {
        ImageView ProfileImage = findViewById(R.id.UserAvatar);
        if (ProfileImage != null) {
            Glide.with(this)
                    .load(ImageURL)
                    .into(ProfileImage);
        }
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        SetUserAvatar(sharedPreferences.getString("Image", "https://picsum.photos/200"));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Get Shared Preferences
        sharedPref = getSharedPreferences("com.drpleaserespect.nyaamii", MODE_PRIVATE);

        // Set the Avatar
        SetUserAvatar(sharedPref.getString("Image", "https://picsum.photos/200"));


        // Initiate Store Fragment ViewModel
        StoreItemViewModel viewModel = new ViewModelProvider(this).get(StoreItemViewModel.class);

        // Get the category from the intent
        String category = getIntent().getStringExtra("Category");

        // Set Text of the Category
        TextView CategoryText = findViewById(R.id.CategoryTextView);
        CategoryText.setText(category);


        // Get the store items from DB and set it to the view model
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CreateDataListener(category, viewModel, "");

        // Search Functionality
        EditText SearchBar = findViewById(R.id.SearchBar);
        SearchBar.setImeOptions(EditorInfo.IME_ACTION_DONE);
        SearchBar.setOnClickListener(v -> SearchBar.setCursorVisible(true));
        SearchBar.setOnEditorActionListener((v, actionId, event) -> {
            Log.d(TAG, "Action ID: " + actionId);
            Log.d(TAG, "Event: " + event);
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                v.setCursorVisible(false);
                CreateDataListener(category, viewModel, v.getText().toString().toLowerCase());
            }

            return false;
        });

        // BackButton Functionality
        ImageView BackButton = findViewById(R.id.BackButton);
        BackButton.setOnClickListener(v -> finish());

        // Profile Button Functionality
        ImageView ProfileButton = findViewById(R.id.UserAvatar);
        ProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfilePage.class);
            startActivity(intent);
        });







    }
}