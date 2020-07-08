package com.example.prototype1.view.sideFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.prototype1.R;
import com.example.prototype1.model.NEvent;
import com.example.prototype1.viewmodel.TitleFragmentViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditPostFragment extends Fragment {
    private TitleFragmentViewModel mModel;
    private String captionToEdit;
    private String postDate;
    private NEvent updatedEvent;

    private NEvent mEvent;
    private String eventType;
    private Map<String, String> existingUpdates;

    public EditPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View editEventView = inflater.inflate(R.layout.fragment_edit_post, container, false);

        // Retrieve NEvent object to be edited
        assert getArguments() != null;
        captionToEdit = EditPostFragmentArgs.fromBundle(getArguments()).getPostCaption();
        postDate = EditPostFragmentArgs.fromBundle(getArguments()).getPostDate();
        mEvent = EditPostFragmentArgs.fromBundle(getArguments()).getEventToEdit();

        if (postDate.equals("")) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
            postDate = dateFormat.format(new Date());
        }

        mModel = new ViewModelProvider(requireActivity()).get(TitleFragmentViewModel.class);

        EditText captionBox = editEventView.findViewById(R.id.postCaption);
        TextView captionTitle = editEventView.findViewById(R.id.postDate);
        captionTitle.setText(postDate);
        captionBox.setText(captionToEdit);

        updatedEvent = mEvent;
        existingUpdates = updatedEvent.getUpdates();

        Button submitButton = editEventView.findViewById(R.id.confirmEditButton);

        submitButton.setOnClickListener(v -> {
            String newCaption = captionBox.getText().toString();
            existingUpdates.put(postDate, newCaption);
            updatedEvent.setUpdates(existingUpdates);
            mModel.updateEvent(updatedEvent, "events");
            NavController navController = Navigation.findNavController(editEventView);
            navController.popBackStack();
        });

        ImageView buttonClose = editEventView.findViewById(R.id.edit_button_back);
        buttonClose.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(editEventView);
            navController.popBackStack();
        });

        return editEventView;
    }
}