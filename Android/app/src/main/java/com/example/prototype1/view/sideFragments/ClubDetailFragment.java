package com.example.prototype1.view.sideFragments;

import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.prototype1.R;
import com.example.prototype1.model.NClub;
import com.example.prototype1.model.NEvent;
import com.example.prototype1.view.adapters.ClubEventsAdapter;
import com.example.prototype1.viewmodel.TitleFragmentViewModel;

import org.jetbrains.annotations.NotNull;

public class ClubDetailFragment extends Fragment implements ClubEventsAdapter.OnItemSelectedListener{
    private TitleFragmentViewModel mModel; //Events ViewModel


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mModel = new ViewModelProvider(requireActivity()).get(TitleFragmentViewModel.class);

        // Retrieve NClub object clicked on in RecyclerView
        assert getArguments() != null;
        NClub mClub = ClubDetailFragmentArgs.fromBundle(getArguments()).getMClub();

        View rootView = inflater.inflate(R.layout.fragment_club_detail, container, false);


        //Get image reference from cloud storage TODO: Store Image in Cloud Storage
        ImageView mImage = rootView.findViewById(R.id.club_image);
        if (!mClub.getImgUrl().equals("")) {
            Glide.with(requireContext()).load(mClub.getImgUrl()).apply(new RequestOptions()
                    .placeholder(R.drawable.nus)).thumbnail(0.02f).into(mImage);
        } else {
            mImage.setImageResource(R.drawable.nus);
        }


        //TextViews displays clubs' info
        TextView mCat = rootView.findViewById(R.id.club_category);
        mCat.setText(mClub.getCatName());
        TextView mName = rootView.findViewById(R.id.club_name);
        mName.setText(mClub.getName());
        TextView mInfo = rootView.findViewById(R.id.club_info_text);
        mInfo.setText(mClub.getInfo());
        TextView mURL = rootView.findViewById(R.id.club_url_text);
        mURL.setText(mClub.getUrl());
        Linkify.addLinks(mURL, Linkify.WEB_URLS); //Allows link in mURL EditText to be clickable

        //Events by Club displayed in recyclerView
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_club_events);
        final ClubEventsAdapter mAdapter = new ClubEventsAdapter(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mModel.getClubEvents(mClub).observe(getViewLifecycleOwner(), mAdapter::submitList);

        //Subscribe button reflects subscription status
        Button subscribeButton = rootView.findViewById(R.id.subscribe);
        mModel.getUser().observe(getViewLifecycleOwner(), mUser -> { //Subscription status is always updated as fetch from repository attaches SnapshotListener
            if (mUser.getClubsSubscribedTo().contains(mClub.getName())) {
                subscribeButton.setText("I'm Following");
            } else {
                subscribeButton.setText("Follow Club");
            }
        });

        subscribeButton.setOnClickListener(v -> mModel.subscribeToClub(mClub.getName()));

        //Close EventDetailFragment on buttonClose clicked
        ImageView buttonClose = rootView.findViewById(R.id.club_button_back);
        buttonClose.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(rootView);
            navController.popBackStack();
        });

        return rootView;


    }

    //Navigate to event when clicked
    @Override
    public void onItemSelected(@NotNull NEvent mEvent, @NotNull View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(ClubDetailFragmentDirections.actionClubDetailFragmentToEventDetailFragment(mEvent, "events"));
    }

}

