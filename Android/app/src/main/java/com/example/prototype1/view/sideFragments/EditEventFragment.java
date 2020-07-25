package com.example.prototype1.view.sideFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.prototype1.R;
import com.example.prototype1.model.NEvent;
import com.example.prototype1.viewmodel.TitleFragmentViewModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class EditEventFragment extends Fragment {
    private TitleFragmentViewModel mModel;
    private NEvent eventToEdit;
    private String eventType;
    private int changePhotoFlag = 0;
    private Uri photoURI;

    public EditEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View EditEventView = inflater.inflate(R.layout.fragment_edit_event, container, false);

        // Retrieve NEvent object to be edited
        assert getArguments() != null;
        eventToEdit = EditEventFragmentArgs.fromBundle(getArguments()).getEventToEdit();
        eventType = EditEventFragmentArgs.fromBundle(getArguments()).getType();


        mModel = new ViewModelProvider(requireActivity()).get(TitleFragmentViewModel.class);

        //Sets EditTexts to existing event properties
        EditText newEventName = EditEventView.findViewById(R.id.newEventName);
        newEventName.setText(eventToEdit.getName());
        EditText newEventPlace = EditEventView.findViewById(R.id.newEventPlace);
        newEventPlace.setText(eventToEdit.getPlace());
        EditText newEventCat = EditEventView.findViewById(R.id.newEventCat);
        newEventCat.setText(eventToEdit.getCategory());
        EditText newEventURL = EditEventView.findViewById(R.id.newEventUrl);
        newEventURL.setText(eventToEdit.getUrl());
        EditText newEventInfo = EditEventView.findViewById(R.id.newEventDescription);
        newEventInfo.setText(eventToEdit.getInfo());

        //Sets DatePicker and TimePicker
        DatePicker datePicker = EditEventView.findViewById(R.id.datePicker);
        TimePicker timePicker = EditEventView.findViewById(R.id.timePicker);
        LocalDateTime existingDate = eventToEdit.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        datePicker.updateDate(existingDate.getYear(), existingDate.getMonthValue() - 1, existingDate.getDayOfMonth());
        timePicker.setHour(existingDate.getHour());
        timePicker.setMinute(existingDate.getMinute());
        timePicker.setIs24HourView(true);

        Button photoButton = EditEventView.findViewById(R.id.photoBtn);
        photoButton.setOnClickListener(v -> {
            changePhotoFlag = 1;
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
        });

        Button confirmEditButton = EditEventView.findViewById(R.id.confirmEditButton);
        confirmEditButton.setOnClickListener(v -> {
            //Sets new properties from EditText inputs
            String newNameString = newEventName.getText().toString();
            String newPlaceString = newEventPlace.getText().toString();
            String newCatString = newEventCat.getText().toString();
            String newURLString = newEventURL.getText().toString();
            String newInfoString = newEventInfo.getText().toString();

            Date formattedTimeString = new Date();
            try {
                String dateFromPicker = datePicker.getDayOfMonth() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getYear() + " " + timePicker.getHour() + ":" + timePicker.getMinute();
                DateFormat dateFormat = new SimpleDateFormat("d/M/yyyy H:m", Locale.ENGLISH);
                formattedTimeString = dateFormat.parse(dateFromPicker);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //Updates eventToEdit with new properties
            eventToEdit.setTime(formattedTimeString);
            eventToEdit.setName(newNameString);
            eventToEdit.setCategory(newCatString);
            eventToEdit.setPlace(newPlaceString);
            eventToEdit.setUrl(newURLString);
            eventToEdit.setInfo(newInfoString);

            mModel.updateEvent(eventToEdit, eventType); //Updates Repository via ViewModel
            mModel.getEventsData();

            if (photoURI != null) {
                mModel.uploadPic("jios", newNameString, photoURI, () -> {});
            }

            NavController navController = Navigation.findNavController(EditEventView);
            navController.popBackStack();
        });


        //Close EditEventFragment on buttonClose clicked
        ImageView buttonClose = EditEventView.findViewById(R.id.edit_button_back);
        buttonClose.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(EditEventView);
            navController.popBackStack();
        });

        return EditEventView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK) {
            photoURI = data.getData();
        }
    }
}
