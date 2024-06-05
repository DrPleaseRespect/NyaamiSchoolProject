package com.drpleaserespect.nyaamii.Database.DAOs;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.drpleaserespect.nyaamii.Database.DataEntites.StoreItem;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface StoreItemDao {

    @Insert
    Completable insert(StoreItem storeItem);

    @Insert
    Completable insertStoreItems(StoreItem... storeItems);

    @Delete
    Completable delete(StoreItem storeItem);

    @Query("SELECT * FROM StoreItems")
    Single<List<StoreItem>> getAll();

    @Query("SELECT * FROM StoreItems WHERE featuredItem != 0")
    Flowable<List<StoreItem>> watchFeaturedItems();
    @Query("SELECT * FROM StoreItems")
    Flowable<List<StoreItem>> watchAll();

    @Query("SELECT * FROM StoreItems" +
            " WHERE (name MATCH :search OR description MATCH :search)" +
            " OR (name MATCH (:search || '*') OR description MATCH (:search || '*'))")
    Flowable<List<StoreItem>> watchSearch(String search);

    @Query("SELECT * FROM StoreItems WHERE category = :category")
    Flowable<List<StoreItem>> watchAllinCategory(String category);

    @Query("SELECT * FROM StoreItems" +
            " WHERE category = :category" +
            " AND (name MATCH :search OR description MATCH :search)" +
            " OR (name MATCH (:search || '*') OR description MATCH (:search || '*'))")
    Flowable<List<StoreItem>> watchSearchInCategory(String category, String search);

    @Query("SELECT * FROM StoreItems WHERE category = :category")
    Single<List<StoreItem>> getAllinCategory(String category);


    @Query("SELECT DISTINCT category FROM StoreItems")
    Single<List<String>> getAllCategories();

    @Query("SELECT * FROM StoreItems WHERE name = :name")
    Single<StoreItem> getByName(String name);

    @Query("SELECT * FROM StoreItems WHERE rowid = :ID")
    Single<StoreItem> getByID(int ID);


}
