package com.example.prototype1.repository;


import com.example.prototype1.model.Filters;
import com.example.prototype1.model.NClub;
import com.example.prototype1.model.NEvent;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;


public class EventClubRepository {

    public EventClubRepository() {
    }

    public void searchEvents(final MyEventsCallback myEventsCallback, Filters filters, String category) {
        ArrayList<NEvent> mResults = new ArrayList<>();

        Query query = FirebaseFirestore.getInstance().collection(category);

        if (filters.hasCategory()) {
            query = query.whereEqualTo("category", filters.getCategory());
        }
        if (filters.hasPlace()) {
            query = query.whereEqualTo("place", filters.getPlace());
        }

        if (filters.hasSortBy()) {
            query = query.orderBy(filters.getSortBy(), filters.getSortDirection());
        }

        query.get().addOnCompleteListener(task -> { //Performs query
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    NEvent newEvent = document.toObject(NEvent.class); //Converts document to NEvent object
                    newEvent.setID(document.getId());
                    mResults.add(newEvent); //Adds to list of NEvent objects that matches query
                }
                myEventsCallback.onCallback(mResults); //Callback required as Firebase query performed asynchronously; code after onCompleteListener will execute before it finishes
            }
        });
    }


    public void searchClubs(final MyClubsCallback myClubsCallback) {
        ArrayList<NClub> mResults = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("clubs").get().addOnCompleteListener(task -> { //Performs query
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    NClub newClub = document.toObject(NClub.class); //Converts document to NClub object
                    mResults.add(newClub); //Adds to list of NClub objects that matches query
                }
                myClubsCallback.onCallback(mResults); //Callback required as Firebase query performed asynchronously; code after onCompleteListener will execute before it finishes
            }
        });
    }

    public void getAllEvents(final MyEventsCallback myEventsCallback) { //Returns all documents in Events collection
        searchEvents(myEventsCallback, new Filters(), "events");
    }

    public void getAllJios(final MyEventsCallback myEventsCallback) { //Returns all documents in Events collection
        searchEvents(myEventsCallback, new Filters(), "jios");
    }

    public void getAllClubs(final MyClubsCallback myClubsCallback) { //Returns all documents in Events collection
        searchClubs(myClubsCallback);
    }

    public void updateEvent(NEvent updatedEvent, String type) {
        FirebaseFirestore.getInstance().collection(type).document(updatedEvent.getID()).set(updatedEvent);
    }

    public void deleteEvent(NEvent eventToDelete) {
        FirebaseFirestore.getInstance().collection("events").document(eventToDelete.getID()).delete();
    }

    public void addEvent(NEvent newEvent, String type) {
        FirebaseFirestore.getInstance().collection(type).add(newEvent);
    }

    public void getClubEvents(NClub mClub, final MyEventsCallback myEventsCallback) {
        ArrayList<NEvent> mResults = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("events").whereEqualTo("org", mClub.getName()).get().addOnCompleteListener(task -> { //Performs query
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    NEvent newEvent = document.toObject(NEvent.class); //Converts document to NClub object
                    mResults.add(newEvent); //Adds to list of NClub objects that matches query
                }
                myEventsCallback.onCallback(mResults); //Callback required as Firebase query performed asynchronously; code after onCompleteListener will execute before it finishes
            }
        });
    }

    public interface MyEventsCallback {
        void onCallback(ArrayList<NEvent> eventList);
    }

    public interface MyClubsCallback {
        void onCallback(ArrayList<NClub> clubList);
    }

}


