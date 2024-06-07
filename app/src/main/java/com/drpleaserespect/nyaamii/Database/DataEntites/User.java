package com.drpleaserespect.nyaamii.Database.DataEntites;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.auto.value.AutoValue;
import com.google.auto.value.AutoValue.CopyAnnotations;

@AutoValue
@Entity(tableName = "Users")
public abstract class User {

    public static User create(String userName, String profileImage, String email) {
        if (profileImage == null) profileImage = "";
        if (email == null) email = "";
        return new AutoValue_User(userName, profileImage, email);
    }

    @CopyAnnotations

    @NonNull
    @PrimaryKey()
    public abstract String getUserName();

    @NonNull
    public abstract String getProfileImage();

    @NonNull
    public abstract String getEmail();

}