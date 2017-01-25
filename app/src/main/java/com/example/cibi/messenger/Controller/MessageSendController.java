package com.example.cibi.messenger.Controller;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.cibi.messenger.GUI.ConversationActivity;
import com.example.cibi.messenger.GUI.MainScreen;
import com.example.cibi.messenger.GUI.NewMessageFragment;
import com.example.cibi.messenger.GUI.RecentConversationsAdapter;
import com.example.cibi.messenger.GUI.RecentConversationsFragment;
import com.example.cibi.messenger.GUI.RecentMessagesAdapter;
import com.example.cibi.messenger.Model.Conversation;
import com.example.cibi.messenger.Model.Message;
import com.example.cibi.messenger.Model.User;
import com.example.cibi.messenger.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;


// Trieda starajuca sa o logiku prisluchajucu odoslaniu spravy a to 2 sposobmi -
// odoslanie v konverzacii medzi 2 osobami / odoslanie v samostatnom Tabe na zadany email.
public class MessageSendController {
    private String dateFormat;

    public MessageSendController (Activity activity) {
        Properties prop = new Properties();
        try {
            prop.load(activity.getAssets().open("conf.properties"));
        }
        catch (IOException e) {
            Log.e("MyLog","Properties problem",e);
        }
        dateFormat = prop.getProperty("messageDateFormat");
    }

    public void sendMessageInConversation (String msgText, Conversation conversation, ConversationActivity activity,
                                           User loggedIn) {
        new AsyncSendMessageInConversation(msgText,conversation,activity,loggedIn).execute();
    }

    public void sendMessageToStranger (String msgText,String recieverMail,MainScreen activity, NewMessageFragment messageFragment,
            RecentConversationsFragment conversationsFragment,User loggedIn) {
        new AsyncSendMessageToStranger(msgText,recieverMail,activity,this,messageFragment,conversationsFragment,loggedIn).execute();
    }

    // Vrati index konverzacie, do ktorej sprava patri, -1 ak taka este nie je.
    public int belongsToConversation (ArrayList<Conversation> conversations, Message newMsg) {
        for (int i = 0; i < conversations.size(); i++) {
            Log.d("MyLog","EMAIL:" + conversations.get(i).getParticipant().getEmail());
            // ak je cielovy email uz v danej konverzacii
            if (conversations.get(i).getParticipant().getEmail().compareToIgnoreCase(newMsg.getReciever().getEmail()) == 0) {
                return i;
            }
        }
        return -1;
    }

    // Odoslanie spravy na zadany email. Bezi na vlastnom vlakne.
    public class AsyncSendMessageToStranger extends AsyncTask<Void, Void, Integer> {
        MainScreen activity;
        String msgText;
        User loggedIn;
        RelativeLayout loadingBar;
        EditText textToSend;
        EditText recieverField;
        Message newMessage;
        NewMessageFragment fragment;
        RecentConversationsFragment conversationsFragment;
        String recieverMail;
        User reciever;
        ArrayList<Conversation> conversations;
        ListView convList;
        MessageSendController controller;
        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(activity);

        public AsyncSendMessageToStranger(String msgText,String recieverMail, MainScreen activity, MessageSendController controller,
                                          NewMessageFragment fragment, RecentConversationsFragment conversationsFragment,User loggedIn) {
            this.activity = activity;
            this.msgText = msgText;
            this.loggedIn = loggedIn;
            this.fragment = fragment;
            this.recieverMail = recieverMail;
            this.conversationsFragment = conversationsFragment;
            this.conversations = conversationsFragment.getConversationList();
            this.controller = controller;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            // Pomocou BackEndCommunicatora nechaj server poslat spravu do aisu. (automaticky ju ulozi do DB)
            BackEndCommunicator communicator = new BackEndCommunicator();
            communicator.sendMessage(loggedIn.getLoginName(),loggedIn.getCookie(),recieverMail,msgText);

            // odoslanu spravu vloz do aktualnych konverzacii
            reciever = new User();
            reciever.setName(recieverMail);
            reciever.setEmail(recieverMail);

            newMessage = new Message();
            newMessage.setContent(msgText);
            newMessage.setReciever(reciever);
            newMessage.setSender(loggedIn);
            newMessage.setDeliveryTime(new SimpleDateFormat(dateFormat).format(new Date()));

            // skontroluj ci treba vytvorit novu konverzaciu, alebo prislusna existuje
            int index = controller.belongsToConversation(conversations,newMessage);
            if (index != -1) {
                conversations.get(index).addMessage(newMessage);
            }
            else {
                Conversation conversation = new Conversation();
                conversation.setParticipant(newMessage.getReciever());
                conversation.addMessage(newMessage);
                conversations.add(0,conversation);
            }
            conversationsFragment.setConversationList(conversations);

            return 0;
        }

