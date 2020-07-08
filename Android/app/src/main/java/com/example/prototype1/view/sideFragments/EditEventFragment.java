package com.example.prototype1.view.sideFragments;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class EditEventFragment extends Fragment {
    private TitleFragmentViewModel mModel;
    private NEvent eventToEdit;
    private NEvent updatedEvent;
    private String eventType;

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

        //Uses existing event properties as default in EditText
        EditText newEventName = EditEventView.findViewById(R.id.newEventName);
        newEventName.setText(eventToEdit.getName());
        EditText newEventPlace = EditEventView.findViewById(R.id.newEventPlace);
        newEventPlace.setText(eventToEdit.getPlace());
        EditText newEventCat = EditEventView.findViewById(R.id.newEventCat);
        newEventCat.setText(eventToEdit.getCategory());
        DatePicker datePicker = EditEventView.findViewById(R.id.datePicker);
        TimePicker timePicker = EditEventView.findViewById(R.id.timePicker);

        LocalDateTime existingDate = eventToEdit.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();


        datePicker.updateDate(existingDate.getYear(), existingDate.getMonthValue() - 1, existingDate.getDayOfMonth());
        timePicker.setHour(existingDate.getHour());
        timePicker.setMinute(existingDate.getMinute());
        timePicker.setIs24HourView(true);
//        newEventTime.setText(dateFormat.format(eventToEdit.getTime()));
        EditText newEventURL = EditEventView.findViewById(R.id.newEventUrl);
        newEventURL.setText(eventToEdit.getUrl());
        EditText newEventInfo = EditEventView.findViewById(R.id.newEventDescription);
        newEventInfo.setText(eventToEdit.getInfo());

        Button confirmEditButton = EditEventView.findViewById(R.id.confirmEditButton);
        confirmEditButton.setOnClickListener(v -> {
            String newNameString = newEventName.getText().toString();
            String newPlaceString = newEventPlace.getText().toString();
            String newCatString = newEventCat.getText().toString();

            Date formattedTimeString = new Date();
            try {
                String dateFromPicker = Integer.toString(datePicker.getDayOfMonth()) + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getYear() + " " + timePicker.getHour() + ":" + timePicker.getMinute();
                DateFormat dateFormat = new SimpleDateFormat("d/M/yyyy H:m", Locale.ENGLISH);
                formattedTimeString = dateFormat.parse(dateFromPicker);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            String newURLString = newEventURL.getText().toString();
            String newInfoString = newEventInfo.getText().toString();

            updatedEvent = eventToEdit; //Changes name, place, category

            updatedEvent.setTime(formattedTimeString);
            updatedEvent.setName(newNameString);
            updatedEvent.setCategory(newCatString);
            updatedEvent.setPlace(newPlaceString);
            updatedEvent.setUrl(newURLString);
            updatedEvent.setInfo(newInfoString);

            mModel.updateEvent(updatedEvent, eventType); //Updates Repository via ViewModel
            mModel.getEventsData();

//            if (eventType=="jios") {
//                NavController navController = Navigation.findNavController(EditEventView);
//                navController.navigate(EditEventFragmentDirections.actionEditEventToJioListFragment());
//            } else {
//                NavController navController = Navigation.findNavController(EditEventView);
//                navController.navigate(EditEventFragmentDirections.actionEditEventToEventDetailFragment(updatedEvent, eventType));
//            }
            NavController navController = Navigation.findNavController(EditEventView);
            navController.popBackStack();
        });

        //Close EventDetailFragment on buttonClose clicked
        ImageView buttonClose = EditEventView.findViewById(R.id.edit_button_back);
        buttonClose.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(EditEventView);
            navController.popBackStack();
        });

        return EditEventView;
    }
}
