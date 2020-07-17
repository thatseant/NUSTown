package com.example.prototype1.view.dialogs;

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

import java.util.Objects;

public class ClubsSearchDialogFragment extends DialogFragment implements View.OnClickListener {

    static public final String TAG = "FilterDialog";
    public int resetFlag = 0; //resetFlag due to bug where Spinner setSelection does not save
    private View mRootView;
    private Spinner mCategorySpinner;

    private TitleFragmentViewModel mModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_clubs_search_dialog, container, false);

        mCategorySpinner = mRootView.findViewById(R.id.spinner_category);

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
        mModel.changeClubFilter(getFilters()); //Filters saved in ViewModel and getClubsData applies same filters until changed
        mModel.mClubSearchCat.setValue(getFilters().getClubSearchDescription(requireContext())); //Updates search box text (stored in ViewModel)
        dismiss();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        mModel.getClubsData();
    }

    private Filters getFilters() { //SearchDocuments in repository retrieves filters properties through filters.get() and applies to queries.
        Filters filters = new Filters(true);

        if (mRootView != null) {
            filters.setClubCategory(getSelectedCategory());
            filters.setClubCategoryText(getSelectedCategoryText());
        }

        return filters;
    }

    //The following get functions retrieves Spinner selections and are called in getFilters()
    @Nullable
    private int getSelectedCategory() {
        return mCategorySpinner.getSelectedItemPosition();
    }

    @Nullable
    private String getSelectedCategoryText() {
        String selected = (String) mCategorySpinner.getSelectedItem();
        if (getString(R.string.value_any_club_category).equals(selected)) {
            return "All Clubs";
        } else {
            return selected;
        }
    }


    private void resetFilters() {
        if (mRootView != null) {
            mCategorySpinner.setSelection(0, true);
        }

        mModel.changeClubFilter(new Filters(true));
        mModel.mClubSearchCat.setValue("<b> All Events <b>");
    }


}