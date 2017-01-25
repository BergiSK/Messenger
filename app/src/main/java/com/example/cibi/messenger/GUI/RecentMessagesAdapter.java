package com.example.cibi.messenger.GUI;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.cibi.messenger.Model.Conversation;
import com.example.cibi.messenger.Model.Message;
import com.example.cibi.messenger.Model.User;
import com.example.cibi.messenger.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

// Trieda parsuje list sprav do GUI.
public class RecentMessagesAdapter extends ArrayAdapter<Message> {
    User participant;
    String participantName = "";
    String msgTextColor;
    String msgDateColor;

    public RecentMessagesAdapter(Context context, ArrayList<Message> messageArrayList, User participant) {
        super(context, 0, messageArrayList);
        this.participant = participant;
        this.participantName.concat(participant.getName());

        Properties prop = new Properties();
        try {
            prop.load(context.getAssets().open("conf.properties"));
        }
        catch (IOException e) {
            Log.e("MyLog","Properties problem",e);
        }
        msgTextColor = prop.getProperty("msgTextColor");
        msgDateColor = prop.getProperty("msgDateColor");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Message message = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view - vypnute - view sa meni pre rozne riadky
        //if (convertView == null) {

        if (participant.getName().contentEquals(message.getSender().getName()) == true ) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_message_participant, parent, false);
        }
        else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_message_me, parent, false);
        }

        //}
        // Lookup view for data population
        TextView MsgText = (TextView) convertView.findViewById(R.id.msgTextBubble);
        TextView MsgDate = (TextView) convertView.findViewById(R.id.msgDateBubble);

        MsgText.setTextColor(Color.parseColor(msgTextColor));
        MsgDate.setTextColor(Color.parseColor(msgDateColor));
        // Populate the data into the template view using the data object
        MsgText.setText(message.getContent());
        MsgDate.setText(message.getDeliveryTime());

        // Return the completed view to render on screen
        return convertView;
    }
}