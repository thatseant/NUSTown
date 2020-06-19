package com.example.prototype1.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.prototype1.model.Filters;
import com.example.prototype1.model.NClub;
import com.example.prototype1.model.NEvent;
import com.example.prototype1.repository.EventRepository;

import java.util.ArrayList;


public class TitleFragmentViewModel extends AndroidViewModel {
    public final MutableLiveData<String> mSearchCat = new MutableLiveData<>();
    public final MutableLiveData<String> mSearchSort = new MutableLiveData<>();
    private final EventRepository mRepository;
    private final MutableLiveData<ArrayList<NEvent>> mLiveData = new MutableLiveData<>(); //TODO: change name to mEventLiveData
    private final MutableLiveData<ArrayList<NClub>> mClubLiveData = new MutableLiveData<>();
    private boolean mIsSigningIn;
    private Filters mFilters = new Filters();


    public TitleFragmentViewModel(Application application, SavedStateHandle savedStateHandle) {
        super(application);
        mRepository = new EventRepository();
        mRepository.getAll(mLiveData::setValue); //TODO: change repository function name to getAllEvents
        mRepository.getAllClubs(mClubLiveData::setValue);
//        mState = savedStateHandle; //Planned to be used to save scroll position, still resolving
    }

    //IsSigningFunctions used to check Sign-In status during Firebase Auth
    public boolean getIsSigningIn() {
        return mIsSigningIn;
    }

    public void setIsSigningIn(boolean mIsSigningIn) {
        this.mIsSigningIn = mIsSigningIn;
    }

    public void changeFilter(Filters filters) {//Called whenever a query is performed
        mFilters = filters;
    }

    public MutableLiveData<ArrayList<NEvent>> getData() {//Called when TitleFragment first launches and whenever a query is performed
        mRepository.search(mLiveData::setValue, mFilters); //First parameter is a callback; mLiveData value is set AFTER asynchronous completion of Firebase Query
        return mLiveData;
    }

    public MutableLiveData<ArrayList<NClub>> getClubsData() {//Called when TitleFragment first launches and whenever a query is performed
        return mClubLiveData;
    }

    public void updateEvent(NEvent updatedEvent) {
        mRepository.updateEvent(updatedEvent);
    }

}
