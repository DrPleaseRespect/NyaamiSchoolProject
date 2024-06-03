package com.drpleaserespect.nyaamii.Database.DataEntites.DataClasses;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.drpleaserespect.nyaamii.Database.DataEntites.OrderItem;
import com.drpleaserespect.nyaamii.Database.DataEntites.StoreItem;

public class OrderWithItem {
    @Embedded
    public StoreItem item;
    @Relation(
            parentColumn = "ItemID",
            entityColumn = "ItemID"
    )
    public OrderItem order;

}
