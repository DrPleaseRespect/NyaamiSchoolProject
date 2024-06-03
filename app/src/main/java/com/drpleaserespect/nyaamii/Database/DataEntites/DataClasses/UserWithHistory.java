package com.drpleaserespect.nyaamii.Database.DataEntites.DataClasses;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.drpleaserespect.nyaamii.Database.DataEntites.CrossRefs.UserCartCrossRef;
import com.drpleaserespect.nyaamii.Database.DataEntites.CrossRefs.UserHistoryCrossRef;
import com.drpleaserespect.nyaamii.Database.DataEntites.StoreItem;
import com.drpleaserespect.nyaamii.Database.DataEntites.User;

import java.util.List;

public class UserWithHistory {
    @Embedded public User user;
    @Relation(
            parentColumn = "userName",
            entityColumn = "ItemID",
            associateBy = @Junction(UserHistoryCrossRef.class)
    )
    public List<StoreItem> history;
}
