package com.example.prototype1.repository;


import com.example.prototype1.model.Filters;
import com.example.prototype1.model.NClub;
import com.example.prototype1.model.NEvent;
import com.example.prototype1.model.NUser;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
            if (filters.getDisplayPast() == false) {
                query = query.whereEqualTo("isPastEvent", false);
            }
            query = query.orderBy(filters.getSortBy(), filters.getSortDirection());
        }

        //TODO: Add DisplayPast to query

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

    public void getEvent(String eventID, String type, final MyEventCallback myEventCallback) {
        FirebaseFirestore.getInstance().collection(type).document(eventID).get().addOnSuccessListener(document -> {
            NEvent updatedEvent = document.toObject(NEvent.class);
            myEventCallback.onCallback(updatedEvent);
        });
    }

    public void getClubFromEvent(NEvent mEvent, final MyClubCallback myClubCallback) {
        String clubName = mEvent.getOrg();
        FirebaseFirestore.getInstance().collection("clubs").document(clubName).get().addOnSuccessListener(document -> {
            NClub mClub = document.toObject(NClub.class);
            myClubCallback.onCallback(mClub);
        });
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

    public void getUserEvents(NUser mUser, final MyEventsCallback myEventsCallback) {
        ArrayList<NEvent> mResults = new ArrayList<>();
        List<Task<DocumentSnapshot>> tasks = new ArrayList<Task<DocumentSnapshot>>();
        List<String> eventIDs = mUser.getEventAttending();

        for (String eventID : eventIDs) {
            tasks.add(FirebaseFirestore.getInstance().collection("events").document(eventID).get());
        }

        Tasks.whenAllSuccess(tasks).addOnSuccessListener(documentList -> {
            for (Object eventDocument : documentList) {
                NEvent newEvent = ((DocumentSnapshot) eventDocument).toObject(NEvent.class);
                int newEventIndex = -1;
                for (int i = 0; i < mResults.size(); i++) {
                    Date prevEventTime = mResults.get(i).getTime();
                    if (prevEventTime.compareTo(newEvent.getTime()) > 0) {//newEvent is before prevEvent
                        newEventIndex = i;
                        break;
                    }
                }

                if (newEventIndex != -1) {
                    mResults.add(newEventIndex, newEvent);
                } else {
                    mResults.add(newEvent);
                }
            }
            myEventsCallback.onCallback(mResults);
        });
    }

    public void getUserJios(NUser mUser, final MyEventsCallback myEventsCallback) {
        ArrayList<NEvent> mResults = new ArrayList<>();
        List<Task<DocumentSnapshot>> tasks = new ArrayList<Task<DocumentSnapshot>>();
        List<String> eventIDs = mUser.getJioEventAttending();

        for (String eventID : eventIDs) {
            tasks.add(FirebaseFirestore.getInstance().collection("jios").document(eventID).get());
        }

        Tasks.whenAllSuccess(tasks).addOnSuccessListener(documentList -> {
            for (Object eventDocument : documentList) {
                DocumentSnapshot eventDocumentSnapshot = ((DocumentSnapshot) eventDocument);
                NEvent mEvent = eventDocumentSnapshot.toObject(NEvent.class);
                mEvent.setID(eventDocumentSnapshot.getId());
                mResults.add(mEvent);
            }
            myEventsCallback.onCallback(mResults);
        });
    }


    public void getUserFeed(NUser mUser, final MyEventsCallback myEventsCallback) {
        ArrayList<NEvent> mResults = new ArrayList<>();
        List<Task<QuerySnapshot>> tasks = new ArrayList<>();

        List<String> clubNames = mUser.getClubsSubscribedTo();

        for (String clubName : clubNames) {
            tasks.add(FirebaseFirestore.getInstance().collection("events").whereEqualTo("org", clubName).get());
        }

        Tasks.whenAllSuccess(tasks).addOnSuccessListener(queryList -> {
            for (Object query : queryList) {
                QuerySnapshot querySnapshot = ((QuerySnapshot) query);
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    int newEventIndex = -1;
                    NEvent newEvent = document.toObject(NEvent.class);
                    for (int i = 0; i < mResults.size(); i++) {
                        Date prevEventTime = mResults.get(i).getTime();
                        if (prevEventTime.compareTo(newEvent.getTime()) > 0) {//newEvent is before prevEvent
                            newEventIndex = i;
                            break;
                        }
                    }

                    if (newEventIndex != -1) {
                        mResults.add(newEventIndex, newEvent);
                    } else {
                        mResults.add(newEvent);
                    }
                }
            }
            myEventsCallback.onCallback(mResults);
        });
    }

    public void getAttendees(NEvent mEvent, final MyAttendeesCallback myAttendeesCallback) {
        ArrayList<NUser> mResults = new ArrayList<>();
        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();

        List<String> attendeesID = mEvent.getUsersAttending();

        for (String attendeeID : attendeesID) {
            tasks.add(FirebaseFirestore.getInstance().collection("users").document(attendeeID).get());
        }

        Tasks.whenAllSuccess(tasks).addOnSuccessListener(documentList -> {
            for (Object eventDocument : documentList) {
                NUser mUser = ((DocumentSnapshot) eventDocument).toObject(NUser.class);
                mResults.add(mUser);
            }
            myAttendeesCallback.onCallback(mResults);
        });
    }

    public void getUser(String userID, final MyUserCallback myUserCallback) {
        FirebaseFirestore.getInstance().collection("users").whereEqualTo("email", userID).get().addOnCompleteListener(task -> { //Performs query
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    NUser mUser = document.toObject(NUser.class);
                    myUserCallback.onCallback(mUser);
                }
            }
        });
    }

    public interface MyEventsCallback {
        void onCallback(ArrayList<NEvent> eventList);
    }

    public interface MyClubsCallback {
        void onCallback(ArrayList<NClub> clubList);
    }

    public interface MyAttendeesCallback {
        void onCallback(ArrayList<NUser> attendeesList);
    }

    public interface MyUserCallback {
        void onCallback(NUser mUser);
    }

    public interface MyEventCallback {
        void onCallback(NEvent mEvent);
    }

    public interface MyClubCallback {
        void onCallback(NClub mClub);
    }

}


