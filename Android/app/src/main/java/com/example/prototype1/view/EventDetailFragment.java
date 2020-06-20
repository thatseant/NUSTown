package com.example.prototype1.view;

import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.prototype1.R;
import com.example.prototype1.model.NEvent;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EventDetailFragment extends Fragment {
    private ImageView mImage;
    private FirebaseFunctions mFunctions;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFunctions = FirebaseFunctions.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Retrieve NEvent object clicked on in RecyclerView
        assert getArguments() != null;
        NEvent mEvent = EventDetailFragmentArgs.fromBundle(getArguments()).getMEvent();

        View rootView = inflater.inflate(R.layout.fragment_event_detail, container, false);


        //Get image reference from cloud storage
        mImage = rootView.findViewById(R.id.event_image);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageReference.child("events/" + mEvent.getImage());

        imageRef.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(requireContext()).load(uri).into(mImage)); //TODO: Figure out how to load image without needing URL

        //Sets text in TextView
        TextView mCat = rootView.findViewById(R.id.event_category);
        mCat.setText(mEvent.getCategory());
        TextView mPlace = rootView.findViewById(R.id.event_city);
        mPlace.setText(mEvent.getPlace());
        TextView mName = rootView.findViewById(R.id.event_name);
        mName.setText(mEvent.getName());
        TextView mNum = rootView.findViewById(R.id.event_number_attend);
        mNum.setText("Number Attending: " + mEvent.getNumberAttending());
        TextView mInfo = rootView.findViewById(R.id.event_info_text);
        mInfo.setText(mEvent.getInfo());
        TextView mURL = rootView.findViewById(R.id.event_url_text);
        mURL.setText(mEvent.getUrl());
        Linkify.addLinks(mURL, Linkify.WEB_URLS); //Allows link in mURL EditText to be clickable

        //Close EventDetailFragment on buttonClose clicked
        ImageView buttonClose = rootView.findViewById(R.id.event_button_back);
        buttonClose.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(rootView);
            navController.navigate(EventDetailFragmentDirections.actionEventDetailFragmentToTitleFragment());
        });

        //RSVP Button invokes cloud function
        Button rsvpButton = rootView.findViewById(R.id.rsvp_button);
        rsvpButton.setOnClickListener(v -> {
            rsvpFunction(user.getEmail(), mEvent.getID());
        });


        //Allows organisers to edit events
        assert user != null;
        if (Objects.equals(user.getEmail(), "sean@tan.com")) {
            View editButton = rootView.findViewById(R.id.edit_event_button);
            editButton.setVisibility(View.VISIBLE);
            //Displays dialog for organisers to edit event
            editButton.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(rootView);
                navController.navigate(EventDetailFragmentDirections.actionEventDetailFragmentToEditEvent(mEvent));
            });
        }


        return rootView;


    }

    private Task<String> rsvpFunction(String email, String ID) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("event_id", ID);

        return mFunctions
                .getHttpsCallable("rsvpFunction")
                .call(data)
                .continueWith(task -> null);
    }
}
