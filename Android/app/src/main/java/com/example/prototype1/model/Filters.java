package com.example.prototype1.model;

import android.content.Context;
import android.text.TextUtils;

import com.example.prototype1.R;
import com.google.firebase.firestore.Query;

/**
 * Object for passing filters around.
 */
public class Filters {

    private String category = null;
    private String place = null;
    private String sortBy;
    private Query.Direction sortDirection;

    public Filters() {
        sortBy = "rating";
        sortDirection = Query.Direction.DESCENDING;
    }

    public boolean hasCategory() {
        return !(TextUtils.isEmpty(category));
    }

    public boolean hasPlace() {
        return !(TextUtils.isEmpty(place));
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

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
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

        if (category == null && place == null) {
            desc.append("<b>");
            desc.append(context.getString(R.string.all_events));
            desc.append("</b>");
        }

        if (category != null) {
            desc.append("<b>");
            desc.append(category);
            desc.append("</b>");
        }

        if (category != null && place != null) {
            desc.append(" in ");
        }

        if (place != null) {
            desc.append("<b>");
            desc.append(place);
            desc.append("</b>");
        }


        return desc.toString();
    }

    public String getOrderDescription(Context context) {
        if (sortBy.equals("name")) {
            return context.getString(R.string.sorted_by_name);
        } else {
            return context.getString(R.string.sorted_by_rating);
        }
    }
}