package com.example.prototype1.view.sideFragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.prototype1.R;
import com.example.prototype1.model.NEvent;
import com.example.prototype1.model.NMessage;
import com.example.prototype1.view.adapters.ChatAdapter;
import com.example.prototype1.viewmodel.TitleFragmentViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Date;

public class ChatFragment extends Fragment {
    TitleFragmentViewModel mModel;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //Retrieve NEvent object clicked on in RecyclerView: supplied by Nav Safe Args
        NEvent mEvent = ChatFragmentArgs.fromBundle(getArguments()).getMEvent();

        //Toolbar displays event name
        Toolbar mToolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbar);
        NavigationUI.setupWithNavController(mToolbar, NavHostFragment.findNavController(this));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mEvent.getName());

        //Events ViewModel
        mModel = new ViewModelProvider(requireActivity()).get(TitleFragmentViewModel.class);

        RecyclerView chatRecycler = rootView.findViewById(R.id.list_chat);
        ChatAdapter chatAdapter = new ChatAdapter();
        chatRecycler.setAdapter(chatAdapter);
        chatRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mModel.getMessages(mEvent.getID()).observe(getViewLifecycleOwner(), chatAdapter::submitList);

        //Send message when button press
        ImageView sendButton = rootView.findViewById(R.id.button_send);
        sendButton.setOnClickListener(v -> {
            EditText chatTextBox = rootView.findViewById(R.id.edittext_chat);
            String chatText = chatTextBox.getText().toString();
            chatTextBox.getText().clear();
            String username = user.getEmail();
            Date date = new Date();
            NMessage newMessage = new NMessage(chatText, username, date);
            mModel.addDoc(newMessage, "events/" + mEvent.getID() + "/messages" );
        });

        return rootView;
    }

}