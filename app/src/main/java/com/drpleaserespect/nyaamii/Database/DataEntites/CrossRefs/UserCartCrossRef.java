package com.drpleaserespect.nyaamii.Database.DataEntites.CrossRefs;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"userName", "OrderID"})
public class UserCartCrossRef {
    @NonNull
    public String userName;
    @NonNull
    @ColumnInfo(index = true)
    public String OrderID;

    public UserCartCrossRef(@NonNull String userName, @NonNull String OrderID) {
        this.userName = userName;
        this.OrderID = OrderID;
    }
}
