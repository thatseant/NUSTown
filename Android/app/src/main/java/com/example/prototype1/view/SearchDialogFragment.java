package com.example.prototype1.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.prototype1.R;
import com.example.prototype1.model.Filters;
import com.example.prototype1.viewmodel.TitleFragmentViewModel;
import com.google.firebase.firestore.Query;

import java.util.Objects;

/**
 * Dialog Fragment containing filter form.
 */
public class SearchDialogFragment extends DialogFragment implements View.OnClickListener {

    static final String TAG = "FilterDialog";
    int resetFlag = 0; //resetFlag due to bug where Spinner setSelection does not save
    private View mRootView;
    private Spinner mCategorySpinner;
    private Spinner mPlaceSpinner;
    private Spinner mSortSpinner;

    private TitleFragmentViewModel mModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_search_dialog, container, false);

        mCategorySpinner = mRootView.findViewById(R.id.spinner_category);
        mPlaceSpinner = mRootView.findViewById(R.id.spinner_place);
        mSortSpinner = mRootView.findViewById(R.id.spinner_sort);

        mRootView.findViewById(R.id.button_search).setOnClickListener(this);
        mRootView.findViewById(R.id.button_cancel).setOnClickListener(this);

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

        mModel.changeFilter(getFilters());
        mModel.mSearchCat.setValue(getFilters().getSearchDescription(requireContext())); //Updates search box text (stored in ViewModel)
        mModel.mSearchSort.setValue(getFilters().getOrderDescription(requireContext()));
        dismiss();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        mModel.getEventsData();
    }

    private Filters getFilters() { //Used to update EventList in ViewModel
        Filters filters = new Filters();

        if (mRootView != null) {
            filters.setCategory(getSelectedCategory());
            filters.setPlace(getSelectedPlace());
            filters.setSortBy(getSelectedSortBy());
            filters.setSortDirection(getSortDirection());
        }

        return filters;
    }

    //get functions retrieves Spinner selections and called in getFilters()
    @Nullable
    private String getSelectedCategory() {
        String selected = (String) mCategorySpinner.getSelectedItem();
        if (getString(R.string.value_any_category).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }

    @Nullable
    private String getSelectedPlace() {
        String selected = (String) mPlaceSpinner.getSelectedItem();
        if (getString(R.string.value_any_place).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }

    @Nullable
    private String getSelectedSortBy() {
        String selected = (String) mSortSpinner.getSelectedItem();
        if (getString(R.string.sort_by_rating).equals(selected)) {
            return "rating";
        }
        if (getString(R.string.sort_by_name).equals(selected)) {
            return "name";
        }

        return null;
    }

    @Nullable
    private Query.Direction getSortDirection() {
        String selected = (String) mSortSpinner.getSelectedItem();
        if (getString(R.string.sort_by_rating).equals(selected)) {
            return Query.Direction.DESCENDING;
        }
        if (getString(R.string.sort_by_name).equals(selected)) {
            return Query.Direction.ASCENDING;
        }
        return null;
    }

    private void resetFilters() {
        if (mRootView != null) {
            mCategorySpinner.setSelection(0, true);
            mPlaceSpinner.setSelection(0, true);
            mSortSpinner.setSelection(0, true);
        }
        mModel.changeFilter(new Filters());
        mModel.mSearchCat.setValue("<b> All Events <b>");
        mModel.mSearchSort.setValue("sorted by Rating");
    }


}
