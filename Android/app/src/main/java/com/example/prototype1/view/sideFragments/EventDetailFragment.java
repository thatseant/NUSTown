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
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.prototype1.R;
import com.example.prototype1.model.NEvent;
import com.example.prototype1.view.adapters.UpdatesPagerAdapter;
import com.example.prototype1.view.adapters.UsersAttendingAdapter;
import com.example.prototype1.viewmodel.TitleFragmentViewModel;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EventDetailFragment extends Fragment implements UpdatesPagerAdapter.OnItemSelectedListener {
    private ImageView mImage;
    private FirebaseFunctions mFunctions;
    private TitleFragmentViewModel mModel;
    private String eventType;
    Button rsvpButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mModel = new ViewModelProvider(requireActivity()).get(TitleFragmentViewModel.class);
        mFunctions = FirebaseFunctions.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Retrieve NEvent object clicked on in RecyclerView
        assert getArguments() != null;
        NEvent mEvent = EventDetailFragmentArgs.fromBundle(getArguments()).getMEvent();
        mModel.getUpdatedEvent(mEvent.getID(), "events");

        View rootView = inflater.inflate(R.layout.fragment_event_detail, container, false);

        rsvpButton = rootView.findViewById(R.id.rsvp_button);
        mModel.getUser().observe(getViewLifecycleOwner(), mUser -> {
            if (mUser.getEventAttending().contains(mEvent.getID())) {
                rsvpButton.setText("Attending");
            } else {
                rsvpButton.setText("RSVP");
            }


        });

        //Info and Navigate to event's organiser
        mModel.getClubFromEvent(mEvent).observe(getViewLifecycleOwner(), mClub -> {
            if (mClub != null) {
                TextView clubTitle = rootView.findViewById(R.id.clubTitle);
                clubTitle.setText(mClub.getName());
                ImageView clubImage = rootView.findViewById(R.id.clubImage);
                if (!mClub.getImgUrl().equals("")) {
                    Glide.with(requireContext()).load(mClub.getImgUrl()).apply(new RequestOptions()
                            .placeholder(R.drawable.nus)
                    ).thumbnail(0.02f).into(clubImage);
                } else {
                    clubImage.setImageResource(R.drawable.nus);
                }
                View clubCard = rootView.findViewById(R.id.clubCard);
                clubCard.setOnClickListener(v -> {
                    NavController navController = Navigation.findNavController(rootView);
                    navController.navigate(EventDetailFragmentDirections.actionEventDetailFragmentToClubDetailFragment(mClub));
                });
            }
        });
        eventType = EventDetailFragmentArgs.fromBundle(getArguments()).getType();


        //ViewPager Tabs for posts updates
        ViewPager2 updatesViewPager = rootView.findViewById(R.id.updatesViewPager);
        if (mEvent.getUpdates() != null) {
            ArrayList<Map.Entry<? extends String, ? extends String>> allUpdates = new ArrayList<>(mEvent.getUpdates().entrySet());
            final UpdatesPagerAdapter mAdapter = new UpdatesPagerAdapter(this);
            mAdapter.submitList(allUpdates);
            updatesViewPager.setAdapter(mAdapter);
            TabLayout tabLayout = rootView.findViewById(R.id.posts_tab_layout);
            new TabLayoutMediator(tabLayout, updatesViewPager,
                    (tab, position) -> tab.setText(allUpdates.get(position).getKey())
            ).attach();
        }


        //Link Users Recycler View to Adapter
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_users_attending);
        final UsersAttendingAdapter mUserAdapter = new UsersAttendingAdapter();
        recyclerView.setAdapter(mUserAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mUserAdapter.submitList(mEvent.getUsersAttending());


        //Get image reference from cloud storage
        mImage = rootView.findViewById(R.id.event_image);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageReference.child("events/" + mEvent.getID() + ".png");

        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(requireContext()).load(uri).thumbnail(0.02f).into(mImage);
        }).addOnFailureListener(url -> mImage.setImageResource(R.drawable.nus)); //TODO: Figure out how to load image without needing URL

//        if (mEvent.getImgUrl() != "") {
//            Glide.with(requireContext()).load(mEvent.getImgUrl()).apply(new RequestOptions()
//                    .placeholder(R.drawable.nus)
//            ).thumbnail(0.02f).into(mImage);
//        } else {
//            mImage.setImageResource(R.drawable.nus);
//        }

        //Sets text in TextView
        TextView mClub = rootView.findViewById(R.id.event_club);
        mClub.setText(mEvent.getOrg());
        TextView mTime = rootView.findViewById(R.id.event_time);
        mTime.setText(mEvent.getTime().toString());
        TextView mPlace = rootView.findViewById(R.id.event_city);
        mPlace.setText(mEvent.getPlace());
        TextView mName = rootView.findViewById(R.id.event_name);
        mName.setText(mEvent.getName());
        TextView mNum = rootView.findViewById(R.id.event_number_attend);
        mNum.setText("NUSync Signups: " + mEvent.getNumberAttending());
        TextView mURL = rootView.findViewById(R.id.event_url_text);
        mURL.setText(mEvent.getUrl());
        Linkify.addLinks(mURL, Linkify.WEB_URLS); //Allows link in mURL EditText to be clickable

        //Close EventDetailFragment on buttonClose clicked
        ImageView buttonClose = rootView.findViewById(R.id.event_button_back);
        buttonClose.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(rootView);
            navController.popBackStack();
        });

        //RSVP Button invokes cloud function --- this
        rsvpButton.setOnClickListener(v -> {
            rsvpFunction(user.getUid(), mEvent.getID()).addOnSuccessListener(result -> {
                mModel.setUser(user.getEmail());
                if (getView() != null) {
                    mModel.getUpdatedEvent(mEvent.getID(), "events").observe(getViewLifecycleOwner(), event -> mUserAdapter.submitList(event.getUsersAttending()));
                }
            });
        });

        //Delete Button deletes event
        if (Objects.equals(user.getEmail(), "sean@tan.com")) {
        Button deleteButton = rootView.findViewById(R.id.delete_button);
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(v -> mModel.deleteEvent(mEvent));
        }


        //Allows organisers to edit events
        assert user != null;
        if (Objects.equals(user.getEmail(), "sean@tan.com")) {
            View editButton = rootView.findViewById(R.id.edit_event_button);
            editButton.setVisibility(View.VISIBLE);
            //Displays dialog for organisers to edit event
            editButton.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(rootView);
                navController.navigate(EventDetailFragmentDirections.actionEventDetailFragmentToEditEvent(mEvent, eventType));
            });
        }


        return rootView;


    }

    private Task<String> rsvpFunction(String email, String ID) { //----this
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("event_id", ID);

        return mFunctions
                .getHttpsCallable("rsvpFunction")
                .call(data)
                .continueWith(task -> null);
    }

    @Override
    public void onItemSelected(@NotNull Map.Entry<String, String> mClub, @NotNull View view) {
//        NavController navController = Navigation.findNavController(view);
//        navController.navigate(ClubFragmentDirections.actionClubFragmentToClubDetailFragment(mClub));
    }

}
