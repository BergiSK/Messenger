package com.example.cibi.messenger.Controller;

import android.os.AsyncTask;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.cibi.messenger.GUI.RecentConversationsFragment;
import com.example.cibi.messenger.GUI.RecentConversationsAdapter;


import com.example.cibi.messenger.Model.Conversation;
import com.example.cibi.messenger.Model.User;
import com.example.cibi.messenger.R;

import java.util.ArrayList;
import java.util.List;

public class ConversationHandler {

    // na novom vlakne stiahne z nasej server DB aktualne konverzacie.
    public void getRecentConversations (FrameLayout frameLayout, User loggedIn, RecentConversationsFragment recentConversationsFragment) {
        new AsyncGetConversations(recentConversationsFragment,loggedIn,this,frameLayout).execute();
    }

    public class AsyncGetConversations extends AsyncTask<Void, String, List<Conversation>> {
        RecentConversationsFragment recentConversationsFragment;
        User loggedIn;
        ConversationHandler handler;
        FrameLayout frameLayout;
        ListView convList;
        RelativeLayout loadingBar;

        public AsyncGetConversations(RecentConversationsFragment recentConversationsFragment, User loggedIn,
                                     ConversationHandler handler,FrameLayout frameLayout) {
            this.recentConversationsFragment = recentConversationsFragment;
            this.loggedIn = loggedIn;
            this.handler = handler;
            this.frameLayout = frameLayout;
        }

        @Override
        protected List<Conversation> doInBackground(Void... params) {
            // download messages from db, parse them to message objects,
            // group messages to conversations, returns the convesation list
            BackEndCommunicator communicator = new BackEndCommunicator();
            Transformer transformer = new Transformer();
            return transformer.transform(communicator.getRecent(loggedIn));
        }

        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(List<Conversation> conversationList) {
            convList = (ListView) frameLayout.findViewById(R.id.listView);
            loadingBar = (RelativeLayout) frameLayout.findViewById(R.id.loadingPanel);

            loadingBar.setVisibility(View.INVISIBLE);
            RecentConversationsAdapter adapter = new RecentConversationsAdapter(
                    recentConversationsFragment.getActivity(),(ArrayList)conversationList);
            convList.setAdapter(adapter);
            recentConversationsFragment.setConversationList((ArrayList)conversationList);
        }
    }

}
