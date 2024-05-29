package com.drpleaserespect.nyaamii.DataObjects;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Locale;
import java.util.Map;

public class StoreItem implements Parcelable {
    private final String name;
    private final float price;
    private final String imageUrl;

    private final String documentId;
    private final String documentPath;

    private final String description;

    private static final String CurrencySuffix = "PHP";

    public StoreItem(DocumentSnapshot firebaseDoc) {
        Map<String, Object> data = firebaseDoc.getData();
        this.name = (String) data.getOrDefault("Name", "Placeholder");
        this.price = ((Number) data.getOrDefault("Price", 0.0f)).floatValue();
        this.imageUrl = (String) data.getOrDefault("ImageURL", "https://picsum.photos/200");
        this.documentId = firebaseDoc.getId();
        this.documentPath = firebaseDoc.getReference().getPath();
        this.description = (String) data.getOrDefault("Description", "Lorem Ipsum Dolor Sit Amet");
    }

    protected StoreItem(Parcel in) {
        name = in.readString();
        price = in.readFloat();
        imageUrl = in.readString();
        documentId = in.readString();
        description = in.readString();
        documentPath = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeFloat(price);
        dest.writeString(imageUrl);
        dest.writeString(documentId);
        dest.writeString(description);
        dest.writeString(documentPath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StoreItem> CREATOR = new Creator<StoreItem>() {
        @Override
        public StoreItem createFromParcel(Parcel in) {
            return new StoreItem(in);
        }

        @Override
        public StoreItem[] newArray(int size) {
            return new StoreItem[size];
        }
    };

    public void PreCacheImage(Context context) {
        // PreCache image
        Glide.with(context).load(imageUrl).preload();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        StoreItem storeItem = (StoreItem) o;
        return name.equals(storeItem.name) && (price == storeItem.price) && imageUrl.equals(storeItem.imageUrl);
    }

    @Override
    public String toString() {
        return "StoreItem{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                ", documentId='" + documentId + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public String getPriceString() {
        return String.format(Locale.getDefault(), "%.2f %s", price, CurrencySuffix);
    }

    public static String getCurrencySuffix() {
        return CurrencySuffix;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getId() {
        return documentId;
    }
    public String getDescription() { return description; }

    public String getDocumentPath() {
        return documentPath;
    }

    public boolean equalsID(StoreItem storeItem) {
        return documentId.equals(storeItem.getId());
    }

}