        // Zobraz loading bar pokym sa vykonava odoslanie.
        @Override
        public void onPreExecute () {
            loadingBar = (RelativeLayout) fragment.getView().findViewById(R.id.loadingPanel);
            loadingBar.setVisibility(View.VISIBLE);
        }

        // Uprav GUI po odoslani.
        @Override
        public void onPostExecute(Integer errorCode) {
            recieverField = (EditText) fragment.getView().findViewById(R.id.reciever);
            textToSend = (EditText) fragment.getView().findViewById(R.id.msgText);

            recieverField.setText("");
            textToSend.setText("");
            loadingBar.setVisibility(View.INVISIBLE);
            Toast.makeText(activity,R.string.msgSent,Toast.LENGTH_SHORT).show();
            Log.d("MyLog", "Message send to: " + recieverMail + " with text: " + msgText);
            // update conversation view - adapter
            convList = (ListView) conversationsFragment.getView().findViewById(R.id.listView);
            RecentConversationsAdapter adapter = new RecentConversationsAdapter(
                    conversationsFragment.getActivity(),conversations);
            convList.setAdapter(adapter);
            sendUpdateNotification(broadcaster);
        }
    }

    // Odoslanie spravy v konverzacii. Bezi na vlastnom vlakne.
    public class AsyncSendMessageInConversation extends AsyncTask<Void, Void, Integer> {
        ConversationActivity activity;
        String msgText;
        Conversation conversation;
        User loggedIn;
        RelativeLayout loadingBar;
        EditText textToSend;
        Message newMessage;
        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(activity);

        public AsyncSendMessageInConversation(String msgText, Conversation conversation, ConversationActivity activity,
                                              User loggedIn) {
            this.activity = activity;
            this.msgText = msgText;
            this.conversation = conversation;
            this.loggedIn = loggedIn;

        }

        @Override
        protected Integer doInBackground(Void... params) {
            // posli spravu server->ais, uloz do DB
            BackEndCommunicator communicator = new BackEndCommunicator();
            communicator.sendMessage(loggedIn.getLoginName(),loggedIn.getCookie(),conversation.getParticipant().getEmail(),msgText);

            newMessage = new Message();
            newMessage.setContent(msgText);
            newMessage.setReciever(conversation.getParticipant());
            newMessage.setSender(loggedIn);
            newMessage.setDeliveryTime(new SimpleDateFormat(dateFormat).format(new Date()));

            conversation.addMessage(newMessage);

            return 0;
        }

        // zobraz loading bar
        @Override
        public void onPreExecute () {
            loadingBar = (RelativeLayout) activity.findViewById(R.id.loadingPanel);
            textToSend = (EditText) activity.findViewById(R.id.textToSend);

            textToSend.setText("");
            loadingBar.setVisibility(View.VISIBLE);

        }

        // obnov GUI
        @Override
        public void onPostExecute(Integer errorCode) {
            ListView convList = (ListView) activity.findViewById(R.id.messagesListView);
            RecentMessagesAdapter adapter = new RecentMessagesAdapter(activity,conversation.getMessages(),conversation.getParticipant());
            convList.setAdapter(adapter);
            activity.setMyConversation(conversation);
            loadingBar.setVisibility(View.INVISIBLE);

            // posli spravu mainscreenu, ze konverzacia bola upravena
            sendUpdateNotification(broadcaster);
        }
    }

    public void sendUpdateNotification(LocalBroadcastManager broadcaster) {
        Intent i = new Intent("Result");
        i.putExtra("Update", "1");
        broadcaster.sendBroadcast(i);
    }
}
