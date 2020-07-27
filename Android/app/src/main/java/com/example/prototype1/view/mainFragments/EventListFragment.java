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
import com.example.prototype1.model.NEvent;
import com.example.prototype1.view.adapters.EventListAdapter;
import com.example.prototype1.view.dialogs.SearchDialogFragment;
import com.example.prototype1.viewmodel.TitleFragmentViewModel;

import org.jetbrains.annotations.NotNull;


public class EventListFragment extends Fragment implements EventListAdapter.OnItemSelectedListener {
    private TitleFragmentViewModel mModel;
    private SearchDialogFragment mSearchDialog;
    private boolean isScrolling = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_event_list, container, false);
        Toolbar mToolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbar);

        // Events ViewModel
        mModel = new ViewModelProvider(requireActivity()).get(TitleFragmentViewModel.class);

        // Link Events List Recycler View to Adapter
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_restaurants);
        final EventListAdapter mAdapter = new EventListAdapter(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); //Last parameter spanCount of 2 indicates 2 events per row in grid
        mModel.getEventsData().observe(getViewLifecycleOwner(), mAdapter::submitList); // Link Adapter to getEventsData() in ViewModel; getEventsData() returns eventList

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
                    mModel.getEventsData();
                }
            }
        };
        recyclerView.addOnScrollListener(onScrollListener);

        //Shows search dialog when searchBox clicked
        mSearchDialog = new SearchDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("eventType", "events");
        mSearchDialog.setArguments(bundle);
        RelativeLayout searchBox = rootView.findViewById(R.id.searchBox);
        searchBox.setOnClickListener(v -> mSearchDialog.show(requireActivity().getSupportFragmentManager(), SearchDialogFragment.TAG)); //TODO: Do we need tag?


        ImageView clearFilter = rootView.findViewById(R.id.button_clear_filter);
        clearFilter.setOnClickListener(v -> {
            mSearchDialog.resetFlag = 1; //Flag needed due to bug where resetting spinner setSelection is not saved; reset later onResume
            mModel.changeEventFilter(new Filters()); //Reset mFilter in ViewModel as mFilter is parameter of getData()
            mModel.mEventSearchSort.setValue("sorted by date");
            mModel.getEventsData();
        });

        //Automatically changes text to in search box to reflect current filter
        TextView mSearchSort = rootView.findViewById(R.id.text_current_sort_by);

        // Update the UI, in this case, a TextView.
        final Observer<String> searchSortObserver = mSearchSort::setText;
        mModel.mEventSearchSort.observe(getViewLifecycleOwner(), searchSortObserver);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //Navigate to EventDetail when event clicked
    @Override
    public void onItemSelected(@NotNull NEvent mEvent, @NotNull View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(EventListFragmentDirections.actionEventListFragmentToEventDetailFragment(mEvent, "events")); //type "events" provided in case we use EventDetailFragment for "jios" too
    }
}

