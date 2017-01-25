package com.example.cibi.messenger.Model;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cibi on 28.3.2016.
 */
public class Conversation implements Serializable{
    private ArrayList<Message> messages;

    public Conversation () {
        messages = new ArrayList<>();
    }

    // ten druhy okrem lognuteho
    private User participant;

    public User getParticipant() {
        return participant;
    }

    public void setParticipant(User participant) {
        this.participant = participant;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public ArrayList getMessages() {
        return messages;
    }

    public void addMessage (Message msg) {
        this.messages.add(msg);
    }


    public String getLastMessageText () {
        return messages.get(messages.size()-1).getContent();
    }

    public String getLastMessageDate () { return messages.get(messages.size()-1).getDeliveryTime();}
}
