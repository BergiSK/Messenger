package com.example.Cibi.myapplication.backend.Controller;


import com.example.Cibi.myapplication.backend.Model.ConversationSS;
import com.example.Cibi.myapplication.backend.Model.MessageSS;
import com.example.Cibi.myapplication.backend.Model.ResponseCode;
import com.example.Cibi.myapplication.backend.Model.UserSS;
import com.google.api.server.spi.response.ConflictException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConversationHandler {
    private static final Logger log = Logger.getLogger(ConversationHandler.class.getName());
    AISCommunicator communicator = new AISCommunicator();
    AISParser parser = new AISParser();

    public List<ConversationSS> updateConversations(List<MessageSS> recentMessages) {
        log.fine("ConversationHandler: updateConversations()");
        List<ConversationSS> recentConversations = new ArrayList<>();

        for (MessageSS curMes : recentMessages) {
            int belong = 0;
            for (ConversationSS curConv : recentConversations) {
                if (curConv.getParticipant().getName().compareToIgnoreCase(curMes.getSender().getName()) == 0) {
                    belong = 1;
                    curConv.addMessage(curMes);
                    break;
                }
            }

            if (belong == 0) {
                ConversationSS c = new ConversationSS();
                c.setParticipant(curMes.getSender());
                c.setOwnerName(curMes.getReciever().getLoginName());
                c.addMessage(curMes);
                recentConversations.add(c);
            }
        }


        return recentConversations;
    }

    public List<MessageSS> getMessageContents(List<MessageSS> recentMessages, String cookieVal) {
        log.fine("ConversationHandler: updateConversations()");
        for (MessageSS curMes : recentMessages) {
            try {
                String html = communicator.getContent(curMes.getLinkSuffix(), cookieVal);
                curMes.setContent(parser.getMessageText(html));
                curMes.setSenderEmail(parser.getMessageSenderEmail(html));

            } catch (Exception e) {
                log.log(Level.WARNING,"Could not get message contents.",e);
            }
        }

        return recentMessages;
    }

    public List<ConversationSS> getRecentConversations(UserSS loggedIn) {
        log.fine("ConversationHandler: getRecentConversations()");
        List<MessageSS> msgList = parser.getMessageList(communicator.getMessages(loggedIn.getCookie()), loggedIn);
        List<MessageSS> msgListContents = this.getMessageContents(msgList, loggedIn.getCookie());

        return this.updateConversations(msgListContents);
    }

    public ResponseCode checkUpdates (String loginName, DatabaseCommunicator communicator, List<ConversationSS> conversations) {
        log.fine("ConversationHandler: checkUpdates()");
        int updateNum = 0;
        for (ConversationSS curConv: conversations) {
            updateNum += communicator.insertConversation(curConv, loginName);
        }

        ResponseCode code = new ResponseCode();
        code.setCode(updateNum);

        return code;
    }
}
