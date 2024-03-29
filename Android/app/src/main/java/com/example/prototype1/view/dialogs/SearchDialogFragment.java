package com.example.prototype1.view.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.prototype1.R;
import com.example.prototype1.model.Filters;
import com.example.prototype1.viewmodel.TitleFragmentViewModel;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class SearchDialogFragment extends DialogFragment implements View.OnClickListener {

    static public final String TAG = "FilterDialog";
    public int resetFlag = 0; //resetFlag due to bug where Spinner setSelection does not save
    private View mRootView;
    private Spinner mSortSpinner;
    private Switch mPastSwitch;
    String eventType;

    private TitleFragmentViewModel mModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_search_dialog, container, false);

        mSortSpinner = mRootView.findViewById(R.id.spinner_sort);
        mPastSwitch = mRootView.findViewById(R.id.showPastEventsSwitch);

        mRootView.findViewById(R.id.button_search).setOnClickListener(this);
        mRootView.findViewById(R.id.button_cancel).setOnClickListener(this);

        if (getArguments() != null) {
            eventType = getArguments().getString("eventType"); //Allows for SearchDialog to be used for both events and jios
        }

        mModel = new ViewModelProvider(requireActivity()).get(TitleFragmentViewModel.class); //returns same instance of ViewModel in TitleFragment
        return mRootView;
    }


    @Override
    public void onResume() {
        if (resetFlag == 1) {
            resetFilters();
            resetFlag = 0;
        }
        super.onResume();
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_search:
                onSearchClicked();
                break;
            case R.id.button_cancel:
                dismiss();
                break;
        }
    }

    private void onSearchClicked() {
        if (eventType.equals("jios")) {
            mModel.changeJioFilter(getFilters()); //Filters saved in ViewModel and getEventsData applies same filters until changed
            mModel.mJioSearchSort.setValue(getFilters().getOrderDescription(requireContext()));
        } else {
            mModel.changeEventFilter(getFilters());
            mModel.mEventSearchSort.setValue(getFilters().getOrderDescription(requireContext()));
        }
        dismiss();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (eventType.equals("jios")) {
            mModel.getJiosData();
        } else {
            mModel.getEventsData();
        }
    }

    private Filters getFilters() { //SearchDocuments in repository retrieves filters properties through filters.get() and applies to queries.
        Filters filters = new Filters();

        if (mRootView != null) {
//            filters.setPlace(getSelectedPlace());
            filters.setSortBy(getSelectedSortBy());
            filters.setSortDirection(getSortDirection());
            filters.setDisplayPast(getDisplayPast());
        }

        return filters;
    }


//    @Nullable
//    private String getSelectedPlace() {
//        String selected = (String) mPlaceSpinner.getSelectedItem();
//        if (getString(R.string.value_any_place).equals(selected)) {
//            return null;
//        } else {
//            return selected;
//        }
//    }

    @Nullable
    private String getSelectedSortBy() {
        String selected = (String) mSortSpinner.getSelectedItem();
        if (getString(R.string.sort_by_post_date).equals(selected)) {
            return "lastUpdate";
        }
        if (getString(R.string.sort_by_name).equals(selected)) {
            return "name";
        }
        if (getString(R.string.sort_by_popularity).equals(selected)) {
            return "likes";
        }
        if (getString(R.string.sort_by_date).equals(selected)) {
            return "time";
        }

        return null;
    }

    @Nullable
    private boolean getDisplayPast() {
        return mPastSwitch.isChecked();
    }

    @Nullable
    private Query.Direction getSortDirection() {
        String selected = (String) mSortSpinner.getSelectedItem();
        if (getString(R.string.sort_by_post_date).equals(selected)) {
            return Query.Direction.DESCENDING;
        }
        if (getString(R.string.sort_by_name).equals(selected)) {
            return Query.Direction.ASCENDING;
        }
        if (getString(R.string.sort_by_date).equals(selected)) {
            return Query.Direction.ASCENDING;
        }
        if (getString(R.string.sort_by_popularity).equals(selected)) {
            return Query.Direction.DESCENDING;
        }
        return null;
    }

    private void resetFilters() {
        if (mRootView != null) {
//            mPlaceSpinner.setSelection(0, true);
            mSortSpinner.setSelection(0, true);
            mPastSwitch.setChecked(false);
        }

        if (eventType.equals("jios")) {
            mModel.changeJioFilter(new Filters());
            mModel.mJioSearchSort.setValue("sorted by date");
        } else {
            mModel.changeEventFilter(new Filters());
            mModel.mEventSearchSort.setValue("sorted by date");
        }
    }


}
