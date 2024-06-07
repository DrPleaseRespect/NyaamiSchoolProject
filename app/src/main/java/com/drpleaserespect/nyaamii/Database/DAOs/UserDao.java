package com.drpleaserespect.nyaamii.Database.DAOs;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.drpleaserespect.nyaamii.Database.DataEntites.CrossRefs.UserHistoryCrossRef;
import com.drpleaserespect.nyaamii.Database.DataEntites.DataClasses.OrderWithItem;
import com.drpleaserespect.nyaamii.Database.DataEntites.DataClasses.UserWithHistory;
import com.drpleaserespect.nyaamii.Database.DataEntites.OrderItem;
import com.drpleaserespect.nyaamii.Database.DataEntites.StoreItem;
import com.drpleaserespect.nyaamii.Database.DataEntites.User;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public abstract class UserDao {

    @Insert
    public abstract Completable insertHistory(UserHistoryCrossRef userHistoryCrossRef);

    @Insert
    public abstract Completable insertOrder(User user);

    @Insert
    public abstract Completable insertOrder(OrderItem item);

    @Update
    public abstract Completable updateOrder(OrderItem item);


    @Delete
    public abstract Completable deleteOrder(OrderItem item);


    @Insert
    public abstract void insertOrder_sync(OrderItem item);

    @Delete
    public abstract Completable deleteUser(User user);


    @Query("SELECT * FROM Users WHERE UserName = :username LIMIT 1")
    public abstract Single<User> getByUsername(String username);

    @Transaction
    @Query("SELECT * FROM UserHistoryCrossRef WHERE userName = :UserName")
    public abstract Flowable<UserWithHistory> watchUserWithHistory(String UserName);

    @Transaction
    @Query("SELECT * FROM OrderItem WHERE OrderOwner = :UserName")
    public abstract Flowable<List<OrderWithItem>> watchCart(String UserName);

    @Transaction
    @Query("SELECT * FROM OrderItem WHERE OrderOwner = :UserName AND StoreItemID = :ItemID")
    public abstract Single<OrderWithItem> getCartItem(String UserName, String ItemID);

    @Transaction
    @Query("SELECT * FROM OrderItem WHERE OrderOwner = :UserName AND StoreItemID = :ItemID")
    public abstract OrderWithItem getCartItem_sync(String UserName, String ItemID);


    public Completable addToHistory(OrderItem item) {
        UserHistoryCrossRef userHistoryCrossRef = new UserHistoryCrossRef(item.OrderOwner, item.StoreItemID);
        return insertHistory(userHistoryCrossRef);
    }

    public Completable addToOrder(User user, StoreItem item) {

        if (getCartItem_sync(user.getUserName(), item.ItemID) != null) {
            OrderItem orderItem = getCartItem_sync(user.getUserName(), item.ItemID).order;
            orderItem.OrderQuantity += 1;
            return updateOrder(orderItem);
        }
        OrderItem orderItem = new OrderItem(item.ItemID, 1, user.getUserName());
        return insertOrder(orderItem);
    }


}
