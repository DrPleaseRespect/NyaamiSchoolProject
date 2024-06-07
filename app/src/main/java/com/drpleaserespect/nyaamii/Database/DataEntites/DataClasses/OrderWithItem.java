package com.drpleaserespect.nyaamii.Database.DataEntites.DataClasses;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.drpleaserespect.nyaamii.Database.DataEntites.OrderItem;
import com.drpleaserespect.nyaamii.Database.DataEntites.StoreItem;

import java.util.Objects;

public class OrderWithItem {
    @Embedded
    public OrderItem order;
    @Relation(
            parentColumn = "StoreItemID",
            entityColumn = "ItemID"
    )
    public StoreItem item;

    public OrderWithItem(OrderItem order, StoreItem item) {
        this.order = order;
        this.item = item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderWithItem that = (OrderWithItem) o;
        return Objects.equals(order, that.order) && Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, item);
    }
}
