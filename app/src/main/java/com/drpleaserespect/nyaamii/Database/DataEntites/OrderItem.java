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

    public String ItemID;

    public String OrderOwner;
    public int OrderQuantity;

    public OrderItem(String ItemID, int OrderQuantity, String OrderOwner) {
        this.ItemID = ItemID;
        this.OrderQuantity = OrderQuantity;
        this.OrderOwner = OrderOwner;
    }


    @Override
    public String toString() {
        return "OrderItem{" +
                "OrderID='" + OrderID + '\'' +
                ", ItemID='" + ItemID + '\'' +
                ", OrderQuantity=" + OrderQuantity +
                '}';
    }
}
