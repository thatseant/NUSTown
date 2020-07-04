package com.example.prototype1.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.prototype1.model.Filters;
import com.example.prototype1.model.NClub;
import com.example.prototype1.model.NEvent;
import com.example.prototype1.model.NUser;
import com.example.prototype1.repository.EventClubRepository;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;


public class TitleFragmentViewModel extends AndroidViewModel {
    public final MutableLiveData<String> mEventSearchCat = new MutableLiveData<>();
    public final MutableLiveData<String> mEventSearchSort = new MutableLiveData<>();
    public final MutableLiveData<String> mJioSearchCat = new MutableLiveData<>();
    public final MutableLiveData<String> mJioSearchSort = new MutableLiveData<>();
    private final EventClubRepository mRepository;
    private final MutableLiveData<ArrayList<NEvent>> mEventLiveData = new MutableLiveData<>(); //TODO: change name to mEventLiveData
    private final MutableLiveData<ArrayList<NClub>> mClubLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<NEvent>> mClubEventLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<NEvent>> mJioLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<NEvent>> mUserEventLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<NEvent>> mUserFeedLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<NEvent>> mUserJioLiveData = new MutableLiveData<>();
    //    private final MutableLiveData<ArrayList<NUser>> mAttendeesLiveData = new MutableLiveData<>();

    private final MutableLiveData<NUser> mUserLiveData = new MutableLiveData<>();
    private ListenerRegistration userListener = null;

    private final MutableLiveData<NEvent> mSingleEventLiveData = new MutableLiveData<>();
    private ListenerRegistration eventListener = null;


    private final MutableLiveData<NClub> mSingleClubLiveData = new MutableLiveData<>();
    private ListenerRegistration clubListener = null;

    private boolean mIsSigningIn;
    private Filters mEventFilters = new Filters();
    private Filters mJioFilters = new Filters();

    private DocumentSnapshot lastClubVisible = null;
    private boolean isClubsLastItemReached = false;

    private DocumentSnapshot lastEventVisible = null;
    private boolean isEventsLastItemReached = false;
    private int limit = 15;


    public TitleFragmentViewModel(Application application, SavedStateHandle savedStateHandle) {
        super(application);
        mRepository = new EventClubRepository();

        getUserEvents();
        getUserJios();

//        mRepository.searchDocuments(new Filters(), "events", 0, null, resultList -> //Get All Events
//                mEventLiveData.setValue(resultList.stream().map(document -> document.toObject(NEvent.class)).collect(Collectors.toCollection(ArrayList::new)))
//        );
//        mRepository.searchDocuments(new Filters(), "clubs",3, null, resultList -> //Get All Clubs
//                mClubLiveData.setValue(resultList.stream().map(document -> document.toObject(NClub.class)).collect(Collectors.toCollection(ArrayList::new)))
//        );
        mRepository.searchDocuments(new Filters(), "jios", 0, null, resultList -> //Get All Jios
                mJioLiveData.setValue(resultList.stream().map(document -> document.toObject(NEvent.class)).collect(Collectors.toCollection(ArrayList::new)))
        );
//        mState = savedStateHandle; //Planned to be used to save scroll position, still resolving
    }

    //IsSigningFunctions used to check Sign-In status during Firebase Auth
    public boolean getIsSigningIn() {
        return mIsSigningIn;
    }

    public void setIsSigningIn(boolean mIsSigningIn) {
        this.mIsSigningIn = mIsSigningIn;
    }

    public void changeEventFilter(Filters filters) {//Called whenever a query is performed
        mEventFilters = filters;
    }

    public void changeJioFilter(Filters filters) {//Called whenever a query is performed
        mJioFilters = filters;
    }


