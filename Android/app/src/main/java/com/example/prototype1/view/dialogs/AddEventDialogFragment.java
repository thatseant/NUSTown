package com.example.prototype1.view.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

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
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddEventDialogFragment extends DialogFragment implements View.OnClickListener {

    static public final String TAG = "AddDialog";
    private String groupName;
    private View mRootView;

    private TitleFragmentViewModel mModel;
    DatePicker datePicker;
    TimePicker timePicker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_add_event_dialog, container, false);
        groupName = getArguments().getString("group_name");

        TextView dialogTitle = mRootView.findViewById(R.id.dialog_title);

        if (groupName.equals("")) {
            dialogTitle.setText("New Event");
        } else {
            dialogTitle.setText("New " + groupName + " Event");
        }

        mRootView.findViewById(R.id.button_add).setOnClickListener(this);
        mRootView.findViewById(R.id.button_cancel).setOnClickListener(this);

        datePicker = mRootView.findViewById(R.id.datePicker);
        timePicker = mRootView.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);


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
            case R.id.button_add:
                try {
                    onApplyClicked();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button_cancel:
                dismiss();
                break;
        }
    }

    private void onApplyClicked() throws ParseException {
        EditText newEventName = mRootView.findViewById(R.id.newJioName);
        EditText newEventPlace = mRootView.findViewById(R.id.newJioPlace);
        String newNameString = newEventName.getText().toString();
        String newPlaceString = newEventPlace.getText().toString();
        String dateFromPicker = datePicker.getDayOfMonth() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getYear() + " " + timePicker.getHour() + ":" + timePicker.getMinute();
        DateFormat dateFormat = new SimpleDateFormat("d/M/yyyy H:m", Locale.ENGLISH);
        Date formattedTimeString = dateFormat.parse(dateFromPicker);

        NEvent newJio = new NEvent();
        newJio.setName(newNameString);
        newJio.setTime(formattedTimeString);
        newJio.setOrgUser(mModel.getUser().getValue().getEmail());
        newJio.setPlace(newPlaceString);
        newJio.setOrg(groupName);
        mModel.addDoc(newJio, "jios");
        dismiss();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        mModel.getJiosData();
    }


}