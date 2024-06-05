package com.drpleaserespect.nyaamii.Database.DataEntites.CrossRefs;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"userName", "ItemID"})
public class UserHistoryCrossRef {
    @NonNull
    public String userName;
    @NonNull
    @ColumnInfo(index = true)
    public String ItemID;

    public UserHistoryCrossRef(@NonNull String userName, @NonNull String ItemID) {
        this.userName = userName;
        this.ItemID = ItemID;
    }
}
