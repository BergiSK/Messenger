package com.example.cibi.messenger.Controller;

import android.util.Log;

import com.example.cibi.myapplication.backend.myApi.model.ConversationSS;
import com.example.cibi.myapplication.backend.myApi.model.MessageSS;
import com.example.cibi.myapplication.backend.myApi.model.UserSS;
import com.example.cibi.messenger.Model.Message;
import com.example.cibi.messenger.Model.Conversation;
import com.example.cibi.messenger.Model.User;

import java.util.ArrayList;
import java.util.List;

// Trieda sluzi na jednoduche skopirovanie objektu zo serverside modelu do clientside modelu. Objekty ziskaju
// vlastnost serializable a mozu tak byt posuvane medzi aktivitami.
public class Transformer {

    public List<Conversation> transform (List<ConversationSS> input) {
        List<Conversation> output = new ArrayList<>();
        if (input != null) {
            Log.d("MyLog.Transformer","Found conversations:"+input.size());
            for (ConversationSS curConv : input) {
                Conversation c = new Conversation();
                for (MessageSS curMes : curConv.getMessages()) {
                    Message message = new Message();
                    message.setContent(curMes.getContent());

                    User sender = new User();
                    User reciever = new User();
                    sender.setName(curMes.getSender().getName());
                    sender.setEmail(curMes.getSender().getEmail());
                    sender.setCookie(curMes.getSender().getCookie());
                    sender.setLoginName(curMes.getSender().getLoginName());

                    reciever.setName(curMes.getReciever().getName());
                    reciever.setEmail(curMes.getReciever().getEmail());
                    reciever.setCookie(curMes.getReciever().getCookie());
                    reciever.setLoginName(curMes.getReciever().getLoginName());

                    message.setSender(sender);
                    message.setReciever(reciever);
                    message.setDeliveryTime(curMes.getDeliveryTime());
                    message.setTopic(curMes.getTopic());
                    message.setLinkSuffix(curMes.getLinkSuffix());
                    c.addMessage(message);
                }
                User participant = new User();
                participant.setName(curConv.getParticipant().getName());
                participant.setEmail(curConv.getParticipant().getEmail());
                participant.setCookie(curConv.getParticipant().getCookie());
                participant.setLoginName(curConv.getParticipant().getLoginName());

                c.setParticipant(participant);
                output.add(c);
            }
        }
        return output;
    }

}
