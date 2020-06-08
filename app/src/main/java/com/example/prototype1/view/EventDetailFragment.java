package com.example.prototype1.view;

import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EventDetailFragment extends Fragment {
    private ImageView mImage;
    private View editButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Retrieve NEvent object clicked on in RecyclerView
        NEvent mEvent = EventDetailFragmentArgs.fromBundle(getArguments()).getMEvent();

        View rootView = inflater.inflate(R.layout.fragment_event_detail, container, false);


        //Get image reference from cloud storage
        mImage = rootView.findViewById(R.id.restaurant_image);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageReference.child("events/" + mEvent.getImage());

        imageRef.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(getContext()).load(uri).into(mImage)); //TODO: Figure out how to load image without needing URL

        //Sets text in TextView
        TextView mCat = rootView.findViewById(R.id.restaurant_category);
        mCat.setText(mEvent.getCategory());
        TextView mPlace = rootView.findViewById(R.id.restaurant_city);
        mPlace.setText(mEvent.getPlace());
        TextView mName = rootView.findViewById(R.id.restaurant_name);
        mName.setText(mEvent.getName());

        //Close EventDetailFragment on buttonClose clicked
        ImageView buttonClose = rootView.findViewById(R.id.restaurant_button_back);
        buttonClose.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(rootView);
            navController.navigate(EventDetailFragmentDirections.actionEventDetailFragmentToTitleFragment());
        });

        //Allows organisers to edit events
        if (user.getEmail().equals("sean@tan.com")) {
            editButton = rootView.findViewById(R.id.edit_event_button);
            editButton.setVisibility(View.VISIBLE);
            //Displays dialog for organisers to edit event
            editButton.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(rootView);
                navController.navigate(EventDetailFragmentDirections.actionEventDetailFragmentToEditEvent(mEvent));
            });
        }




        return rootView;


    }
}
