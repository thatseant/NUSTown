package com.example.prototype1.view.mainFragments;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prototype1.R;
import com.example.prototype1.model.NEvent;
import com.example.prototype1.view.adapters.ClubEventsAdapter;
import com.example.prototype1.view.adapters.EventListAdapter;
import com.example.prototype1.view.adapters.JioGridAdapter;
import com.example.prototype1.view.dialogs.FollowingDialogFragment;
import com.example.prototype1.view.dialogs.InfoDialogFragment;
import com.example.prototype1.viewmodel.TitleFragmentViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;


public class HomeFragment extends Fragment implements ClubEventsAdapter.OnItemSelectedListener, EventListAdapter.OnItemSelectedListener, JioGridAdapter.OnItemSelectedListener {
    private InfoDialogFragment mInfoDialog;
    private FollowingDialogFragment mClubDialog, mGroupDialog;
    TitleFragmentViewModel mModel;
    private ImageView profileButton;
    StorageReference imageRef;
    View rootView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        Toolbar mToolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbar);


        mInfoDialog = new InfoDialogFragment();
        Bundle clubsBundle = new Bundle();
        clubsBundle.putString("orgType", "clubs");
        mClubDialog = new FollowingDialogFragment();
        mClubDialog.setArguments(clubsBundle);

        mGroupDialog = new FollowingDialogFragment();
        Bundle infoBundle = new Bundle();
        infoBundle.putString("orgType", "groups");
        mGroupDialog.setArguments(infoBundle);

        //Events ViewModel
        mModel = new ViewModelProvider(requireActivity()).get(TitleFragmentViewModel.class);

        //Link Events Recycler View to Adapter
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_rsvp_events);
        final ClubEventsAdapter mAdapter = new ClubEventsAdapter(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mModel.getUserEvents().observe(getViewLifecycleOwner(), mAdapter::submitList);

        //Link Jio Recycler View to Adapter
        RecyclerView jioRecyclerView = rootView.findViewById(R.id.recycler_casual_jios);
        final JioGridAdapter mJioAdapter = new JioGridAdapter(this);
        jioRecyclerView.setAdapter(mJioAdapter);
        jioRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mModel.getUserJios().observe(getViewLifecycleOwner(), mJioAdapter::submitList);

        //Link Groups Feed Recycler View to Adapter
        RecyclerView feedGroupsRecycler = rootView.findViewById(R.id.recycler_group_feed);
        final ClubEventsAdapter mGroupFeedAdapter = new ClubEventsAdapter(this);
        feedGroupsRecycler.setAdapter(mGroupFeedAdapter);
        feedGroupsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mModel.getUserGroupsFeed().observe(getViewLifecycleOwner(), mGroupFeedAdapter::submitList);

        FloatingActionButton groupsFollowingButton = rootView.findViewById(R.id.groups_following_button);
        groupsFollowingButton.setOnClickListener(v -> mGroupDialog.show(requireActivity().getSupportFragmentManager(), FollowingDialogFragment.TAG));

        //Link Events Feed Recycler View to Adapter
        RecyclerView feedRecyclerView = rootView.findViewById(R.id.recycler_events_feed);
        final EventListAdapter mFeedAdapter = new EventListAdapter(this);
        feedRecyclerView.setAdapter(mFeedAdapter);
        feedRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mModel.getUserFeed().observe(getViewLifecycleOwner(), mFeedAdapter::submitList);

        FloatingActionButton followingButton = rootView.findViewById(R.id.clubs_following_button);
        followingButton.setOnClickListener(v -> mClubDialog.show(requireActivity().getSupportFragmentManager(), FollowingDialogFragment.TAG));

        //Temporary Button for setting Profile Picture TODO: Change to ImageView
        profileButton = rootView.findViewById(R.id.setProfile);
        profileButton.setOnClickListener(v -> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(); //Get image reference from cloud storage
        mModel.getUser().observe(getViewLifecycleOwner(), v -> {        imageRef = storageReference.child("profile/" + user.getUid() + ".jpg");
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(requireContext()).load(uri).thumbnail(0.02f).into(profileButton));});
        return rootView;
    }

    //For Profile Picture
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK) {
            Uri file = data.getData();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            mModel.uploadPic("profile", user.getUid(), file, () -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(requireContext()).load(uri).thumbnail(0.02f).into(profileButton))); //Upload to Cloud Storage
        }
    }

    //Navigate to EventDetail when event clicked
    @Override
    public void onItemSelected(@NotNull NEvent mEvent, @NotNull View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(HomeFragmentDirections.actionHomeFragmentToEventDetailFragment(mEvent, "events"));
    }

    //Display Jio Dialog when Jio selected
    @Override
    public void onJioSelected(@NotNull NEvent mEvent, @NotNull View view) {
        Bundle infoBundle = new Bundle();
        infoBundle.putParcelable("mEvent", mEvent);
        mInfoDialog.setArguments(infoBundle);
        mInfoDialog.show(requireActivity().getSupportFragmentManager(), InfoDialogFragment.TAG);
    }

}