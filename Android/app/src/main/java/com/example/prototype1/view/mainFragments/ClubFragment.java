package com.example.prototype1.view.mainFragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype1.R;
import com.example.prototype1.model.Filters;
import com.example.prototype1.model.NClub;
import com.example.prototype1.view.adapters.ClubListAdapter;
import com.example.prototype1.view.dialogs.ClubsSearchDialogFragment;
import com.example.prototype1.view.dialogs.SearchDialogFragment;
import com.example.prototype1.viewmodel.TitleFragmentViewModel;

import org.jetbrains.annotations.NotNull;


public class ClubFragment extends Fragment implements ClubListAdapter.OnItemSelectedListener {
    private boolean isScrolling = false;
    private ClubsSearchDialogFragment mSearchDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_club, container, false);
        Toolbar mToolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbar);

        //Main ViewModel
        TitleFragmentViewModel mModel = new ViewModelProvider(requireActivity()).get(TitleFragmentViewModel.class);

        //Link Clubs List Recycler View to Adapter
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_restaurants);
        final ClubListAdapter mAdapter = new ClubListAdapter(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mModel.getClubsData().observe(getViewLifecycleOwner(), mAdapter::submitList);  //Link Adapter to getClubsData() in ViewModel; getClubsData() returns clubList

        // Allows for pagination of Firestore Data
        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();

                if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount)) {
                    isScrolling = false;
                    mModel.getClubsData();
                }
            }
        };
        recyclerView.addOnScrollListener(onScrollListener);

        //Shows search dialog when searchBox clicked
        mSearchDialog = new ClubsSearchDialogFragment();
        RelativeLayout searchBox = rootView.findViewById(R.id.searchBox);
        searchBox.setOnClickListener(v -> mSearchDialog.show(requireActivity().getSupportFragmentManager(), SearchDialogFragment.TAG));

        ImageView clearFilter = rootView.findViewById(R.id.button_clear_filter);
        clearFilter.setOnClickListener(v -> {
            mSearchDialog.resetFlag = 1; //Flag needed due to bug where resetting spinner setSelection is not saved; reset later onResume
            mModel.changeClubFilter(new Filters(true)); //Reset mFilter in ViewModel as mFilter is parameter of getData()
            mModel.mClubSearchCat.setValue("<b> All Clubs <b>");
            mModel.getClubsData();
        });

        //Automatically changes text to in search box to reflect current filter
        TextView mSearchCat = rootView.findViewById(R.id.text_current_search);
        mSearchCat.setText(HtmlCompat.fromHtml("<b>All Clubs<b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
        final Observer<String> searchCatObserver = searchCat -> {
            // Update the UI, in this case, a TextView.
            mSearchCat.setText(HtmlCompat.fromHtml(searchCat, HtmlCompat.FROM_HTML_MODE_LEGACY));
        };
        mModel.mClubSearchCat.observe(getViewLifecycleOwner(), searchCatObserver);


        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //Navigate to ClubDetail when club clicked
    @Override
    public void onItemSelected(@NotNull NClub mClub, @NotNull View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(ClubFragmentDirections.actionClubFragmentToClubDetailFragment(mClub, "clubs"));
    }
}
