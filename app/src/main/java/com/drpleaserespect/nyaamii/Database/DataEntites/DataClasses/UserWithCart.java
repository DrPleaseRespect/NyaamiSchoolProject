package com.drpleaserespect.nyaamii.Database.DataEntites.DataClasses;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.drpleaserespect.nyaamii.Database.DataEntites.CrossRefs.UserCartCrossRef;
import com.drpleaserespect.nyaamii.Database.DataEntites.OrderItem;
import com.drpleaserespect.nyaamii.Database.DataEntites.StoreItem;
import com.drpleaserespect.nyaamii.Database.DataEntites.User;

import java.util.List;

public class UserWithCart {
    @Embedded public User user;
    @Relation(
            parentColumn = "userName",
            entityColumn = "OrderID",
            associateBy = @Junction(UserCartCrossRef.class)
    )
    public List<OrderItem> cart;


    @Override
    public String toString() {
        return "UserWithCart{" +
                "user=" + user +
                ", cart=" + cart +
                '}';
    }
}
