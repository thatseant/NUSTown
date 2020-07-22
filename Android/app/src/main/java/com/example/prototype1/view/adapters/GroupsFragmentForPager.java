package com.example.prototype1.view.adapters;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype1.R;
import com.example.prototype1.model.NClub;
import com.example.prototype1.view.dialogs.AddEventDialogFragment;
import com.example.prototype1.view.dialogs.GroupInfoFragment;
import com.example.prototype1.view.dialogs.InfoDialogFragment;
import com.example.prototype1.view.dialogs.SearchDialogFragment;
import com.example.prototype1.view.mainFragments.JioListFragmentDirections;
import com.example.prototype1.viewmodel.TitleFragmentViewModel;

import org.jetbrains.annotations.NotNull;


public class GroupsFragmentForPager extends Fragment implements GroupsAdapter.OnItemSelectedListener{
    private TitleFragmentViewModel mModel; //Events ViewModel
    private SearchDialogFragment mSearchDialog;
    private AddEventDialogFragment mAddDialog;
    private InfoDialogFragment mInfoDialog;
    private GroupInfoFragment mGroupDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragments_for_jios_pager, container, false);
        Toolbar mToolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbar);

        // Events ViewModel
        mModel = new ViewModelProvider(requireActivity()).get(TitleFragmentViewModel.class);

        //Link Recycler View to Adapter
        RecyclerView recyclerClubsView = rootView.findViewById(R.id.recycler_jio_clubs);
        final GroupsAdapter mClubAdapter = new GroupsAdapter(this);
        recyclerClubsView.setAdapter(mClubAdapter);
        recyclerClubsView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mModel.getGroups().observe(getViewLifecycleOwner(),
                mClubAdapter::submitList);   //Link Adapter to getJiosData() in ViewModel; getJiosData() returns jioList

        mGroupDialog = new GroupInfoFragment(); //For displaying GroupDialogFragment about group when clicked

        View addGroupButton = rootView.findViewById(R.id.add_group_button);
        addGroupButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(rootView);
            navController.navigate(JioListFragmentDirections.actionJioListFragmentToAddGroupFragment());
        });

        return rootView;
    }

    //Navigate to GroupDialog when club clicked
    @Override
    public void onItemSelected(@NotNull NClub mGroup, @NotNull View view) {
        Bundle infoBundle = new Bundle();
        infoBundle.putParcelable("mGroup", mGroup);
        mGroupDialog.setArguments(infoBundle);
        mGroupDialog.show(requireActivity().getSupportFragmentManager(), InfoDialogFragment.TAG);
    }
}
