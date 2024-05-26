package com.drpleaserespect.nyaamii.DataObjects;

import java.util.HashMap;
import java.util.Map;

public class Loader {

    public interface Listener {
        void onLoaded(String object);
        void onAllLoaded();
    }

    private final Map<String, Boolean> loaded_objects = new HashMap<>();
    private Listener listener = null;
    public Loader(String[] load_objects) {
        for (String object : load_objects) {
            loaded_objects.put(object, false);
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
    public void setLoaded(String object) {
        loaded_objects.put(object, true);
        if (listener != null) {
            listener.onLoaded(object);
            if (AllLoaded()) {
                listener.onAllLoaded();
            }
        }
    };

    public void setUnloaded(String object) {
        loaded_objects.put(object, false);
    };

    public boolean isLoaded(String object) {
        return loaded_objects.get(object);
    }

    public boolean AllLoaded() {
        for (Map.Entry<String, Boolean> entry : loaded_objects.entrySet()) {
            if (!entry.getValue()) {
                return false;
            }
        }
        return true;
    }
}
