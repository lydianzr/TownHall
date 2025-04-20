package com.example.townhall;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventHelper implements Parcelable {

    private int image;
    private String day;
    private String month;
    private String year;
    private String title;
    private String location;
    private String state;
    private String category;
    private String type;
    private String dateAdded;
    private String docId; // Added docId field

    // Constructor that accepts a date string in "dd-MM-yyyy" format
    public EventHelper(int image, String day, String month, String year, String title, String location, String state, String category, String type, String dateAddedString, String docId) {
        this.image = image;
        this.day = day;
        this.month = month;
        this.year = year;
        this.title = title;
        this.location = location;
        this.state = state;
        this.category = category;
        this.type = type;
        this.dateAdded = dateAddedString;
        this.docId = docId; // Initialize docId
    }

    // Convert date string ("dd-MM-yyyy") to timestamp (milliseconds)
    private long parseDateToTimestamp(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date date = sdf.parse(dateString);
            if (date != null) {
                return date.getTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Getter methods
    public int getImage() {
        return image;
    }

    public String getDay() {
        return day;
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getState() {
        return state;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public String getDocId() {
        return docId; // Getter for docId
    }

    // Parcelable implementation
    protected EventHelper(Parcel in) {
        image = in.readInt();
        day = in.readString();
        month = in.readString();
        year = in.readString();
        title = in.readString();
        location = in.readString();
        state = in.readString();
        category = in.readString();
        type = in.readString();
        dateAdded = in.readString();
        docId = in.readString(); // Read docId from the parcel
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(image);
        dest.writeString(day);
        dest.writeString(month);
        dest.writeString(year);
        dest.writeString(title);
        dest.writeString(location);
        dest.writeString(state);
        dest.writeString(category);
        dest.writeString(type);
        dest.writeString(dateAdded);
        dest.writeString(docId); // Write docId to the parcel
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EventHelper> CREATOR = new Creator<EventHelper>() {
        @Override
        public EventHelper createFromParcel(Parcel in) {
            return new EventHelper(in);
        }

        @Override
        public EventHelper[] newArray(int size) {
            return new EventHelper[size];
        }
    };
}