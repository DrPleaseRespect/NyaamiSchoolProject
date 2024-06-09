package com.drpleaserespect.nyaamii.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.drpleaserespect.nyaamii.Database.DAOs.UserDao;
import com.drpleaserespect.nyaamii.Database.DataEntites.CrossRefs.UserHistoryCrossRef;
import com.drpleaserespect.nyaamii.Database.DataEntites.DataClasses.OrderWithItem;
import com.drpleaserespect.nyaamii.Database.NyaamiDatabase;
import com.drpleaserespect.nyaamii.R;
import com.drpleaserespect.nyaamii.R.layout;
import com.drpleaserespect.nyaamii.ViewModels.OrdersViewModel;

import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TransactionActivity extends AppCompatActivity {

    private final String TAG = "TransactionActivity";
    private Disposable CartListener;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private SharedPreferences sharedPref = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_transaction);

        // Get the ViewModel
        OrdersViewModel ordersViewModel = new ViewModelProvider(this).get(OrdersViewModel.class);

        // Get the Shared Preferences
        sharedPref = getSharedPreferences(getString(R.string.ProfileState), MODE_PRIVATE);


        // DB
        NyaamiDatabase db = NyaamiDatabase.getInstance(this);
        UserDao userDao = db.userDao();

        // Obtain the Cart Data
        CartListener = userDao.watchCart(sharedPref.getString("User", "DrPleaseRespect"))
                .subscribeOn(Schedulers.io())
                .subscribe(orders -> {
                    ordersViewModel.postOrders(orders);
                }, throwable -> {
                    Log.e(TAG, "CartData: " + throwable.getMessage());
                });


        // Back Button
        findViewById(R.id.BackButtonLinearLayout).setOnClickListener(v -> {
            finish();
        });


        // Checkout Button
        findViewById(R.id.CheckoutButton).setOnClickListener(v -> {

            // Save the cart to the History
            List<OrderWithItem> history_values = ordersViewModel.getOrders().getValue();
            if (history_values != null) {
                for (OrderWithItem order : history_values) {
                    mDisposable.add(userDao.insertHistory(
                                    new UserHistoryCrossRef(
                                            sharedPref.getString("User", "DrPleaseRespect"),
                                            order.item.ItemID)
                            )
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io()).subscribe(() -> {
                                mDisposable.add(userDao.deleteOrder(order.order)
                                        .subscribeOn(Schedulers.io()).
                                        observeOn(Schedulers.io()).
                                        subscribe(() -> {
                                            //Toast.makeText(this, getString(R.string.AddedToCartText), Toast.LENGTH_SHORT).show();
                                        }, throwable -> {
                                            Log.e(TAG, "Error: " + throwable.getMessage());
                                        }));
                            }, throwable -> {
                                Log.e(TAG, "Error: " + throwable.getMessage());
                            }));
                }
            }

            Intent intent = new Intent(this, SuccessfulOrder.class);
            startActivity(intent);

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CartListener.dispose();
    }
}