package com.drpleaserespect.nyaamii.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class LoaderViewModel extends ViewModel {
    private final MutableLiveData<Boolean> LoadedObjects = new MutableLiveData<>();
    private final MutableLiveData<Integer> StartDelay = new MutableLiveData<>();

    public MutableLiveData<Boolean> getLoadedObjects() {
        return LoadedObjects;
    }

    public MutableLiveData<Integer> getStartDelay() {
        return StartDelay;
    }

    public void setStartDelay(Integer delay) {
        StartDelay.setValue(delay);
    }

    public void setStatus(Boolean loaded) {
        LoadedObjects.setValue(loaded);
    }

}
