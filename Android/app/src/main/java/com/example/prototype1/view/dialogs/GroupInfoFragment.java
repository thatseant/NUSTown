package com.example.prototype1.view.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prototype1.R;
import com.example.prototype1.model.NClub;
import com.example.prototype1.model.NEvent;
import com.example.prototype1.view.adapters.ClubEventsAdapter;
import com.example.prototype1.view.adapters.EventListAdapter;
import com.example.prototype1.view.adapters.UsersAttendingAdapter;
import com.example.prototype1.view.mainFragments.JioListFragmentDirections;
import com.example.prototype1.viewmodel.TitleFragmentViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class GroupInfoFragment extends DialogFragment implements View.OnClickListener, ClubEventsAdapter.OnItemSelectedListener {

    static public final String TAG = "InfoDialog";

    final UsersAttendingAdapter mUserAdapter = new UsersAttendingAdapter();
    FirebaseUser user;
    private TitleFragmentViewModel mModel;
    Button followButton;
    private NClub mGroup;
    private View mRootView;
    ProgressBar progress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_group_info, container, false);
        mModel = new ViewModelProvider(requireActivity()).get(TitleFragmentViewModel.class);
        mGroup = getArguments().getParcelable("mGroup");

        //Sets text in TextView
        TextView jioName = mRootView.findViewById(R.id.jioDialogName);
        jioName.setText(mGroup.getName());
        TextView jioInfo = mRootView.findViewById(R.id.jioDialogInfo);
        jioInfo.setText(mGroup.getInfo());

        //RSVP Button text reflects whether event is part of current user's attending list.
        followButton = mRootView.findViewById(R.id.button_group_follow);
        mModel.getUser().observe(getViewLifecycleOwner(), mUser -> { //Attendance status is always updated as fetch from repository attaches SnapshotListener
            progress = mRootView.findViewById(R.id.progressBar_cyclic);
            progress.setVisibility(View.GONE);
            followButton.setEnabled(true);
            if (mUser.getGroupsSubscribedTo().contains(mGroup.getName())) {//TODO: Change to groupsSubscribeTo?
                followButton.setText("FOLLOWING");
            } else {
                followButton.setText("FOLLOW");
            }
        });

        //Uses Glide library for loading image from Firebase Cloud Storage
        ImageView mImage = mRootView.findViewById(R.id.groupImage);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(); //Get image reference from cloud storage
        StorageReference imageRef = storageReference.child("groups/" + mGroup.getName() + ".jpg");
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(requireContext()).load(uri).thumbnail(0.02f).into(mImage)).addOnFailureListener(url -> mImage.setVisibility(View.GONE)); //TODO: Figure out how to load image without needing URL
        mImage.setAdjustViewBounds(true);

        //Link Groups Events Recycler View to Adapter
        RecyclerView feedRecyclerView = mRootView.findViewById(R.id.recycler_group_events);
        final ClubEventsAdapter mFeedAdapter = new ClubEventsAdapter(this);
        feedRecyclerView.setAdapter(mFeedAdapter);
        feedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mModel.getClubEvents(mGroup, "groups").observe(getViewLifecycleOwner(), mFeedAdapter::submitList);


        //RSVP/Edit/Cancel onClickListeners
        followButton.setOnClickListener(this);
        mRootView.findViewById(R.id.button_cancel).setOnClickListener(this);
        mRootView.findViewById(R.id.chat_jio_button).setOnClickListener(this);
        mRootView.findViewById(R.id.add_jio_button).setOnClickListener(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
//        if (Objects.equals(user.getEmail(), mEvent.getOrgUser())) { //Allows organisers to edit events
//            mRootView.findViewById(R.id.edit_jio_button).setVisibility(View.VISIBLE);
//            mRootView.findViewById(R.id.edit_jio_button).setOnClickListener(this);
//        }

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_group_follow:
                mModel.subscribeToClub(mGroup.getName(), "groups");
                progress.setVisibility(View.VISIBLE);
                followButton.setEnabled(false);
                break;
            case R.id.button_cancel:
                dismiss();
                break;
//            case R.id.edit_jio_button:
//                NavController navController = NavHostFragment.findNavController(this);
//                navController.navigate(JioListFragmentDirections.actionJioListFragmentToEditEvent(mEvent, "jios"));
//                dismiss();
//                break;
            case R.id.add_jio_button:
                AddEventDialogFragment mAddDialog = new AddEventDialogFragment();
                Bundle infoBundle = new Bundle();
                infoBundle.putString("group_name", mGroup.getName());
                mAddDialog.setArguments(infoBundle);
                mAddDialog.show(requireActivity().getSupportFragmentManager(), AddEventDialogFragment.TAG);
                break;
            case R.id.chat_jio_button:
                dismiss();
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(JioListFragmentDirections.actionJioListFragmentToChatFragment(mGroup.getID(), mGroup.getName(), "groups"));
                dismiss();
                break;
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onItemSelected(@NotNull NEvent mEvent, @NotNull View view) {
    }

    @Override
    public void onJioSelected(@NotNull NEvent mEvent, @NotNull View view) {
        dismiss();
        InfoDialogFragment mInfoDialog = new InfoDialogFragment(); //For displaying InfoDialog about jio when clicked
        Bundle infoBundle = new Bundle();
        infoBundle.putParcelable("mEvent", mEvent);
        mInfoDialog.setArguments(infoBundle);
        mInfoDialog.show(requireActivity().getSupportFragmentManager(), InfoDialogFragment.TAG);
    }
}