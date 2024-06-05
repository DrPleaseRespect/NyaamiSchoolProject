package com.drpleaserespect.nyaamii.Database.DAOs;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.drpleaserespect.nyaamii.Database.DataEntites.CrossRefs.UserCartCrossRef;
import com.drpleaserespect.nyaamii.Database.DataEntites.DataClasses.OrderWithItem;
import com.drpleaserespect.nyaamii.Database.DataEntites.DataClasses.UserWithCart;
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
    public abstract Completable insert(User user);

    @Insert
    public abstract Completable insert(OrderItem item);

    @Insert
    public abstract void insert_sync(OrderItem item);

    @Delete
    public abstract Completable delete(User user);

    @Query("SELECT * FROM Users WHERE UserName = :username LIMIT 1")
    public abstract Single<User> getByUsername(String username);

    //@Query("SELECT * FROM Users WHERE UserID = :ID LIMIT 1")
    //public Single<User> getByID(int ID);

    @Transaction
    @Query("SELECT * FROM Users WHERE userName = :UserName LIMIT 1")
    public abstract Flowable<UserWithCart> getUserWithCart(String UserName);

    @Transaction
    @Query("SELECT * FROM Users WHERE userName = :UserName LIMIT 1")
    public abstract Flowable<UserWithHistory> getUserWithHistory(String UserName);

    @Transaction
    @Query("SELECT * FROM OrderItem WHERE OrderOwner = :UserName")
    public abstract Flowable<List<OrderWithItem>> getCart(String UserName);

    @Insert
    public abstract Completable insertUserWithCart(UserCartCrossRef userWithCart);



    public Completable addToCart(User user, StoreItem item) {
        OrderItem orderItem = new OrderItem(item.ItemID, 1, user.getUserName());
        return insert(orderItem);

    }
    public Completable addToCart(User user, OrderItem item) {
        UserCartCrossRef userCartCrossRef = new UserCartCrossRef(user.getUserName(), item.OrderID);
        return insertUserWithCart(userCartCrossRef);
    }




}
