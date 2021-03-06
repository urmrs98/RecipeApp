package com.velasco.recipeapp.Pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Recipe implements Parcelable {

    // members variable same as table's columns' name
    private int id;
    private String name;
    private String description;
    private int category;
    private int userid;

    // constructor
    public Recipe(int id, String name, String description, int category, int userid) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.userid = userid;
    }

    // setters and getters
    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    // Parcelable
    protected Recipe(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        category = in.readInt();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(category);
    }
}
