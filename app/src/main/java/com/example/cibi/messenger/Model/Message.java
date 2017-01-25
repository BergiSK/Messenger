package com.example.cibi.messenger.Model;

import java.io.Serializable;

/**
 * Created by Cibi on 28.3.2016.
 */
public class Message implements Serializable{

    private Integer id;

    private User reciever;

    private User sender;

    private String topic;

    private String deliveryTime;

    private boolean opened;

    private String content;

    private String linkSuffix;

    public void setSenderEmail (String email) {
        this.sender.setEmail(email);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getReciever() {
        return reciever;
    }

    public void setReciever(User reciever) {
        this.reciever = reciever;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
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
