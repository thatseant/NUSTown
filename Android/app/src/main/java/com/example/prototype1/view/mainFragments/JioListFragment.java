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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.prototype1.R;
import com.example.prototype1.model.Filters;
import com.example.prototype1.model.NClub;
import com.example.prototype1.model.NEvent;
import com.example.prototype1.view.adapters.ClubListAdapter;
import com.example.prototype1.view.adapters.GroupsAdapter;
import com.example.prototype1.view.adapters.JioListAdapter;
import com.example.prototype1.view.adapters.ScreenSlidePagerAdapter;
import com.example.prototype1.view.dialogs.AddEventDialogFragment;
import com.example.prototype1.view.dialogs.GroupInfoFragment;
import com.example.prototype1.view.dialogs.InfoDialogFragment;
import com.example.prototype1.view.dialogs.SearchDialogFragment;
import com.example.prototype1.viewmodel.TitleFragmentViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class JioListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_jio_list, container, false);
        Toolbar mToolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbar);

        // Instantiate a ViewPager2 and a PagerAdapter.
        ViewPager2 viewPager = rootView.findViewById(R.id.pager);
        ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(requireActivity());
        viewPager.setAdapter(pagerAdapter);

        ArrayList<String> tabsTitle = new ArrayList<>();
        tabsTitle.add("Jios");
        tabsTitle.add("Groups");
        TabLayout tabLayout = rootView.findViewById(R.id.posts_tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, //Creates tabs for individual posts where title is post date (stored as key).
                (tab, position) -> tab.setText(tabsTitle.get(position))
        ).attach();

        return rootView;
    }

}
