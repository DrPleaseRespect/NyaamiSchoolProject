package com.drpleaserespect.nyaamii.Database;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.drpleaserespect.nyaamii.Database.DAOs.StoreItemDao;
import com.drpleaserespect.nyaamii.Database.DAOs.UserDao;
import com.drpleaserespect.nyaamii.Database.DataEntites.CrossRefs.UserCartCrossRef;
import com.drpleaserespect.nyaamii.Database.DataEntites.CrossRefs.UserHistoryCrossRef;
import com.drpleaserespect.nyaamii.Database.DataEntites.OrderItem;
import com.drpleaserespect.nyaamii.Database.DataEntites.StoreItem;
import com.drpleaserespect.nyaamii.Database.DataEntites.User;



@Database(entities = {StoreItem.class, User.class, OrderItem.class, UserCartCrossRef.class, UserHistoryCrossRef.class},
        version = 1
)
public abstract class NyaamiDatabase extends RoomDatabase {
    public abstract StoreItemDao storeItemDao();
    public abstract UserDao userDao();

    private static volatile NyaamiDatabase INSTANCE = null;

    public static NyaamiDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (NyaamiDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            NyaamiDatabase.class,
                            "NyaamiDatabase"
                    )
                            .createFromAsset("database/NyaamiDatabase.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
