package com.example.cibi.messenger.GUI;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.example.cibi.messenger.Controller.EventHandler;
import com.example.cibi.messenger.Controller.NewMessageCheckService;
import com.example.cibi.messenger.Model.Conversation;
import com.example.cibi.messenger.Model.User;
import com.example.cibi.messenger.R;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Properties;

// Fragment zobrazuje nedavne konverzacie.
public class RecentConversationsFragment extends Fragment {
    User loggedIn;
    BroadcastReceiver receiver;
    FrameLayout frameLayout;
    NewMessageCheckService checkService;
    boolean isBound = false;
    EventHandler eventHandler = new EventHandler();

    private ArrayList<Conversation> conversationList = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public RecentConversationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getActivity().getIntent();
        loggedIn = (User) i.getSerializableExtra("User");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MyLog", "OnCreateView");
        conversationList = new ArrayList<>();
        frameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_conversations, container, false);
        ListView convList = (ListView) frameLayout.findViewById(R.id.listView);

        // Ziskaj obsah GUI.
        eventHandler.initRecentConversations(frameLayout, conversationList, this, loggedIn);

        // handle conversation click
        convList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        eventHandler.openSpecificConversation(conversationList, position, getActivity(),loggedIn);
                    }
                }
        );

        // Bindne k fragmentu sluzbu ktora kontroluje prichod novych sprav do aisu.
        if (isBound == false) {
            Intent i = new Intent(getActivity(), NewMessageCheckService.class);
            i.putExtra("User", loggedIn);
            i.putExtra("refreshMilis",getRefreshInterval());
            getContext().bindService(i, msgCheckConnection, Context.BIND_AUTO_CREATE);
            Log.d("MyLog", "service binded");

            // broadcast reciever pocuva spravy od sluzby, ktora hlada nove spravy
            receiver = new MyBroadcastReciever(this);

            LocalBroadcastManager.getInstance(getActivity()).registerReceiver((receiver),
                    new IntentFilter("Result")
            );
        }

        // Inflate the layout for this fragment
        return frameLayout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
        mListener = null;
    }

    public int getRefreshInterval () {
        Properties prop = new Properties();
        try {
            prop.load(this.getContext().getAssets().open("conf.properties"));
        }
        catch (IOException e) {
            Log.e("MyLog","Properties problem",e);
        }
        return Integer.parseInt(prop.getProperty("refreshMilis"));
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public ArrayList<Conversation> getConversationList() {
        return conversationList;
    }

    public void setConversationList(ArrayList<Conversation> conversationList) {
        this.conversationList = conversationList;
    }

    // pripojenie k sluzbe, ktora checkuje spravy
    private ServiceConnection msgCheckConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            NewMessageCheckService.MyLocalBinder binder = (NewMessageCheckService.MyLocalBinder) service;
            checkService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    private class MyBroadcastReciever extends BroadcastReceiver {
        RecentConversationsFragment fragment;

        MyBroadcastReciever (RecentConversationsFragment fragment) {
            this.fragment = fragment;
        }

        // Handluje ked sluzba najde novu spravu.
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MyLog", "New MSG found!");
            eventHandler.updateRecentConversations(frameLayout,fragment,loggedIn);
        }
    }



}
