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
    NEvent mEvent;
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

        //Retrieve NEvent object clicked on in RecyclerView: supplied by Nav Safe Args
        assert getArguments() != null;
        mEvent = EventDetailFragmentArgs.fromBundle(getArguments()).getMEvent();
//        mModel.getUpdatedEvent(mEvent.getID(), "events");

        View rootView = inflater.inflate(R.layout.fragment_event_detail, container, false);

        //Uses Glide library for loading image from Firebase Cloud Storage
        mImage = rootView.findViewById(R.id.event_image);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(); //Get image reference from cloud storage
        StorageReference imageRef = storageReference.child("events/" + mEvent.getID() + ".png");
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(requireContext()).load(uri).thumbnail(0.02f).into(mImage);
        }).addOnFailureListener(url -> mImage.setImageResource(R.drawable.nus)); //TODO: Figure out how to load image without needing URL

        //Sets text in TextView
        TextView orgClub = rootView.findViewById(R.id.event_club);
        orgClub.setText(mEvent.getOrg());
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

        //RSVP Button text reflects if user attendance status by checking mEvent against his list of attending events
        rsvpButton = rootView.findViewById(R.id.rsvp_button);
        mModel.getUser().observe(getViewLifecycleOwner(), mUser -> { //Attendance status is always updated as fetch from repository attaches SnapshotListener
            if (mUser.getEventAttending().contains(mEvent.getID())) {
                rsvpButton.setText("Attending");
            } else {
                rsvpButton.setText("RSVP");
            }
        });

        //RSVP Button passes user email and event ID to cloud function
        rsvpButton.setOnClickListener(v ->
                rsvpFunction(user.getUid(), mEvent.getID())
        );

        //Club/Organiser name and image displayed; navigates to club when clicked
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


        //ViewPager2 Tabs for posts updates
        ViewPager2 updatesViewPager = rootView.findViewById(R.id.updatesViewPager);
        final UpdatesPagerAdapter mAdapter = new UpdatesPagerAdapter(this);
        ArrayList<Map.Entry<? extends String, ? extends ArrayList<String>>> //Map of postDateString as key and ArrayList of captions and imgURL as values
                allUpdates = new ArrayList<>(mEvent.getUpdates().entrySet());
        mAdapter.submitList(allUpdates);
        updatesViewPager.setAdapter(mAdapter);

        TabLayout tabLayout = rootView.findViewById(R.id.posts_tab_layout);
        new TabLayoutMediator(tabLayout, updatesViewPager, //Creates tabs for individual posts where title is post date (stored as key).
                (tab, position) -> tab.setText(allUpdates.get(position).getKey())
        ).attach();

        updatesViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() { //ensures tab dynamically resize to fit content
            @Override
            public void onPageSelected(int position) {
                mAdapter.notifyDataSetChanged();
            }
        });


        //Displays profilePic and username of users attending
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_users_attending);
        final UsersAttendingAdapter mUserAdapter = new UsersAttendingAdapter();
        recyclerView.setAdapter(mUserAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mModel.getUpdatedEvent(mEvent.getID(), "events").observe(getViewLifecycleOwner(), event -> //List always updated as repository's getDoc() is called.
                mUserAdapter.submitList(event.getUsersAttending()));


        //Allows organisers to delete events
        if (Objects.equals(user.getEmail(), "sean@tan.com")) {//TODO: Change Email to Event Organiser
            Button deleteButton = rootView.findViewById(R.id.delete_button);
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(v -> mModel.deleteEvent(mEvent));
        }


        //Allows organisers to edit events
        if (Objects.equals(user.getEmail(), "sean@tan.com")) {//TODO: Change Email to Event Organiser
            View editButton = rootView.findViewById(R.id.edit_event_button);
            editButton.setVisibility(View.VISIBLE);
            editButton.setOnClickListener(v -> { //Navigate to EditEventFragment
                NavController navController = Navigation.findNavController(rootView);
                eventType = EventDetailFragmentArgs.fromBundle(getArguments()).getType();
                navController.navigate(EventDetailFragmentDirections.actionEventDetailFragmentToEditEvent(mEvent, eventType));
            });
        }

        //Allows Organisers to create new posts
        Button createNewPost = rootView.findViewById(R.id.new_post_button);
        createNewPost.setVisibility(View.VISIBLE);//TODO: Visible only to organisers
        createNewPost.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(rootView);
            navController.navigate(EventDetailFragmentDirections.actionEventDetailFragmentToEditPostFragment("", mEvent, new ArrayList()));
        });

        //Close EventDetailFragment on buttonClose clicked
        ImageView buttonClose = rootView.findViewById(R.id.event_button_back);
        buttonClose.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(rootView);
            navController.popBackStack();
        });

        return rootView;


    }

    private Task<String> rsvpFunction(String email, String ID) {
        // Provides current user's email and event to cloud function when user RSVP
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("event_id", ID);

        return mFunctions
                .getHttpsCallable("rsvpFunction")
                .call(data)
                .continueWith(task -> null);
    }

    //Navigates to EditPostFragment when organisers click on edit button within post page.
    @Override
    public void onItemSelected(@NotNull Map.Entry<String, ? extends ArrayList<String>> mPost, @NotNull View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(EventDetailFragmentDirections.actionEventDetailFragmentToEditPostFragment(mPost.getKey(), mEvent, mPost.getValue()));
    }

    //Deletes post when organisers click on delete button within post page.
    @Override
    public void deleteItemSelected(@NotNull Map.Entry<String, ? extends ArrayList<String>> mPost, @NotNull View view) {
        Map<String, ArrayList<String>> existingUpdates = mEvent.getUpdates();
        existingUpdates.remove(mPost.getKey());
        mEvent.setUpdates(existingUpdates);
        mModel.updateEvent(mEvent, "events");
    }

}
