package com.drpleaserespect.nyaamii.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.drpleaserespect.nyaamii.DataObjects.OrderObject;

import java.util.List;

public class OrdersViewModel extends ViewModel {
    private final MutableLiveData<List<OrderObject>> orders = new MutableLiveData<>();
    private final MutableLiveData<Boolean> CartMode = new MutableLiveData<>(true);

    public MutableLiveData<Boolean> getCartMode() {
        return CartMode;
    }

    public void setCartMode(Boolean mode) {
        CartMode.setValue(mode);
    }

    public MutableLiveData<List<OrderObject>> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderObject> orders) {
        this.orders.setValue(orders);
    }
}
