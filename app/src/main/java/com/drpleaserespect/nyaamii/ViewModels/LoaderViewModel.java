package com.drpleaserespect.nyaamii.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class LoaderViewModel extends ViewModel {
    private final MutableLiveData<Boolean> LoadedObjects = new MutableLiveData<>();

    public MutableLiveData<Boolean> getLoadedObjects() {
        return LoadedObjects;
    }

    public void setStatus(Boolean loaded) {
        LoadedObjects.setValue(loaded);
    }

}