    //Get Collections
    public MutableLiveData<ArrayList<NEvent>> getEventsData() {//Called when EventListFragment first launches and whenever a query is performed
        if (!isEventsLastItemReached) {
            mRepository.searchDocuments(mEventFilters, "events2", limit, lastEventVisible, resultList -> {
                if (resultList.size() < limit) {
                    isEventsLastItemReached = true;
                }
                lastEventVisible = resultList.get(resultList.size() - 1);

                ArrayList<NEvent> fullList = new ArrayList<>();
                if (mEventLiveData.getValue() != null) {
                    fullList.addAll(mEventLiveData.getValue());
                }
                fullList.addAll(resultList.stream().map(document -> document.toObject(NEvent.class)).collect(Collectors.toCollection(ArrayList::new)));
                        mEventLiveData.setValue(fullList);
                    }
            );
        }
//        mRepository.searchDocuments(mEventFilters, "events", 0, null, resultList ->
//                mEventLiveData.setValue(resultList.stream().map(document -> document.toObject(NEvent.class)).collect(Collectors.toCollection(ArrayList::new))));
        return mEventLiveData;
    }

    public void clearEventLiveData() {
        mEventLiveData.setValue(null);
        isEventsLastItemReached = false;
        lastEventVisible = null;
    }

    public MutableLiveData<ArrayList<NEvent>> getJiosData() {//Called when JioListFragment first launches and whenever a query is performed
        mRepository.searchDocuments(mJioFilters, "jios", 0, null, resultList ->
                mJioLiveData.setValue(resultList.stream().map(document -> document.toObject(NEvent.class)).collect(Collectors.toCollection(ArrayList::new))));
        return mJioLiveData;
    }

    public MutableLiveData<ArrayList<NClub>> getClubsData() {//Called when TitleFragment first launches and whenever a query is performed
        if (!isClubsLastItemReached) {
            mRepository.searchDocuments(new Filters(true), "clubs", limit, lastClubVisible, resultList -> {
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
            );
        }
        return mClubLiveData;
    }


    //Edit Documents
    public void updateEvent(NEvent updatedEvent, String type) {
        mRepository.updateDoc(updatedEvent.getID(), updatedEvent, type);
    }

    public void deleteEvent(NEvent eventToDelete) {
        mRepository.deleteDoc(eventToDelete.getID(), "events");
    }

    public void addEvent(NEvent newEvent, String type) {
        mRepository.addDoc(newEvent, type);
    }


    public void setUser(String userID) {
        mRepository.getDoc(userID, "email", "users", document -> mUserLiveData.setValue(document.toObject(NUser.class)), docListener -> userListener = docListener);
    }


    public LiveData<NUser> getUser() {
        return mUserLiveData;
    }


    public LiveData<NEvent> getUpdatedEvent(String eventID, String type) {
        if (eventListener != null) {
            eventListener.remove();
        }
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

    public MutableLiveData<ArrayList<NEvent>> getClubEvents(NClub mClub) {
        mRepository.multipleDocumentSearches(Collections.singletonList(mClub.getName()), "org", "events2", docs -> //TODO: Change collection to events
                mClubEventLiveData.setValue(documentsToEvents(docs)));
        return mClubEventLiveData;
    }


    public LiveData<ArrayList<NEvent>> getUserEvents() {
        mUserLiveData.observeForever(user ->
                mRepository.multipleDocumentSearches(user.getEventAttending(), "id", "events2", docs ->
                        mUserEventLiveData.setValue(documentsToEvents(docs))));
        return mUserEventLiveData;
    }

    public LiveData<ArrayList<NEvent>> getUserJios() {
        mUserLiveData.observeForever(user ->
                mRepository.multipleDocumentSearches(user.getJioEventAttending(), "id", "jios", docs ->
                        mUserJioLiveData.setValue(documentsToEvents(docs))));
        return mUserJioLiveData;
    }

    public LiveData<ArrayList<NEvent>> getUserFeed() {
        mUserLiveData.observeForever(user ->
                mRepository.multipleDocumentSearches(user.getClubsSubscribedTo(), "org", "events2", docs ->
                        mUserFeedLiveData.setValue(documentsToEvents(docs))));
        return mUserFeedLiveData;
    }

    public ArrayList<NEvent> documentsToEvents(ArrayList<DocumentSnapshot> documents) {
        ArrayList<NEvent> mResults = new ArrayList<>();
        for (DocumentSnapshot document : documents) {
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
        return mResults;
    }

//    public LiveData<ArrayList<NUser>> getUsersAttending() {
//        mSingleEventLiveData.observeForever(mEvent ->
//                mRepository.getAttendees(mEvent, usersAttending -> mAttendeesLiveData.setValue(usersAttending))
//        );
//        return mAttendeesLiveData;
//    }

}
