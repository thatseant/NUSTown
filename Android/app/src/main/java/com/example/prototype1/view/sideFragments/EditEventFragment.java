package com.example.prototype1.view.sideFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
        EditText newEventTime = EditEventView.findViewById(R.id.newEventTime);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
        newEventTime.setText(dateFormat.format(eventToEdit.getTime()));
        EditText newEventURL = EditEventView.findViewById(R.id.newEventUrl);
        newEventURL.setText(eventToEdit.getUrl());
        EditText newEventInfo = EditEventView.findViewById(R.id.newEventDescription);
        newEventInfo.setText(eventToEdit.getInfo());

        Button confirmEditButton = EditEventView.findViewById(R.id.confirmEditButton);
        confirmEditButton.setOnClickListener(v -> {
            String newNameString = newEventName.getText().toString();
            String newPlaceString = newEventPlace.getText().toString();
            String newCatString = newEventCat.getText().toString();
            String newTimeString = newEventTime.getText().toString();
            Date formattedTimeString = new Date();
            try {
                formattedTimeString = dateFormat.parse(newTimeString);//TODO: Fix Time
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String newURLString = newEventURL.getText().toString();
            String newInfoString = newEventInfo.getText().toString();

            updatedEvent = new NEvent(eventToEdit.getID(), newNameString, formattedTimeString, newCatString, newPlaceString, eventToEdit.getRating(),
                    eventToEdit.getNumberAttending(), newURLString, eventToEdit.getImage(), newInfoString, eventToEdit.getImgUrl(), eventToEdit.getOrg(), eventToEdit.getUpdates(), eventToEdit.getMaxAttending(), eventToEdit.getUsersAttending()); //Changes name, place, category

            mModel.updateEvent(updatedEvent, eventType); //Updates Repository via ViewModel
            mModel.getEventsData();
            NavController navController = Navigation.findNavController(EditEventView);
            navController.navigate(EditEventFragmentDirections.actionEditEventToEventDetailFragment(updatedEvent, eventType));
        });

        //Close EventDetailFragment on buttonClose clicked
        ImageView buttonClose = EditEventView.findViewById(R.id.edit_button_back);
        buttonClose.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(EditEventView);
            navController.navigate(EditEventFragmentDirections.actionEditEventToEventDetailFragment(eventToEdit, eventType));
        });

        return EditEventView;
    }
}
