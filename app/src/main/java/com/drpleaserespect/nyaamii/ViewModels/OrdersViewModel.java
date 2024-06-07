package com.drpleaserespect.nyaamii.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.drpleaserespect.nyaamii.Database.DataEntites.DataClasses.OrderWithItem;

import java.util.List;

public class OrdersViewModel extends ViewModel {
    private final MutableLiveData<List<OrderWithItem>> orders = new MutableLiveData<>();
    private final MutableLiveData<Boolean> CartMode = new MutableLiveData<>(true);

    public MutableLiveData<Boolean> getCartMode() {
        return CartMode;
    }

    public void setCartMode(Boolean mode) {
        CartMode.setValue(mode);
    }

    public MutableLiveData<List<OrderWithItem>> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderWithItem> orders) {
        this.orders.setValue(orders);
    }

    public void postOrders(List<OrderWithItem> orders) {
        this.orders.postValue(orders);
    }

}
