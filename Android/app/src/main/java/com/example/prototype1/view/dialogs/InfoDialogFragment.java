package com.example.prototype1.view.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.prototype1.R;
import com.example.prototype1.model.NEvent;
import com.example.prototype1.viewmodel.TitleFragmentViewModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * Dialog Fragment containing filter form.
 */
public class InfoDialogFragment extends DialogFragment implements View.OnClickListener {

    static public final String TAG = "InfoDialog";

    private View mRootView;

    private TitleFragmentViewModel mModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_info_dialog, container, false);

        NEvent mEvent = getArguments().getParcelable("mEvent");
        TextView jioName = mRootView.findViewById(R.id.jioDialogName);
        jioName.setText(mEvent.getName());
        TextView jioInfo = mRootView.findViewById(R.id.jioDialogInfo);
        jioInfo.setText(mEvent.getInfo());

        TextView jioTime = mRootView.findViewById(R.id.jioDialogTime);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
        jioTime.setText(dateFormat.format(mEvent.getTime()));


        TextView jioAttendees = mRootView.findViewById(R.id.jioDialogAttendees);
        jioAttendees.setText(mEvent.getNumberAttending() + " Attending");
        mRootView.findViewById(R.id.button_jio_rsvp).setOnClickListener(this);
        mRootView.findViewById(R.id.button_cancel).setOnClickListener(this);

        mModel = new ViewModelProvider(requireActivity()).get(TitleFragmentViewModel.class); //returns same instance of ViewModel in TitleFragment
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
                try {
                    onRSVPClicked();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button_cancel:
                dismiss();
                break;
        }
    }

    private void onRSVPClicked() throws ParseException {

        dismiss();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        mModel.getJiosData();
    }
}