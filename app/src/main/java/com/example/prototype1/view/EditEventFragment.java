package com.example.prototype1.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.prototype1.R;
import com.example.prototype1.model.NEvent;
import com.example.prototype1.viewmodel.TitleFragmentViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditEventFragment extends Fragment {
    private TitleFragmentViewModel mModel;
    private NEvent eventToEdit;
    private NEvent updatedEvent;

    public EditEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View EditEventView = inflater.inflate(R.layout.fragment_edit_event, container, false);

        // Retrieve NEvent object to be edited
        NEvent eventToEdit = EditEventFragmentArgs.fromBundle(getArguments()).getEventToEdit();

        mModel = new ViewModelProvider(getActivity()).get(TitleFragmentViewModel.class);

        EditText newEventName = EditEventView.findViewById(R.id.newEventName);
        EditText newEventPlace = EditEventView.findViewById(R.id.newEventPlace);
        EditText newEventCat = EditEventView.findViewById(R.id.newEventCat);
        Button confirmEditButton = EditEventView.findViewById(R.id.confirmEditButton);
        confirmEditButton.setOnClickListener(v -> {
            String newNameString = newEventName.getText().toString();
            String newPlaceString = newEventPlace.getText().toString();
            String newCatString = newEventCat.getText().toString();

            updatedEvent = new NEvent(eventToEdit.getID(), newCatString, newNameString, newPlaceString, eventToEdit.getRating(), eventToEdit.getImage());

            mModel.updateEvent(updatedEvent);
            mModel.getData();
            NavController navController = Navigation.findNavController(EditEventView);
            navController.navigate(EditEventFragmentDirections.actionEditEventToEventDetailFragment(updatedEvent));
        });
        return EditEventView;
    }
}
