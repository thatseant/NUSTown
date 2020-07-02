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

import java.util.ArrayList;
import java.util.Arrays;
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
    private final MutableLiveData<NEvent> mSingleEventLiveData = new MutableLiveData<>();
    private final MutableLiveData<NClub> mSingleClubLiveData = new MutableLiveData<>();
    private boolean mIsSigningIn;
    private Filters mEventFilters = new Filters();
    private Filters mJioFilters = new Filters();


    public TitleFragmentViewModel(Application application, SavedStateHandle savedStateHandle) {
        super(application);
        mRepository = new EventClubRepository();


        mRepository.searchDocuments(new Filters(), "events", resultList -> //Get All Events
                mEventLiveData.setValue(resultList.stream().map(document -> document.toObject(NEvent.class)).collect(Collectors.toCollection(ArrayList::new)))
        );
        mRepository.searchDocuments(new Filters(), "clubs", resultList -> //Get All Clubs
                mClubLiveData.setValue(resultList.stream().map(document -> document.toObject(NClub.class)).collect(Collectors.toCollection(ArrayList::new)))
        );
        mRepository.searchDocuments(new Filters(), "jios", resultList -> //Get All Jios
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
        mRepository.searchDocuments(mEventFilters, "events", resultList ->
                mEventLiveData.setValue(resultList.stream().map(document -> document.toObject(NEvent.class)).collect(Collectors.toCollection(ArrayList::new))));
        return mEventLiveData;
    }

    public MutableLiveData<ArrayList<NEvent>> getJiosData() {//Called when JioListFragment first launches and whenever a query is performed
        mRepository.searchDocuments(mJioFilters, "jios", resultList ->
                mJioLiveData.setValue(resultList.stream().map(document -> document.toObject(NEvent.class)).collect(Collectors.toCollection(ArrayList::new))));
        return mJioLiveData;
    }

    public MutableLiveData<ArrayList<NClub>> getClubsData() {//Called when TitleFragment first launches and whenever a query is performed
        mRepository.searchDocuments(new Filters(true), "clubs", resultList ->
                mClubLiveData.setValue(resultList.stream().map(document -> document.toObject(NClub.class)).collect(Collectors.toCollection(ArrayList::new)))
        );
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
        mRepository.getUser(userID, user -> mUserLiveData.setValue(user));
    }


    public LiveData<NUser> getUser() {
        return mUserLiveData;
    }


    public LiveData<NEvent> getUpdatedEvent(String eventID, String type) {
        mRepository.getDocument(eventID, type, document -> mSingleEventLiveData.setValue(document.toObject(NEvent.class)));
        return mSingleEventLiveData;
    }


    public LiveData<NClub> getClubFromEvent(NEvent mEvent) {
        mRepository.getDocument(mEvent.getOrg(), "clubs", document -> mSingleClubLiveData.setValue(document.toObject(NClub.class)));
        return mSingleClubLiveData;
    }

    public MutableLiveData<ArrayList<NEvent>> getClubEvents(NClub mClub) {
        mRepository.multipleDocumentSearches(Arrays.asList(mClub.getName()), "org", "events", docs ->
                mClubEventLiveData.setValue(documentsToEvents(docs)));
        return mClubEventLiveData;
    }


    public LiveData<ArrayList<NEvent>> getUserEvents() {
        mUserLiveData.observeForever(user ->
                mRepository.multipleDocumentSearches(user.getEventAttending(), "id", "events", docs ->
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
                mRepository.multipleDocumentSearches(user.getClubsSubscribedTo(), "org", "events", docs ->
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
