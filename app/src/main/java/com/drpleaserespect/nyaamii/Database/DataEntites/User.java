package com.drpleaserespect.nyaamii.Database.DataEntites;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
@Entity(tableName = "Users")
public abstract class User {

    @AutoValue.CopyAnnotations

    @NonNull
    @PrimaryKey()
    public abstract String getUserName();


    public abstract String getProfileImage();

    public abstract String getEmail();

    public static User create(String userName, String profileImage, String email) {
        return new AutoValue_User(userName,profileImage, email);
    }

}