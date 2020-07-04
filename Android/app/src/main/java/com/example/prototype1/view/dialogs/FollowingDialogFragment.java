package com.example.prototype1.view.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private View mRootView;

    private TitleFragmentViewModel mModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_following_dialog, container, false);


        mModel = new ViewModelProvider(requireActivity()).get(TitleFragmentViewModel.class); //returns same instance of ViewModel in TitleFragment

        //Link Events Recycler View to Adapter
        RecyclerView recyclerView = mRootView.findViewById(R.id.recycler_followed_clubs);
        final UserClubsAdapter mAdapter = new UserClubsAdapter(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mModel.getUser().observe(getViewLifecycleOwner(), user -> mAdapter.submitList(user.getClubsSubscribedTo()));

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
        mModel.subscribeToClub(clubName);
    }
}