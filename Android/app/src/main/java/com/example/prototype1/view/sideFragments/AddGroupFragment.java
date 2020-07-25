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
import com.example.prototype1.model.NClub;
import com.example.prototype1.model.NEvent;
import com.example.prototype1.viewmodel.TitleFragmentViewModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class AddGroupFragment extends Fragment {
    private TitleFragmentViewModel mModel;
    private NClub mClub = new NClub();
    private String eventType;
    private Uri photoURI;
    private int changePhotoFlag = 0;

    public AddGroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View AddGroupView = inflater.inflate(R.layout.fragment_add_group, container, false);

        mModel = new ViewModelProvider(requireActivity()).get(TitleFragmentViewModel.class);

        //Sets EditTexts to existing event properties
        EditText newGroupName = AddGroupView.findViewById(R.id.newGroupName);
        EditText newGroupCat = AddGroupView.findViewById(R.id.newGroupCat);
        EditText newGroupInfo = AddGroupView.findViewById(R.id.newGroupDescription);

        Button photoButton = AddGroupView.findViewById(R.id.photoBtn);
        photoButton.setOnClickListener(v -> {
            changePhotoFlag = 1;
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
        });

        Button confirmEditButton = AddGroupView.findViewById(R.id.confirmEditButton);
        confirmEditButton.setOnClickListener(v -> {
            //Sets new properties from EditText inputs
            String newNameString = newGroupName.getText().toString();
            String newCatString = newGroupCat.getText().toString();
            String newInfoString = newGroupInfo.getText().toString();


            //Updates eventToEdit with new properties
            mClub.setName(newNameString);
            mClub.setCatName(newCatString);
            mClub.setInfo(newInfoString);

            mModel.addDoc(mClub, "groups"); //Updates Repository via ViewModel
            mModel.getGroups();

            if (photoURI != null) {
                mModel.uploadPic("groups", newNameString, photoURI, () -> {});
            }

            NavController navController = Navigation.findNavController(AddGroupView);
            navController.popBackStack();
        });

        //Close EditEventFragment on buttonClose clicked
        ImageView buttonClose = AddGroupView.findViewById(R.id.edit_button_back);
        buttonClose.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(AddGroupView);
            navController.popBackStack();
        });

        return AddGroupView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK) {
            photoURI = data.getData();
        }
    }
}