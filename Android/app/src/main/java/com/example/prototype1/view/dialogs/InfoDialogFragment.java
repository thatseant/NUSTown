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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prototype1.R;
import com.example.prototype1.model.NEvent;
import com.example.prototype1.view.adapters.UsersAttendingAdapter;
import com.example.prototype1.view.mainFragments.HomeFragmentDirections;
import com.example.prototype1.view.mainFragments.JioListFragment;
import com.example.prototype1.view.mainFragments.JioListFragmentDirections;
import com.example.prototype1.viewmodel.TitleFragmentViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class InfoDialogFragment extends DialogFragment implements View.OnClickListener {

    static public final String TAG = "InfoDialog";

    final UsersAttendingAdapter mUserAdapter = new UsersAttendingAdapter();
    FirebaseUser user;
    private TitleFragmentViewModel mModel;
    Button rsvpButton;
    private NEvent mEvent;
    private View mRootView;
    ProgressBar progress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_info_dialog, container, false);
        mModel = new ViewModelProvider(requireActivity()).get(TitleFragmentViewModel.class);
        mEvent = getArguments().getParcelable("mEvent");

        //Sets text in TextView
        TextView jioName = mRootView.findViewById(R.id.jioDialogName);
        jioName.setText(mEvent.getName());
        TextView jioInfo = mRootView.findViewById(R.id.jioDialogInfo);
        jioInfo.setText(mEvent.getInfo());
        TextView jioTime = mRootView.findViewById(R.id.jioDialogTime);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
        jioTime.setText(dateFormat.format(mEvent.getTime()));

        TextView jioAttendees = mRootView.findViewById(R.id.jioDialogAttendees);
        mModel.getUpdatedEvent(mEvent.getID(), "jios").observe(getViewLifecycleOwner(), updatedEvent -> jioAttendees.setText(updatedEvent.getNumberAttending() + " Attending"));

        if (!mEvent.getPlace().equals("")) {
            TextView eventPlace = mRootView.findViewById(R.id.jioDialogPlace);
            eventPlace.setText(mEvent.getPlace());
            eventPlace.setVisibility(View.VISIBLE);
        }

        //RSVP Button text reflects whether event is part of current user's attending list.
        rsvpButton = mRootView.findViewById(R.id.button_jio_rsvp);
        mModel.getUser().observe(getViewLifecycleOwner(), mUser -> { //Attendance status is always updated as fetch from repository attaches SnapshotListener
            progress = mRootView.findViewById(R.id.progressBar_cyclic);
            progress.setVisibility(View.GONE);
            rsvpButton.setEnabled(true);
            if (mUser.getJioEventAttending().contains(mEvent.getID())) {
                rsvpButton.setText("Attending");
            } else {
                rsvpButton.setText("RSVP");
            }
        });

        //Displays profilePic and username of users attending
        RecyclerView recyclerView = mRootView.findViewById(R.id.recycler_jio_users_attending);
        recyclerView.setAdapter(mUserAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mModel.getUpdatedEvent(mEvent.getID(), "jios").observe(getViewLifecycleOwner(), event -> mUserAdapter.submitList(event.getUsersAttending()));

        //Uses Glide library for loading image from Firebase Cloud Storage
        ImageView mImage = mRootView.findViewById(R.id.jioImage);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(); //Get image reference from cloud storage
        StorageReference imageRef = storageReference.child("jios/" + mEvent.getName() + ".jpg");
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(requireContext()).load(uri).thumbnail(0.02f).into(mImage)).addOnFailureListener(url -> mImage.setVisibility(View.GONE));
        mImage.setAdjustViewBounds(true);

        //RSVP/Edit/Cancel onClickListeners
        mRootView.findViewById(R.id.button_jio_rsvp).setOnClickListener(this);
        mRootView.findViewById(R.id.button_cancel).setOnClickListener(this);
        mRootView.findViewById(R.id.chat_jio_button).setOnClickListener(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (Objects.equals(user.getEmail(), mEvent.getOrgUser())) { //Allows organisers to edit events
            mRootView.findViewById(R.id.edit_jio_button).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.edit_jio_button).setOnClickListener(this);
        }


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
        NavController navController = NavHostFragment.findNavController(this);
        switch (v.getId()) {
            case R.id.button_jio_rsvp:
                mModel.rsvpJioFunction(mEvent.getID());
                progress.setVisibility(View.VISIBLE);
                rsvpButton.setEnabled(false);
                break;
            case R.id.button_cancel:
                dismiss();
                break;
            case R.id.edit_jio_button:
                if (navController.getCurrentDestination().getId() == R.id.homeFragment) {
                    navController.navigate(HomeFragmentDirections.actionHomeFragmentToEditEvent(mEvent, "jios"));
                } else if (navController.getCurrentDestination().getId() == R.id.jioListFragment) {
                navController.navigate(JioListFragmentDirections.actionJioListFragmentToEditEvent(mEvent, "jios"));
                }
                dismiss();
                break;

                case R.id.chat_jio_button:

                if (navController.getCurrentDestination().getId() == R.id.homeFragment) {
                    navController.navigate(HomeFragmentDirections.actionHomeFragmentToChatFragment(mEvent.getID(), mEvent.getName(), "jios"));
                } else if (navController.getCurrentDestination().getId() == R.id.jioListFragment) {
                    navController.navigate(JioListFragmentDirections.actionJioListFragmentToChatFragment(mEvent.getID(), mEvent.getName(), "jios"));
                }
                dismiss();
                break;
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        mModel.getJiosData();
    }

}