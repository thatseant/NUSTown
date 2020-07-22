package com.example.prototype1.view.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype1.R;
import com.example.prototype1.model.NEvent;
import com.example.prototype1.view.adapters.UsersAttendingAdapter;
import com.example.prototype1.view.mainFragments.JioListFragmentDirections;
import com.example.prototype1.viewmodel.TitleFragmentViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        switch (v.getId()) {
            case R.id.button_jio_rsvp:
                mModel.rsvpJioFunction(mEvent.getID());
                break;
            case R.id.button_cancel:
                dismiss();
                break;
            case R.id.edit_jio_button:
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(JioListFragmentDirections.actionJioListFragmentToEditEvent(mEvent, "jios"));
                dismiss();
                break;
            case R.id.chat_jio_button:
                navController = NavHostFragment.findNavController(this);
                navController.navigate(JioListFragmentDirections.actionJioListFragmentToChatFragment(mEvent.getID(), mEvent.getName(), "jios"));
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