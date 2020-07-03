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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype1.R;
import com.example.prototype1.model.NEvent;
import com.example.prototype1.view.adapters.UsersAttendingAdapter;
import com.example.prototype1.viewmodel.TitleFragmentViewModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Dialog Fragment containing filter form.
 */
public class InfoDialogFragment extends DialogFragment implements View.OnClickListener {

    static public final String TAG = "InfoDialog";

    final UsersAttendingAdapter mUserAdapter = new UsersAttendingAdapter();
    private FirebaseFunctions mFunctions;
    FirebaseUser user;
    private TitleFragmentViewModel mModel;
    Button rsvpButton;
    private NEvent mEvent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_info_dialog, container, false);
        mModel = new ViewModelProvider(requireActivity()).get(TitleFragmentViewModel.class);
        mEvent = getArguments().getParcelable("mEvent");
        rsvpButton = mRootView.findViewById(R.id.button_jio_rsvp);
        mModel.getUser().observe(getViewLifecycleOwner(), mUser -> {
            if (mUser.getJioEventAttending().contains(mEvent.getID())) {
                rsvpButton.setText("Attending");
            } else {
                rsvpButton.setText("RSVP");
            }


        });

        TextView jioAttendees = mRootView.findViewById(R.id.jioDialogAttendees);
        mModel.getUpdatedEvent(mEvent.getID(), "jios").observe(getViewLifecycleOwner(), updatedEvent -> jioAttendees.setText(updatedEvent.getNumberAttending() + " Attending"));
        user = FirebaseAuth.getInstance().getCurrentUser();
        mFunctions = FirebaseFunctions.getInstance();
        TextView jioName = mRootView.findViewById(R.id.jioDialogName);
        jioName.setText(mEvent.getName());
        TextView jioInfo = mRootView.findViewById(R.id.jioDialogInfo);
        jioInfo.setText(mEvent.getInfo());
        TextView jioTime = mRootView.findViewById(R.id.jioDialogTime);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
        jioTime.setText(dateFormat.format(mEvent.getTime()));


        mRootView.findViewById(R.id.button_jio_rsvp).setOnClickListener(this);
        mRootView.findViewById(R.id.button_cancel).setOnClickListener(this);

        //Link Users Recycler View to Adapter
        RecyclerView recyclerView = mRootView.findViewById(R.id.recycler_jio_users_attending);
        recyclerView.setAdapter(mUserAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//        mUserAdapter.submitList(mEvent.getUsersAttending());
        mModel.getUpdatedEvent(mEvent.getID(), "events").observe(getViewLifecycleOwner(), event -> mUserAdapter.submitList(event.getUsersAttending()));
        return mRootView;
    }

    private Task<String> rsvpJioFunction(String email, String ID) { //----this
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("event_id", ID);

        return mFunctions
                .getHttpsCallable("rsvpJioFunction")
                .call(data)
                .continueWith(task -> null);
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
                onRSVPClicked();
                break;
            case R.id.button_cancel:
                dismiss();
                break;
        }
    }

    private void onRSVPClicked() {
        rsvpJioFunction(user.getUid(), mEvent.getID()).addOnSuccessListener(result -> {
//            mModel.setUser(user.getEmail());
//            if (getView() != null) {
//                mModel.getUpdatedEvent(mEvent.getID(), "jios").observe(getViewLifecycleOwner(), jio -> mUserAdapter.submitList(jio.getUsersAttending()));
//            }
        });
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        mModel.getJiosData();
    }

}