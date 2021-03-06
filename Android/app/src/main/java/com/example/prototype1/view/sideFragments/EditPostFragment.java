package com.example.prototype1.view.sideFragments;

import android.content.Intent;
import android.net.Uri;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class EditPostFragment extends Fragment {
    private TitleFragmentViewModel mModel;
    private ArrayList<String> captionToEdit;
    private String postDate;
    private Uri photoURI;
    private int changePhotoFlag = 0;

    private NEvent mEvent;
    private Map<String, ArrayList<String>> existingUpdates;

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

        if (postDate.equals("")) { //If post to edit is a new post
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
            postDate = dateFormat.format(new Date());
        }

        mModel = new ViewModelProvider(requireActivity()).get(TitleFragmentViewModel.class);

        EditText captionBox = editEventView.findViewById(R.id.postCaption);
        TextView captionTitle = editEventView.findViewById(R.id.postDate);
        captionTitle.setText(postDate);

        String cleaned_text = captionToEdit.get(0).replaceAll("&([a-z0-9]+|#[0-9]{1,6}|#x[0-9a-f]{1,6});", " "); //Remove &...; html
            captionBox.setText(cleaned_text);

        existingUpdates = mEvent.getUpdates();

        Button submitButton = editEventView.findViewById(R.id.confirmEditButton);

        submitButton.setOnClickListener(v -> {
             captionToEdit.set(0, captionBox.getText().toString());
             if (changePhotoFlag == 1) {
                 captionToEdit.set(1, mEvent.getID() + "/" + postDate);
             }

            existingUpdates.put(postDate, captionToEdit);
            mEvent.setUpdates(existingUpdates);
            mModel.updateEvent(mEvent, "events");

            if (photoURI != null) {
                mModel.uploadPic("updates", mEvent.getID() + "/" + postDate, photoURI, () -> {});
            }
            NavController navController = Navigation.findNavController(editEventView);
            navController.popBackStack();
        });

        ImageView buttonClose = editEventView.findViewById(R.id.edit_button_back);
        buttonClose.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(editEventView);
            navController.popBackStack();
        });


        Button photoButton = editEventView.findViewById(R.id.photoBtn);
        photoButton.setOnClickListener(v -> {
            changePhotoFlag = 1;
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
        });

        return editEventView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK) {
            photoURI = data.getData();
        }
    }

}