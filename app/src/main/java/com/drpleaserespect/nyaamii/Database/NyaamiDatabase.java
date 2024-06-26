package com.drpleaserespect.nyaamii.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.drpleaserespect.nyaamii.Database.DAOs.StoreItemDao;
import com.drpleaserespect.nyaamii.Database.DAOs.UserDao;
import com.drpleaserespect.nyaamii.Database.DataEntites.CrossRefs.UserHistoryCrossRef;
import com.drpleaserespect.nyaamii.Database.DataEntites.OrderItem;
import com.drpleaserespect.nyaamii.Database.DataEntites.StoreItem;
import com.drpleaserespect.nyaamii.Database.DataEntites.User;


@Database(entities = {StoreItem.class, User.class, OrderItem.class, UserHistoryCrossRef.class},
        version = 2
)
public abstract class NyaamiDatabase extends RoomDatabase {
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            // Rename Table from OrderItem to OrderItem_old
            db.execSQL("ALTER TABLE OrderItem RENAME TO OrderItem_old");

            // Create Updated OrderItem Table
            db.execSQL("CREATE TABLE OrderItem (OrderOwner TEXT NOT NULL, OrderID TEXT NOT NULL, StoreItemID TEXT NOT NULL, OrderQuantity INTEGER NOT NULL, PRIMARY KEY(OrderID, StoreItemID, OrderOwner))");

            // Delete
            // Move data from old OrderItem table to OrderItem table
            db.execSQL("INSERT INTO OrderItem ( OrderOwner, OrderID, StoreItemID, OrderQuantity) SELECT OrderID, ItemID, OrderOwner, OrderQuantity FROM OrderItem_old");

            // Delete old OrderItem table
            db.execSQL("DROP TABLE OrderItem_old");

            // Drop UserCartCrossRef (Unneeded)
            db.execSQL("DROP TABLE UserCartCrossRef");


        }
    };
    private static volatile NyaamiDatabase INSTANCE = null;

    public static NyaamiDatabase getInstance(Context context) {
        if (INSTANCE == null) synchronized (NyaamiDatabase.class) {
            if (INSTANCE == null) INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            NyaamiDatabase.class,
                            "NyaamiDatabase"
                    )
                    .createFromAsset("database/NyaamiDatabase.db")
                    .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_1_2)
                    .build();
        }
        return INSTANCE;
    }

    public abstract StoreItemDao storeItemDao();

    public abstract UserDao userDao();

}


