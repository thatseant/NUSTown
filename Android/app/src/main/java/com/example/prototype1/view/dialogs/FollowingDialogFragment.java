package com.example.prototype1.view.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype1.R;
import com.example.prototype1.view.adapters.UserClubsAdapter;
import com.example.prototype1.viewmodel.TitleFragmentViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Dialog Fragment containing filter form.
 */
public class FollowingDialogFragment extends DialogFragment implements UserClubsAdapter.ClickListener {

    static public final String TAG = "AddDialog";

    String orgType;

    private View mRootView;

    private TitleFragmentViewModel mModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        orgType = getArguments().getString("orgType");
        mRootView = inflater.inflate(R.layout.fragment_following_dialog, container, false);
        mModel = new ViewModelProvider(requireActivity()).get(TitleFragmentViewModel.class); //returns same instance of ViewModel in TitleFragment

        //Provides list of clubs followed with unfollow button
        RecyclerView clubFollowedRecycler = mRootView.findViewById(R.id.recycler_followed_clubs);
        final UserClubsAdapter mAdapter = new UserClubsAdapter(this);
        clubFollowedRecycler.setAdapter(mAdapter);
        clubFollowedRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        if (orgType == "clubs") {
            mModel.getUser().observe(getViewLifecycleOwner(), user -> {
                mAdapter.submitList(user.getClubsSubscribedTo());
                TextView emptyHolder = mRootView.findViewById(R.id.no_clubs_placeholder);
                if (user.getClubsSubscribedTo().size()==0) {
                    emptyHolder.setVisibility(View.VISIBLE);
                } else {
                    emptyHolder.setVisibility(View.GONE);
                }
            });
        } else {
            TextView orgTypeTitle = mRootView.findViewById(R.id.org_type_title);
            orgTypeTitle.setText("Groups Following");
            mModel.getUser().observe(getViewLifecycleOwner(), user -> {
                mAdapter.submitList(user.getGroupsSubscribedTo());
                TextView emptyHolder = mRootView.findViewById(R.id.no_groups_placeholder);
                if (user.getGroupsSubscribedTo().size()==0) {
                    emptyHolder.setVisibility(View.VISIBLE);
                } else {
                    emptyHolder.setVisibility(View.GONE);
                }
            });
        }

        return mRootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    @Override
    public void onUnfollow(@NotNull String clubName) {
        mModel.subscribeToClub(clubName, orgType);
    }
}