package com.drpleaserespect.nyaamii.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.drpleaserespect.nyaamii.Database.DAOs.UserDao;
import com.drpleaserespect.nyaamii.Database.DataEntites.StoreItem;
import com.drpleaserespect.nyaamii.Database.NyaamiDatabase;
import com.drpleaserespect.nyaamii.R.id;
import com.drpleaserespect.nyaamii.R.layout;
import com.drpleaserespect.nyaamii.R.string;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProductDetailActivity extends AppCompatActivity {

    private static final String TAG = "ProductDetailActivity";
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private SharedPreferences sharedPref = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_product_detail);

        // Get the StoreItem object from the intent
        StoreItem item = getIntent().getParcelableExtra("StoreItem", StoreItem.class);

        // Get Shared Preferences
        sharedPref = getSharedPreferences(getString(string.ProfileState), MODE_PRIVATE);

        // Set Back Button
        findViewById(id.BackButton).setOnClickListener(v -> finish());

        // Get Loading Layout
        ConstraintLayout loadinglayout = findViewById(id.LoadingLayout);


        // Set Item Data
        ((TextView) findViewById(id.ItemName)).setText(item.getName());
        ((TextView) findViewById(id.ItemPrice)).setText(item.getPriceString());
        ((TextView) findViewById(id.ItemDescription)).setText(item.getDescription());
        Glide.with(this).load(item.getImageUrl()).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                loadinglayout.setVisibility(View.GONE);
                return false;
            }
        }).into((ImageView) findViewById(id.ItemImage));

        // Cart Button
        ImageView CartButton = findViewById(id.CartButton);
        CartButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        });

        // Add To Cart Button
        Button AddToCartButton = findViewById(id.AddToCartButton);
        AddToCartButton.setOnClickListener(v -> {
            AddToCart(item, false);
        });

        // Buy Now Button
        Button BuyNowButton = findViewById(id.BuyNowButton);
        BuyNowButton.setOnClickListener(v -> {
            AddToCart(item, true);
        });


        // Search Bar Functionality
        EditText SearchBar = findViewById(id.SearchBar);
        SearchBar.setImeOptions(EditorInfo.IME_ACTION_DONE);
        SearchBar.setImeActionLabel("Search", EditorInfo.IME_ACTION_DONE);
        SearchBar.setOnClickListener(v -> SearchBar.setCursorVisible(true));
        SearchBar.setOnEditorActionListener((v, actionId, event) -> {
            Log.d(TAG, "Action ID: " + actionId);
            Log.d(TAG, "Event: " + event);
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                v.setCursorVisible(false);
                Intent intent = new Intent(this, CategoryActivity.class);
                intent.putExtra("SearchQuery", v.getText().toString());
                intent.putExtra("Category", "");
                startActivity(intent);
            }

            return false;
        });
    }

    private void AddToCart(StoreItem item, boolean BuyNow) {
        // Get User
        String user = sharedPref.getString("User", "DrPleaseRespect");
        // Grab NyaamiDatabase Instance
        NyaamiDatabase db = NyaamiDatabase.getInstance(this);
        UserDao userDao = db.userDao();
        // Get User Entity
        // Add Item to Cart
        mDisposable.add(userDao.getByUsername(user).subscribeOn(Schedulers.io()).subscribe(user_entity -> {
            mDisposable.add(userDao.addToOrder(user_entity, item).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                if (BuyNow) {
                    Intent intent = new Intent(this, CartActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, getString(string.AddedToCartText), Toast.LENGTH_SHORT).show();
                }
            }, throwable -> {
                Log.e(TAG, "AddToCart: " + throwable.getMessage());
            }));
        }, throwable -> {
            Log.e(TAG, "Error: " + throwable.getMessage());
        }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
    }

}