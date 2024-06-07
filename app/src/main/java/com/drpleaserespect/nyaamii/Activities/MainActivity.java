package com.drpleaserespect.nyaamii.Activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.drpleaserespect.nyaamii.DataObjects.Loader;
import com.drpleaserespect.nyaamii.DataObjects.Loader.Listener;
import com.drpleaserespect.nyaamii.Database.DAOs.StoreItemDao;
import com.drpleaserespect.nyaamii.Database.DAOs.UserDao;
import com.drpleaserespect.nyaamii.Database.DataEntites.User;
import com.drpleaserespect.nyaamii.Database.NyaamiDatabase;
import com.drpleaserespect.nyaamii.R.id;
import com.drpleaserespect.nyaamii.R.layout;
import com.drpleaserespect.nyaamii.R.string;
import com.drpleaserespect.nyaamii.ViewModels.LoaderViewModel;
import com.drpleaserespect.nyaamii.ViewModels.StoreItemViewModel;
import com.drpleaserespect.nyaamii.ViewModels.StoreItemsCarouselViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements OnSharedPreferenceChangeListener {

    private static final String TAG = "MainActivity";
    private static final String TEST_USER = "DrPleaseRespect";

    private static final ListenerRegistration user_snapshot_listener = null;
    private static Disposable store_snapshot_listener = null;
    private static Disposable carousel_listener = null;
    private final List<MaterialButton> CategoryButtons = new ArrayList<>();
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private Loader loader_obj = null;
    private LoaderViewModel loaderViewModel = null;
    private SharedPreferences sharedPref = null;

    private void SetUserInfo(String ImageURL, String Username, String Email) {
        // Set the user's Image
        ImageView imageView = findViewById(id.UserAvatar);
        Glide.with(this).load(ImageURL).into(imageView);
        Editor editor = sharedPref.edit();
        editor.putString("User", Username);
        editor.putString("Image", ImageURL);
        editor.putString("Email", Email);
        editor.apply();

        // Set the user's Username
        TextView username_view = findViewById(id.UserName);
        Log.d(TAG, String.format("SetUserInfo: {ImageURL: %s, Username: %s}", ImageURL, Username));
        username_view.setText(Username);

        loader_obj.setLoaded("Profile");
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        if (key != null && key.equals("User"))
            Log.d(TAG, "User Changed: " + sharedPreferences.getString("User", "DrPleaseRespect"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        // Initialize Loader
        loader_obj = new Loader(new String[]{"Profile", "Categories", "Items"});


        // Get the loader view model
        loaderViewModel = new ViewModelProvider(this).get(LoaderViewModel.class);

        loader_obj.setListener(new Listener() {
            @Override
            public void onLoaded(String object) {

            }

            @Override
            public void onAllLoaded() {
                loaderViewModel.setStartDelay(2000);
                loaderViewModel.setStatus(true);
            }
        });
        // Get the shared preferences
        sharedPref = getSharedPreferences(getString(string.ProfileState), MODE_PRIVATE);
        sharedPref.registerOnSharedPreferenceChangeListener(this);

        //loaderViewModel.setStatus(true);

        // Setup Firebase Firestore
        //deleteDatabase("NyaamiDatabase");
        NyaamiDatabase db = NyaamiDatabase.getInstance(this);
        StoreItemDao storeItemDao = db.storeItemDao();



        UserDao userDao = db.userDao();
        // Create a new user for testing
        User user = User.create("DrPleaseRespect", "https://drpleaserespect.github.io/assets/img/Avatar.png", "juliannayr2007@gmail.com");
        //mDisposable.add(userDao.insert(user)
        //        .subscribeOn(Schedulers.io())
        //        .observeOn(AndroidSchedulers.mainThread())
        //        .subscribe(() -> {
        //            Log.d(TAG, "User Inserted");
        //            SetUserInfo(user.getProfileImage(), user.getUserName(), user.getEmail());
        //        }, throwable -> {
        //            Log.e(TAG, "Error Inserting User", throwable);
        //        }));


        // Get User Data from DB
        mDisposable.add(userDao.getByUsername(sharedPref.getString("User", "DrPleaseRespect"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user1 -> {
                    Log.d(TAG, "User Retrieved");
                    SetUserInfo(user1.getProfileImage(), user1.getUserName(), user1.getEmail());
                }, throwable -> {
                    Log.e(TAG, "Error Getting User", throwable);
                }));


        // Category Data Setup
        mDisposable.add(
                storeItemDao.getAllCategories()
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.computation())
                        .subscribe(
                                categories -> {
                                    LinearLayout buttonstuff = findViewById(id.CategoryLayout);
                                    LayoutParams layouts = new LayoutParams(
                                            LayoutParams.WRAP_CONTENT,
                                            LayoutParams.WRAP_CONTENT
                                    );
                                    int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
                                    layouts.setMargins(margin, margin, 0, 0);
                                    for (String category : categories) {
                                        MaterialButton materialbutton = new MaterialButton(this);
                                        materialbutton.setText(category);
                                        CategoryButtons.add(materialbutton);
                                    }
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
                                    loader_obj.setLoaded("Categories");
                                }, throwable -> {
                                    Log.e(TAG, "Error Getting Categories", throwable);
                                }
                        )
        );


        // Initialize Debug Preferences

        Editor editor = sharedPref.edit();
        editor.putString("User", sharedPref.getString("User", "DrPleaseRespect"));
        editor.apply();


        // Initialize Main Store Fragment
        StoreItemViewModel store_viewModel = new ViewModelProvider(this).get(StoreItemViewModel.class);

        // Initialize Items

        store_snapshot_listener = db.storeItemDao()
                .watchAll()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(
                        storeItems_watching -> {
                            store_viewModel.postStoreItems(storeItems_watching);
                            Log.d(TAG, "Item Initialization Finished");
                        }, throwable -> {
                            Log.e(TAG, "Item Initialization Failed " + throwable);
                        }
                );


        // Profile Button
        findViewById(id.Profile).setOnClickListener(v -> {
            // Open the Profile Activity
            Log.d(TAG, "Profile Button Clicked");
            // Start DebuggingPage Activity
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });


        // Search Functionality
        EditText SearchBar = findViewById(id.SearchBar);
        SearchBar.setImeOptions(EditorInfo.IME_ACTION_DONE);
        SearchBar.setOnClickListener(v -> SearchBar.setCursorVisible(true));
        SearchBar.setImeActionLabel("Search", EditorInfo.IME_ACTION_DONE);
        SearchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                v.setCursorVisible(false);
                if (store_snapshot_listener != null) {
                    store_snapshot_listener.dispose();
                }
                if (!v.getText().toString().equals("")) store_snapshot_listener = db.storeItemDao()
                        .watchSearch(v.getText().toString() + '*')
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                storeItems_watching -> {
                                    store_viewModel.postStoreItems(storeItems_watching);
                                    Log.d(TAG, "Item Initialization Finished");
                                }, throwable -> {
                                    Log.e(TAG, "Item Initialization Failed " + throwable);
                                }
                        );
                else store_snapshot_listener = db.storeItemDao()
                        .watchAll()
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                storeItems_watching -> {
                                    db.close();
                                    store_viewModel.postStoreItems(storeItems_watching);
                                    Log.d(TAG, "Item Initialization Finished");
                                }, throwable -> {
                                    Log.e(TAG, "Item Initialization Failed " + throwable);
                                }
                        );

                //CreateDataListener(store_viewModel, v.getText().toString().toLowerCase());
            }

            return false;
        });

        // Carousel Data
        carousel_listener = db.storeItemDao()
                .watchFeaturedItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        storeItems_watching -> {
                            StoreItemsCarouselViewModel carousel_viewModel = new ViewModelProvider(this)
                                    .get(StoreItemsCarouselViewModel.class);
                            Log.d(TAG, "Carousel Data Set: " + storeItems_watching);
                            carousel_viewModel.postStoreItems(storeItems_watching);
                            Log.d(TAG, "Carousel Data Set");
                            loader_obj.setLoaded("Items");

                        }, throwable -> {
                            Log.e(TAG, "Carousel Data Failed " + throwable);
                        }
                );


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
    }
}