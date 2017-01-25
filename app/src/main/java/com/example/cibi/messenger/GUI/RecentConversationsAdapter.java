package com.example.cibi.messenger.GUI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.cibi.messenger.Model.Conversation;
import com.example.cibi.messenger.R;

import java.util.ArrayList;

// Trieda sluzi na naparsovanie listu konverzacii do GUI.
public class RecentConversationsAdapter extends ArrayAdapter<Conversation> {
    public RecentConversationsAdapter(Context context, ArrayList<Conversation> conversationArrayList) {
        super(context, 0, conversationArrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Conversation conversation = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_conversation, parent, false);
        }
        // Lookup view for data population
        TextView convPerson = (TextView) convertView.findViewById(R.id.convPerson);
        TextView convMsgText = (TextView) convertView.findViewById(R.id.convMsgText);
        TextView convDate = (TextView) convertView.findViewById(R.id.convDate);

        // Populate the data into the template view using the data object
        convPerson.setText(conversation.getParticipant().getName());
        convMsgText.setText(conversation.getLastMessageText());
        convDate.setText(conversation.getLastMessageDate());
        // Return the completed view to render on screen
        return convertView;
    }
}