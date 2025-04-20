package com.example.townhall;

import java.util.Date;

public class GovernmentInitiatives {

    public String initiativeID,Name,category,Description,Status ,StartDate,EndDate;

    public int imageResId;
    public boolean isOngoing;

    public GovernmentInitiatives(String category, String description, String endDate, String initiativeID, String name, String startDate, String status, int imageResId, boolean isOngoing) {
        this.category = category;
        Description = description;
        EndDate = endDate;
        this.initiativeID = initiativeID;
        Name = name;
        StartDate = startDate;
        Status = status;
        this.imageResId = imageResId;
        this.isOngoing = isOngoing;

    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return Description;
    }



    public String getInitiativeID() {
        return initiativeID;
    }

    public String getName() {
        return Name;
    }



    public String getStatus() {
        return Status;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        Description = description;
    }



    public void setInitiativeID(String initiativeID) {
        this.initiativeID = initiativeID;
    }

    public void setName(String name) {
        Name = name;
    }



    public void setStatus(String status) {
        Status = status;
    }

    @Override
    public String toString() {
        return "Governmentiniatives{" +
                "category='" + category + '\'' +
                ", initiativeID='" + initiativeID + '\'' +
                ", Name='" + Name + '\'' +
                ", Description='" + Description + '\'' +
                ", Status='" + Status + '\'' +
                ", StartDate=" + StartDate +
                ", EndDate=" + EndDate +
                '}';
    }


    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }
    public boolean isOngoing() {
        return isOngoing;
    }

    public void setOngoing(boolean ongoing) {
        isOngoing = ongoing;
    }
}