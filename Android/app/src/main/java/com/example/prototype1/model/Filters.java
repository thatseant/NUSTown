package com.example.prototype1.model;

import android.content.Context;
import android.text.TextUtils;

import com.example.prototype1.R;
import com.google.firebase.firestore.Query;

public class Filters {

    private String category = null;
    private String place = null;
    private String sortBy;
    private int club_category = 0;
    private String club_category_text = null;
    private Boolean displayPast;
    private Query.Direction sortDirection;

    public Filters() {
        sortBy = "time";
        sortDirection = Query.Direction.ASCENDING;
        displayPast = false;
    }

    public Filters(Boolean noFilter) {//For creating empty filters for non-events
    }


    public boolean hasCategory() {
        return !(TextUtils.isEmpty(category));
    }

    public boolean hasPlace() {
        return !(TextUtils.isEmpty(place));
    }

    public boolean hasDisplayPast() {
        return (displayPast!=null);
    }

    public boolean hasClubCategory() {
        return !(TextUtils.isEmpty(Integer.toString(club_category)));
    }

    public boolean hasSortBy() {
        return !(TextUtils.isEmpty(sortBy));
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getClubCategory() {
        return club_category;
    }

    public void setClubCategory(int category) {
        this.club_category = category;
    }

    public String getClubCategoryText() {
        return club_category_text;
    }

    public void setClubCategoryText(String category) {
        this.club_category_text = category;
    }

    public String getPlace() {
        return place;
    }

    public void setDisplayPast(Boolean displayPast) {
        this.displayPast = displayPast;
    }

    public Boolean getDisplayPast() {
        return displayPast;
    }


    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Query.Direction getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(Query.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getSearchDescription(Context context) {
        StringBuilder desc = new StringBuilder();

        if (category == null) {
            desc.append("<b>");
            desc.append(context.getString(R.string.all_events));
            desc.append("</b>");
        }

        if (category != null) {
            desc.append("<b>");
            desc.append(category);
            desc.append("</b>");
        }


        return desc.toString();
    }

    public String getClubSearchDescription(Context context) {
        StringBuilder desc = new StringBuilder();

        if (club_category == 0) {
            desc.append("<b>");
            desc.append(context.getString(R.string.all_clubs));
            desc.append("</b>");
        }

        if (club_category != 0) {
            desc.append("<b>");
            desc.append(club_category_text);
            desc.append("</b>");
        }


        return desc.toString();
    }

    public String getOrderDescription(Context context) {
        if (sortBy.equals("name")) {
            return context.getString(R.string.sorted_by_name);
        } else if (sortBy.equals("lastUpdate")) {
            return "sorted by post date";
        } else if (sortBy.equals("followers")) {
            return "sorted by popularity";
        }
        else if (sortBy.equals("likes")) {
            return "sorted by popularity";
        }

        else {
            return context.getString(R.string.sorted_by_date);
        }
    }
}