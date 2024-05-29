package com.drpleaserespect.nyaamii.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.drpleaserespect.nyaamii.R;
import com.drpleaserespect.nyaamii.DataObjects.StoreItem;
import com.drpleaserespect.nyaamii.R.id;
import com.drpleaserespect.nyaamii.R.layout;
import com.drpleaserespect.nyaamii.R.string;
import com.drpleaserespect.nyaamii.ViewModels.StoreItemViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity implements OnSharedPreferenceChangeListener {

    private static final String TAG = "CategoryActivity";
    private ListenerRegistration DataListener;

    private SharedPreferences sharedPref = null;


    protected void SetUserAvatar(String ImageURL) {
        ImageView ProfileImage = findViewById(id.UserAvatar);
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

    private void CreateDataListener(String Category, StoreItemViewModel viewModel, String SearchQuery) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query db_collection;
        if (Category.equals("")) {
            Log.d(TAG, "Category is empty");
            db_collection = db.collectionGroup("Items");
        } else {
            Log.d(TAG, "Category is not empty");
            db_collection = db.collection("StoreData")
                    .document(Category)
                    .collection("Items");
        }

        if (!SearchQuery.equals("")) {
            db_collection = db_collection.whereGreaterThanOrEqualTo("searchName", SearchQuery)
                    .whereLessThan("searchName", SearchQuery + '\uf8ff');
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_category);

        // Get Shared Preferences
        sharedPref = getSharedPreferences(getString(string.ProfileState), MODE_PRIVATE);
        sharedPref.registerOnSharedPreferenceChangeListener(this);

        // Set the Avatar
        SetUserAvatar(sharedPref.getString("Image", "https://picsum.photos/200"));


        // Initiate Store Fragment ViewModel
        StoreItemViewModel viewModel = new ViewModelProvider(this).get(StoreItemViewModel.class);

        // Get the category from the intent
        String category = getIntent().getStringExtra("Category");
        String searchQuery = getIntent().getStringExtra("SearchQuery");
        if (searchQuery == null) {
            searchQuery = "";
        }
        if (category == null) {
            category = "";
        }



        // Set Text of the Category
        TextView CategoryText = findViewById(id.CategoryTextView);
        CategoryText.setText(category);


        // Get the store items from DB and set it to the view model
        CreateDataListener(category, viewModel, searchQuery);

        // Search Functionality
        EditText SearchBar = findViewById(id.SearchBar);

        if (!searchQuery.equals("")) {
            SearchBar.setText(searchQuery);
        }

        SearchBar.setImeOptions(EditorInfo.IME_ACTION_DONE);
        SearchBar.setImeActionLabel("Search", EditorInfo.IME_ACTION_DONE);
        SearchBar.setOnClickListener(v -> SearchBar.setCursorVisible(true));
        String finalCategory = category;
        SearchBar.setOnEditorActionListener((v, actionId, event) -> {
            Log.d(TAG, "Action ID: " + actionId);
            Log.d(TAG, "Event: " + event);
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                v.setCursorVisible(false);
                CreateDataListener(finalCategory, viewModel, v.getText().toString().toLowerCase());
            }

            return false;
        });

        // BackButton Functionality
        ImageView BackButton = findViewById(id.BackButton);
        BackButton.setOnClickListener(v -> finish());

        // Profile Button Functionality
        ImageView ProfileButton = findViewById(id.UserAvatar);
        ProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });







    }
}