package com.example.prototype1.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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

/**
 * Dialog Fragment containing filter form.
 */
public class AddEventDialogFragment extends DialogFragment implements View.OnClickListener {

    static final String TAG = "AddDialog";

    private View mRootView;

    private TitleFragmentViewModel mModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_add_event_dialog, container, false);


        mRootView.findViewById(R.id.button_add).setOnClickListener(this);
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
        EditText newEventTime = mRootView.findViewById(R.id.newJioTime);
        String newNameString = newEventName.getText().toString();
        String newTimeString = newEventTime.getText().toString();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
        Date time = format.parse(newTimeString);
        NEvent newJio = new NEvent();
        newJio.setName(newNameString);
        assert time != null;
        newJio.setTime(time);
        mModel.addEvent(newJio, "jios");
        dismiss();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        mModel.getJiosData();
    }


}