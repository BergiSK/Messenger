package com.example.cibi.messenger.GUI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.example.cibi.messenger.Controller.EventHandler;
import com.example.cibi.messenger.Model.Conversation;
import com.example.cibi.messenger.Model.User;
import com.example.cibi.messenger.R;

// Aktivita zachytava konverzaciu medzi dvomi konkretnymi osobami.
public class ConversationActivity extends AppCompatActivity {

    EventHandler eventHandler = new EventHandler();
    private Conversation myConversation;
    private User loggedIn;
    private RecentConversationsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        // postaraj sa o distribuciu dat do GUI
        eventHandler.showConversationContent(this);

        Button sendMsg = (Button) findViewById(R.id.sendMsgInConversation);
        final EditText textToSendField = (EditText) findViewById(R.id.textToSend);

        // v pripade odoslania novej spravy nechaj handler spracovat poziadavku
        sendMsg.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String textToSend = textToSendField.getText().toString();
                        eventHandler.sendMessage(textToSend,myConversation,ConversationActivity.this,loggedIn);
                    }
                }
        );
    }

    // get + set
    public Conversation getMyConversation() {
        return myConversation;
    }

    public void setMyConversation(Conversation myConversation) {
        this.myConversation = myConversation;
    }

    public void setMyUser(User loggedIn) {
        this.loggedIn = loggedIn;
    }

    public RecentConversationsFragment getFragment() {
        return fragment;
    }

    public void setFragment(RecentConversationsFragment fragment) {
        this.fragment = fragment;
    }
}
