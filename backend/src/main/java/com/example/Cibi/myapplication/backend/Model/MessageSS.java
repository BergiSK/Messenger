package com.example.Cibi.myapplication.backend.Model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class MessageSS {

    @Id
    private Long id;

    private UserSS reciever;

    private UserSS sender;

    private String topic;

    @Index
    private String deliveryTime;

    private boolean opened;

    private String content;

    private String linkSuffix;

    public void setSenderEmail (String email) {
        this.sender.setEmail(email);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserSS getReciever() {
        return reciever;
    }

    public void setReciever(UserSS reciever) {
        this.reciever = reciever;
    }

    public UserSS getSender() {
        return sender;
    }

    public void setSender(UserSS sender) {
        this.sender = sender;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public String getLinkSuffix() {
        return linkSuffix;
    }

    public void setLinkSuffix(String linkSuffix) {
        this.linkSuffix = linkSuffix;
    }
}
