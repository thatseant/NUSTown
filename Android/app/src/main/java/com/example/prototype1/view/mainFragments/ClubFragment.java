package com.example.prototype1.view.mainFragments;


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
import com.example.prototype1.view.adapters.ClubListAdapter;
import com.example.prototype1.viewmodel.TitleFragmentViewModel;

import org.jetbrains.annotations.NotNull;


public class ClubFragment extends Fragment implements ClubListAdapter.OnItemSelectedListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_club, container, false);
        Toolbar mToolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbar);

        //Link Recycler View to Adapter
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_restaurants);
        final ClubListAdapter mAdapter = new ClubListAdapter(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        //Main ViewModel
        TitleFragmentViewModel mModel = new ViewModelProvider(requireActivity()).get(TitleFragmentViewModel.class);
        //Link Adapter to getData() in ViewModel; getData() returns clubList
        mModel.getClubsData().observe(getViewLifecycleOwner(), mAdapter::submitList);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onItemSelected(@NotNull NClub mClub, @NotNull View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(ClubFragmentDirections.actionClubFragmentToClubDetailFragment(mClub));
    }
}
