package com.drpleaserespect.nyaamii.Database.DataEntites;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.Ignore;

import java.util.Objects;
import java.util.UUID;

@Fts4
@Entity(tableName = "StoreItems")
public class StoreItem implements Parcelable {

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
    private static final String CurrencySuffix = "PHP";
    public String ItemID = UUID.randomUUID().toString();
    public String Name;
    public double Price;
    public String ImageUrl;
    public String Description;
    public String Category;
    public boolean FeaturedItem = false;

    public StoreItem(String Name, double Price, String ImageUrl, String Description, String Category, boolean FeaturedItem) {
        this.Name = Name;
        this.Price = Price;
        this.ImageUrl = ImageUrl;
        this.Description = Description;
        this.Category = Category;
        this.FeaturedItem = FeaturedItem;
    }

    @Ignore
    public StoreItem(String Name, double Price, String ImageUrl, String Description, String Category) {
        this.Name = Name;
        this.Price = Price;
        this.ImageUrl = ImageUrl;
        this.Description = Description;
        this.Category = Category;
        this.FeaturedItem = false;
    }

    protected StoreItem(Parcel in) {
        ItemID = in.readString();
        Name = in.readString();
        Price = in.readDouble();
        ImageUrl = in.readString();
        Description = in.readString();
        Category = in.readString();
        FeaturedItem = in.readBoolean();
    }

    public static String getCurrencySuffix() {
        return CurrencySuffix;
    }

    @Override
    public String toString() {
        return "StoreItem{" +
                "ItemID='" + ItemID + '\'' +
                ", Name='" + Name + '\'' +
                ", Price=" + Price +
                ", ImageUrl='" + ImageUrl + '\'' +
                ", Description='" + Description + '\'' +
                ", Category='" + Category + '\'' +
                ", FeaturedItem=" + FeaturedItem +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreItem storeItem = (StoreItem) o;
        return Double.compare(storeItem.getPrice(), getPrice()) == 0 && FeaturedItem == storeItem.FeaturedItem && Objects.equals(ItemID, storeItem.ItemID) && Objects.equals(getName(), storeItem.getName()) && Objects.equals(getImageUrl(), storeItem.getImageUrl()) && Objects.equals(getDescription(), storeItem.getDescription()) && Objects.equals(Category, storeItem.Category);
    }

    public boolean equalsID(Object o) {
        StoreItem storeItem = (StoreItem) o;
        return Objects.equals(ItemID, storeItem.ItemID);
    }

    public String getName() {
        return Name;
    }

    public double getPrice() {
        return Price;
    }

    public String getPriceString() {
        return Price + " " + getCurrencySuffix();
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public String getDescription() {
        return Description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(ItemID);
        dest.writeString(Name);
        dest.writeDouble(Price);
        dest.writeString(ImageUrl);
        dest.writeString(Description);
        dest.writeString(Category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ItemID, getName(), getPrice(), getImageUrl(), getDescription(), Category, FeaturedItem);
    }
}
