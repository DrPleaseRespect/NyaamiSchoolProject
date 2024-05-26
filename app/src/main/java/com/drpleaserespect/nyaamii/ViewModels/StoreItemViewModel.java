package com.drpleaserespect.nyaamii.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.drpleaserespect.nyaamii.DataObjects.StoreItem;

import java.util.List;


public class StoreItemViewModel extends ViewModel {
    private final MutableLiveData<List<StoreItem>> storeItems = new MutableLiveData<>();

    public MutableLiveData<List<StoreItem>> getStoreItems() {
        return storeItems;
    }

    public void setStoreItems(List<StoreItem> items) {
        storeItems.setValue(items);
    }

}
