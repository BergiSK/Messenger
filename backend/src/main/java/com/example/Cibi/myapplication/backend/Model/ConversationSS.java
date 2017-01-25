package com.example.Cibi.myapplication.backend.Model;

import java.util.ArrayList;
import java.util.List;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;



@Entity
public class ConversationSS {
    public ConversationSS() {
        messages = new ArrayList<>();
    }

    // index znaci, ze sa podla daneho atributu da datastore prehladavat
    @Index
    private List<MessageSS> messages;

    @Index
    private String ownerName;

    @Index
    private UserSS participant;

    @Id
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserSS getParticipant() {
        return participant;
    }

    public void setParticipant(UserSS participant) {
        this.participant = participant;
    }

    public void setMessages(ArrayList<MessageSS> messages) {
        this.messages = messages;
    }

    public List<MessageSS> getMessages() {
        return messages;
    }

    public void addMessage (MessageSS msg) {
        this.messages.add(msg);
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
