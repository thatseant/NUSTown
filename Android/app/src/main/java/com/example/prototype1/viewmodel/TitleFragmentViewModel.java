package com.example.prototype1.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.prototype1.model.Filters;
import com.example.prototype1.model.NClub;
import com.example.prototype1.model.NEvent;
import com.example.prototype1.model.NMessage;
import com.example.prototype1.model.NUser;
import com.example.prototype1.repository.EventClubRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class TitleFragmentViewModel extends AndroidViewModel {
    public final MutableLiveData<String> mEventSearchCat = new MutableLiveData<>();
    public final MutableLiveData<String> mEventSearchSort = new MutableLiveData<>();
    public final MutableLiveData<String> mJioSearchCat = new MutableLiveData<>();
    public final MutableLiveData<String> mJioSearchSort = new MutableLiveData<>();
    public final MutableLiveData<String> mClubSearchCat = new MutableLiveData<>();
    public final MutableLiveData<String> mClubSearchSort = new MutableLiveData<>();
    private final EventClubRepository mRepository;
    private final MutableLiveData<ArrayList<NMessage>> mMessageLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<NEvent>> mEventLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<NClub>> mClubLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<NClub>> mGroupLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<NEvent>> mClubEventLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<NEvent>> mJioLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<NEvent>> mUserEventLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<NEvent>> mUserFeedLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<NEvent>> mUserGroupFeedLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<NEvent>> mUserJioLiveData = new MutableLiveData<>();
    private final FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
    //    private final MutableLiveData<ArrayList<NUser>> mAttendeesLiveData = new MutableLiveData<>();

    private final MutableLiveData<NUser> mUserLiveData = new MutableLiveData<>();
    private ListenerRegistration userListener = null;

    private final MutableLiveData<NEvent> mSingleEventLiveData = new MutableLiveData<>();
    private ListenerRegistration eventListener = null;

    private ListenerRegistration messageListener = null;


    private final MutableLiveData<NClub> mSingleClubLiveData = new MutableLiveData<>();
    private ListenerRegistration clubListener = null;

    private boolean mIsSigningIn;
    private Filters mEventFilters = new Filters();
    private Filters mJioFilters = new Filters();
    private Filters mClubFilters = new Filters(true);

    private DocumentSnapshot lastClubVisible = null;
    private boolean isClubsLastItemReached = false;

    private DocumentSnapshot lastEventVisible = null;
    private boolean isEventsLastItemReached = false;
    private final int limit = 15;


    public TitleFragmentViewModel(Application application) {
        super(application);
        mRepository = new EventClubRepository();

        //For Home Page
//        getUserEvents();
//        getUserJios();
    }

    //IsSigningFunctions used to check Sign-In status during Firebase Auth
    public boolean getIsSigningIn() {
        return mIsSigningIn;
    }

    public void setIsSigningIn(boolean mIsSigningIn) {
        this.mIsSigningIn = mIsSigningIn;
    }

    //changeFilter Functions called whenever a new query is performed; resets pagination variables
    public void changeEventFilter(Filters filters) {
        mEventLiveData.setValue(null);
        isEventsLastItemReached = false;
        lastEventVisible = null;
        mEventFilters = filters;
    }

    public void changeJioFilter(Filters filters) {//Called whenever a query is performed
        mJioFilters = filters;
    }

    public void changeClubFilter(Filters filters) {
        mClubLiveData.setValue(null);
        isClubsLastItemReached = false;
        lastClubVisible = null;
        mClubFilters = filters;
    }

    //Get Collections
    public MutableLiveData<ArrayList<NEvent>> getEventsData() {//Called when EventListFragment first launches and whenever a query is performed
        //For pagination of firestore results
        if (!isEventsLastItemReached) { //As long as last item in query not reached yet
            mRepository.searchDocuments(mEventFilters, "events", limit, lastEventVisible, resultList -> {
                if (resultList.size() != 0) {
                    if (resultList.size() < limit) {
                        isEventsLastItemReached = true; //Last item in query reached when final fetch smaller than limit
                    }
                    lastEventVisible = resultList.get(resultList.size() - 1); //Last item of previous fetch, to know which item to start from

                    ArrayList<NEvent> fullList = new ArrayList<>();
                    if (mEventLiveData.getValue() != null) {
                        fullList.addAll(mEventLiveData.getValue()); //Add items from previous fetch
                            }
                    //Add items from current fetch
                            fullList.addAll(resultList.stream().map(document -> document.toObject(NEvent.class)).collect(Collectors.toCollection(ArrayList::new)));
                            mEventLiveData.setValue(fullList);
                        }
                    }
            );
        }
        return mEventLiveData;
    }

    public MutableLiveData<ArrayList<NEvent>> getJiosData() {//Called when JioListFragment first launches and whenever a query is performed
        mRepository.searchDocuments(mJioFilters, "jios", 0, null, resultList ->
                mJioLiveData.setValue(resultList.stream().map(document -> document.toObject(NEvent.class)).collect(Collectors.toCollection(ArrayList::new))));
        return mJioLiveData;
    }

    public MutableLiveData<ArrayList<NClub>> getClubsData() {//Called when TitleFragment first launches and whenever a query is performed
        if (!isClubsLastItemReached) {
            mRepository.searchDocuments(mClubFilters, "clubs", limit, lastClubVisible, resultList -> {
                if (resultList.size() != 0) {
                    if (resultList.size() < limit) {
                        isClubsLastItemReached = true;
                    }
                    lastClubVisible = resultList.get(resultList.size() - 1);

                    ArrayList<NClub> fullList = new ArrayList<>();
                    if (mClubLiveData.getValue() != null) {
                        fullList.addAll(mClubLiveData.getValue());
                    }
                    fullList.addAll(resultList.stream().map(document -> document.toObject(NClub.class)).collect(Collectors.toCollection(ArrayList::new)));
                    mClubLiveData.setValue(fullList);
                }
                    }
            );
        }
        return mClubLiveData;
    }

    public MutableLiveData<ArrayList<NClub>> getGroups() {//Called when TitleFragment first launches and whenever a query is performed
        mRepository.searchDocuments(new Filters(true), "groups", 0, null, resultList ->
                mGroupLiveData.setValue(resultList.stream().map(document -> document.toObject(NClub.class)).collect(Collectors.toCollection(ArrayList::new))));
        return mGroupLiveData;
    }

    public MutableLiveData<ArrayList<NMessage>> getMessages(String eventID, String collection) {
        mMessageLiveData.setValue(new ArrayList<>());

        if (messageListener != null) {
            messageListener.remove();
        }

        Filters chronoFilter = new Filters(true);
        chronoFilter.setSortBy("timestamp");
        chronoFilter.setSortDirection(Query.Direction.ASCENDING);
        mRepository.searchDocumentsSnapshot(chronoFilter, collection + "/" + eventID + "/messages", 0, null, resultList ->
                mMessageLiveData.setValue(resultList.stream().map(document -> document.toObject(NMessage.class)).collect(Collectors.toCollection(ArrayList::new))), queryListener -> messageListener = queryListener);
        return mMessageLiveData;
    }


    //Edit Documents
    public void updateEvent(NEvent updatedEvent, String type) { //TODO: Rename to Doc instead of event
        mRepository.updateDoc(updatedEvent.getID(), updatedEvent, type);
    }

    public void deleteEvent(NEvent eventToDelete) {
        mRepository.deleteDoc(eventToDelete.getID(), "events");
    }

    public void addDoc(Object newEvent, String type) {
        mRepository.addDoc(newEvent, type);
    }


    public void setUser(String userID) {
        mRepository.getDoc(userID, "email", "users", document -> mUserLiveData.setValue(document.toObject(NUser.class)), docListener -> userListener = docListener);
    }


    public LiveData<NUser> getUser() {
        return mUserLiveData;
    }


    //Following methods are for document queries
    public LiveData<NEvent> getUpdatedEvent(String eventID, String type) {//For getting single document which is always updated as results is attached with SnapshotListener
        if (eventListener != null) {
            eventListener.remove();
        }
        mSingleEventLiveData.setValue(new NEvent());
        mRepository.getDoc(eventID, "id", type, document -> mSingleEventLiveData.setValue(document.toObject(NEvent.class)), docListener -> eventListener = docListener);
        return mSingleEventLiveData;
    }


    public LiveData<NClub> getClubFromEvent(NEvent mEvent) {
        if (clubListener != null) {
            clubListener.remove();
        }
        mRepository.getDoc(mEvent.getOrg(), "id", "clubs", document -> mSingleClubLiveData.setValue(document.toObject(NClub.class)), docListener -> clubListener = docListener);
        return mSingleClubLiveData;
    }

    public MutableLiveData<ArrayList<NEvent>> getClubEvents(NClub mClub, String clubType) {
        String eventType = "";
        if (clubType.equals("clubs")) {
            eventType = "events";
        } else if (clubType.equals("groups")) {
            eventType = "jios";
        }
        mRepository.multipleDocumentSearches(Collections.singletonList(mClub.getName()), "org", eventType,
                docs -> mClubEventLiveData.setValue(documentsToEvents(docs, true)));
        return mClubEventLiveData;
    }


    public LiveData<ArrayList<NEvent>> getUserEvents() {
        mUserLiveData.observeForever(user ->
                mRepository.multipleDocumentSearches(user.getEventAttending(), "id", "events", docs ->
                        mUserEventLiveData.setValue(documentsToEvents(docs, false))));
        return mUserEventLiveData;
    }

    public LiveData<ArrayList<NEvent>> getUserJios() {
        mUserLiveData.observeForever(user ->
                mRepository.multipleDocumentSearches(user.getJioEventAttending(), "id", "jios", docs ->
                        mUserJioLiveData.setValue(documentsToEvents(docs, false))));
        return mUserJioLiveData;
    }

    public LiveData<ArrayList<NEvent>> getUserFeed() {
        mUserLiveData.observeForever(user ->
                mRepository.multipleDocumentSearches(user.getClubsSubscribedTo(), "org", "events", docs ->
                        mUserFeedLiveData.setValue(documentsToEvents(docs, false))));
        return mUserFeedLiveData;
    }

    public LiveData<ArrayList<NEvent>> getUserGroupsFeed() {
        mUserLiveData.observeForever(user ->
                mRepository.multipleDocumentSearches(user.getGroupsSubscribedTo(), "org", "jios", docs ->
                        mUserGroupFeedLiveData.setValue(documentsToEvents(docs, false))));
        return mUserGroupFeedLiveData;
    }


    //Helper Methods
    public ArrayList<NEvent> documentsToEvents(ArrayList<DocumentSnapshot> documents, Boolean displayPast) { //Converting querySnapshot to events in chronological order
        ArrayList<NEvent> mResults = new ArrayList<>();
        for (DocumentSnapshot document : documents) {
            int newEventIndex = -1;
            NEvent newEvent = document.toObject(NEvent.class);
            if (newEvent != null) {
                if ((newEvent.getTime().compareTo(new Date()) < 0) && displayPast == false) {//newEvent is past event
                } else {
                    //Ensures Events are in Chronological Order
                    for (int i = 0; i < mResults.size(); i++) {
                        Date prevEventTime = mResults.get(i).getTime();
                        if (prevEventTime.compareTo(newEvent.getTime()) > 0) {//prevEvent is after newEvent
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

        }
        return mResults;
    }

    public void rsvpFunction(String eventID) {
        // Provides current user's email and event to cloud function when user RSVP
        Map<String, Object> data = new HashMap<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        data.put("display_name", user.getDisplayName());
        data.put("user_id", user.getUid());
        data.put("event_id", eventID);

        mFunctions
                .getHttpsCallable("rsvpFunction")
                .call(data)
                .continueWith(task -> null);
    }

    public void rsvpJioFunction(String eventID) {
        // Provides current user's email and event to cloud function when user RSVP
        Map<String, Object> data = new HashMap<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        data.put("display_name", user.getDisplayName());
        data.put("user_id", user.getUid());
        data.put("event_id", eventID);

        mFunctions
                .getHttpsCallable("rsvpJioFunction")
                .call(data)
                .continueWith(task -> null);
    }


    public void subscribeToClub(String clubName, String orgType) {
        //create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("email", mUserLiveData.getValue().getUid());
        data.put("club_name", clubName);
        data.put("orgType", orgType);

        mFunctions
                .getHttpsCallable("subscribeToClub")
                .call(data)
                .continueWith(task -> null);
    }

    public void uploadPic(String collection, String fileName, Uri file, MyPhotoCallback myPhotoCallback) {
        StorageReference imageRef;
        if (collection == "updates") { //Cos Insta stored as .png
            imageRef = FirebaseStorage.getInstance().getReference().child(collection + "/" + fileName + ".png");
        } else {
            imageRef = FirebaseStorage.getInstance().getReference().child(collection + "/" + fileName + ".jpg");
        }
        UploadTask uploadTask = imageRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
        }).addOnSuccessListener(taskSnapshot -> {
            myPhotoCallback.onCallback();
        });
    }

    //Callbacks
    public interface MyPhotoCallback {
        void onCallback();
    }

//    public LiveData<ArrayList<NUser>> getUsersAttending() {
//        mSingleEventLiveData.observeForever(mEvent ->
//                mRepository.getAttendees(mEvent, usersAttending -> mAttendeesLiveData.setValue(usersAttending))
//        );
//        return mAttendeesLiveData;
//    }

}
