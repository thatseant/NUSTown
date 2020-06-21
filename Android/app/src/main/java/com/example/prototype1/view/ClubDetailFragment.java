package com.example.prototype1.view;

import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.prototype1.R;
import com.example.prototype1.model.NClub;
import com.google.firebase.functions.FirebaseFunctions;

public class ClubDetailFragment extends Fragment {
    private ImageView mImage;
    private FirebaseFunctions mFunctions;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        mFunctions = FirebaseFunctions.getInstance();
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Retrieve NClub object clicked on in RecyclerView
        assert getArguments() != null;
        NClub mClub = ClubDetailFragmentArgs.fromBundle(getArguments()).getMClub();

        View rootView = inflater.inflate(R.layout.fragment_club_detail, container, false);


        //Get image reference from cloud storage
        mImage = rootView.findViewById(R.id.club_image);
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//        StorageReference imageRef = storageReference.child("clubs/" + mEvent.getImage());
//
//        imageRef.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(requireContext()).load(uri).into(mImage)); //TODO: Figure out how to load image without needing URL
        if (mClub.getImgUrl() != "") {
            Glide.with(requireContext()).load(mClub.getImgUrl()).apply(new RequestOptions()
                    .placeholder(R.drawable.nus)).thumbnail(0.02f).into(mImage);
        } else {
            mImage.setImageResource(R.drawable.nus);
        }


        //Sets text in TextView
        TextView mCat = rootView.findViewById(R.id.club_category);
        mCat.setText(mClub.getCatName());
        TextView mName = rootView.findViewById(R.id.club_name);
        mName.setText(mClub.getName());
        TextView mInfo = rootView.findViewById(R.id.club_info_text);
        mInfo.setText(mClub.getInfo());
        TextView mURL = rootView.findViewById(R.id.club_url_text);
        mURL.setText(mClub.getUrl());
        Linkify.addLinks(mURL, Linkify.WEB_URLS); //Allows link in mURL EditText to be clickable

        //Close EventDetailFragment on buttonClose clicked
        ImageView buttonClose = rootView.findViewById(R.id.club_button_back);
        buttonClose.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(rootView);
            navController.navigate(ClubDetailFragmentDirections.actionClubDetailFragmentToClubFragment());
        });


        return rootView;


    }
}
