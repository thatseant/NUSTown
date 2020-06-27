package com.example.prototype1.view.mainFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.prototype1.R;
import com.example.prototype1.model.NEvent;
import com.example.prototype1.view.adapters.ClubEventsAdapter;
import com.example.prototype1.view.adapters.EventListAdapter;
import com.example.prototype1.viewmodel.TitleFragmentViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;


public class HomeFragment extends Fragment implements ClubEventsAdapter.OnItemSelectedListener, EventListAdapter.OnItemSelectedListener {
    private TitleFragmentViewModel mModel; //Events ViewModel

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

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        mModel = new ViewModelProvider(requireActivity()).get(TitleFragmentViewModel.class);
        mModel.getUser().observe(getViewLifecycleOwner(), user -> {
//            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile/" + user.getProfilePic());
//            storageReference.getDownloadUrl().addOnSuccessListener(url ->
//                Glide.with(getContext()).load(url).into(profilePic));

        });

        //Link Events Recycler View to Adapter
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_rsvp_events);
        final ClubEventsAdapter mAdapter = new ClubEventsAdapter(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mModel.getUserEvents().observe(getViewLifecycleOwner(), mAdapter::submitList);

        //Link Events Recycler View to Adapter
        RecyclerView feedRecyclerView = rootView.findViewById(R.id.recycler_events_feed);
        final EventListAdapter mFeedAdapter = new EventListAdapter(this);
        feedRecyclerView.setAdapter(mFeedAdapter);
        feedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mModel.getUserFeed().observe(getViewLifecycleOwner(), mAdapter::submitList);

        return rootView;
    }

    @Override
    public void onItemSelected(@NotNull NEvent mEvent, @NotNull View view) {
//        NavController navController = Navigation.findNavController(view);
//        navController.navigate(ClubDetailFragmentDirections.actionClubDetailFragmentToEventDetailFragment(mEvent, "events"));
    }

}