package com.drpleaserespect.nyaamii.Database.DAOs;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.drpleaserespect.nyaamii.Database.DataEntites.CrossRefs.UserCartCrossRef;
import com.drpleaserespect.nyaamii.Database.DataEntites.DataClasses.UserWithCart;
import com.drpleaserespect.nyaamii.Database.DataEntites.DataClasses.UserWithHistory;
import com.drpleaserespect.nyaamii.Database.DataEntites.User;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface UserDao {
    @Insert
    public Completable insert(User user);

    @Delete
    public Completable delete(User user);

    @Query("SELECT * FROM Users WHERE UserName = :username LIMIT 1")
    public Single<User> getByUsername(String username);

    //@Query("SELECT * FROM Users WHERE UserID = :ID LIMIT 1")
    //public Single<User> getByID(int ID);

    @Transaction
    @Query("SELECT * FROM Users WHERE userName = :UserName LIMIT 1")
    public Flowable<UserWithCart> getUserWithCart(String UserName);

    @Transaction
    @Query("SELECT * FROM Users WHERE userName = :UserName LIMIT 1")
    public Flowable<UserWithHistory> getUserWithHistory(String UserName);

    @Insert
    public Completable insertUserWithCart(UserCartCrossRef userWithCart);




}
