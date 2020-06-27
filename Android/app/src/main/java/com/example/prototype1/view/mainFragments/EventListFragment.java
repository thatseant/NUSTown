package com.example.prototype1.view.mainFragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype1.R;
import com.example.prototype1.model.Filters;
import com.example.prototype1.model.NEvent;
import com.example.prototype1.view.adapters.EventListAdapter;
import com.example.prototype1.view.dialogs.SearchDialogFragment;
import com.example.prototype1.viewmodel.TitleFragmentViewModel;

import org.jetbrains.annotations.NotNull;


public class EventListFragment extends Fragment implements EventListAdapter.OnItemSelectedListener {
    private TitleFragmentViewModel mModel; //Events ViewModel
    private SearchDialogFragment mSearchDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_event_list, container, false);
        Toolbar mToolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbar);

        mSearchDialog = new SearchDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("eventType", "events");
        mSearchDialog.setArguments(bundle);

        //Link Recycler View to Adapter
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_restaurants);
        final EventListAdapter mAdapter = new EventListAdapter(this);
        recyclerView.setAdapter(mAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mModel = new ViewModelProvider(requireActivity()).get(TitleFragmentViewModel.class);
        //Link Adapter to getData() in ViewModel; getData() returns eventList
        mModel.getEventsData().observe(getViewLifecycleOwner(), mAdapter::submitList);

        //Automatically changes text to in search box to reflect current filter
        TextView mSearchCat = rootView.findViewById(R.id.text_current_search);
        mSearchCat.setText(HtmlCompat.fromHtml("<b>All Events<b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
        TextView mSearchSort = rootView.findViewById(R.id.text_current_sort_by);
        final Observer<String> searchCatObserver = searchCat -> {
            // Update the UI, in this case, a TextView.
            mSearchCat.setText(HtmlCompat.fromHtml(searchCat, HtmlCompat.FROM_HTML_MODE_LEGACY));
        };
        mModel.mEventSearchCat.observe(getViewLifecycleOwner(), searchCatObserver);

        // Update the UI, in this case, a TextView.
        final Observer<String> searchSortObserver = mSearchSort::setText;
        mModel.mEventSearchSort.observe(getViewLifecycleOwner(), searchSortObserver);

        //Shows search dialog when searchBox clicked
        RelativeLayout searchBox = rootView.findViewById(R.id.searchBox);
        searchBox.setOnClickListener(v -> mSearchDialog.show(requireActivity().getSupportFragmentManager(), SearchDialogFragment.TAG));


        ImageView clearFilter = rootView.findViewById(R.id.button_clear_filter);
        clearFilter.setOnClickListener(v -> {
            mSearchDialog.resetFlag = 1; //Flag needed due to bug where resetting spinner setSelection is not saved; reset later onResume
            mModel.changeEventFilter(new Filters()); //Reset mFilter in ViewModel as mFilter is parameter of getData()
            mModel.mEventSearchCat.setValue("<b> All Events <b>");
            mModel.mEventSearchSort.setValue("sorted by date");
            mModel.getEventsData();
        });


        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onItemSelected(@NotNull NEvent mEvent, @NotNull View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(EventListFragmentDirections.actionEventListFragmentToEventDetailFragment(mEvent, "events")); //type "events" provided in case we use EventDetailFragment for "jios" too
    }
}
