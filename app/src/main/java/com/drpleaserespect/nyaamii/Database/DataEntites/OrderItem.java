package com.drpleaserespect.nyaamii.Database.DataEntites;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity()
public class OrderItem {
    @PrimaryKey
    @NonNull
    public String OrderID = UUID.randomUUID().toString();

    @NonNull
    public String StoreItemID;

    @NonNull
    public String OrderOwner;
    public int OrderQuantity;

    public OrderItem(String StoreItemID, int OrderQuantity, String OrderOwner) {
        this.StoreItemID = StoreItemID;
        this.OrderQuantity = OrderQuantity;
        this.OrderOwner = OrderOwner;
    }


    @Override
    public String toString() {
        return "OrderItem{" +
                "OrderID='" + OrderID + '\'' +
                ", ItemID='" + StoreItemID + '\'' +
                ", OrderQuantity=" + OrderQuantity +
                '}';
    }
}
