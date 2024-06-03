package com.drpleaserespect.nyaamii.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.drpleaserespect.nyaamii.Database.DataEntites.StoreItem;

import java.util.List;

public class StoreItemsCarouselViewModel extends ViewModel {
    private final MutableLiveData<List<StoreItem>> storeItems = new MutableLiveData<>();

    public MutableLiveData<List<StoreItem>> getStoreItems() {
        return storeItems;
    }

    public void setStoreItems(List<StoreItem> items) {
        storeItems.setValue(items);
    }

    public void postStoreItems(List<StoreItem> items) {
        storeItems.postValue(items);
    }

    public void clearStoreItems() {
        List<StoreItem> Items = storeItems.getValue();
        if (Items != null) {
            Items.clear();
            storeItems.setValue(Items);
        }
    }

}
