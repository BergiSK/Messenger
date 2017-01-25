package com.example.Cibi.myapplication.backend.Controller;

import com.example.Cibi.myapplication.backend.Model.ConversationSS;
import com.example.Cibi.myapplication.backend.Model.MessageSS;
import com.example.Cibi.myapplication.backend.Model.UserSS;
import com.google.api.server.spi.response.ConflictException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.cmd.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class DatabaseCommunicator {
    private static final Logger log = Logger.getLogger(DatabaseCommunicator.class.getName());

    public List<ConversationSS> readConvesationsDB (String cursorString, Integer count, String name) {
        log.fine("DatabaseCommunicator: readConvesationsDB()");
        Query<ConversationSS> query = OfyService.ofy().load().type(ConversationSS.class).order("-messages.deliveryTime").filter("ownerName", name);
        if (count != null) query.limit(count);
        if (cursorString != null && cursorString != "") {
            query = query.startAt(Cursor.fromWebSafeString(cursorString));
        }

        List<ConversationSS> records = new ArrayList<>();
        QueryResultIterator<ConversationSS> iterator = query.iterator();
        int num = 0;
        while (iterator.hasNext()) {
            records.add(iterator.next());
            if (count != null) {
                num++;
                if (num == count) break;
            }
        }

        return records;
    }
    
    public int insertConversation(ConversationSS conversation, String ownerName)  {
        log.fine("DatabaseCommunicator: insertConversation()");
        //If if is not null, then check if it exists. If yes, throw an Exception
        //that it is already present

        ConversationSS fromDB = findRecord(conversation.getParticipant().getEmail(),ownerName);
        if (fromDB != null) {

            // check the messages
            int added = 0;
            for (MessageSS curMes: conversation.getMessages()) {
                boolean toAdd = true;
                for (MessageSS curMesDB: fromDB.getMessages()) {
                    // ak lisi v link suffixe - treba inak riesit pre odoslane mnou
                    if (curMes.getLinkSuffix().compareTo(curMesDB.getLinkSuffix()) == 0) {
                        toAdd = false;
                    }
                }


                if (toAdd == true) {
                    added++;
                    fromDB.addMessage(curMes);
                }
            }

            if (added == 0) {
                return 0;
            }
            else {
                OfyService.ofy().save().entity(fromDB);
                return added;
            }
            //throw new ConflictException("Object already exists");
        }

        //Since our @Id field is a Long, Objectify will generate a unique value for us
        //when we use put
        OfyService.ofy().save().entity(conversation);
        return 1;
    }

    public void insertSentMessage (String ownerName, String msgText, String recieverEmail) {
        log.fine("DatabaseCommunicator: insertSentMessage()");
        // pohladaj ci uz existuje konverzacia s danym clovekom
        ConversationSS fromDB = findRecord(recieverEmail,ownerName);

        MessageSS sentMsg = new MessageSS();
        UserSS sender = new UserSS();
        UserSS reciever = new UserSS();

        sender.setName(ownerName);
        sender.setEmail(ownerName);
        sender.setId(OfyService.factory().allocateId(UserSS.class).getId());
        reciever.setEmail(recieverEmail);
        reciever.setName(recieverEmail);
        reciever.setId(OfyService.factory().allocateId(UserSS.class).getId());

        sentMsg.setContent(msgText);
        sentMsg.setDeliveryTime(new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date()));
        sentMsg.setSender(sender);
        sentMsg.setReciever(reciever);
        sentMsg.setId(OfyService.factory().allocateId(MessageSS.class).getId());
        // ak ano vloz do nej odoslanu spravu, inak vytvor novu konverzaciu
        if (fromDB != null) {
            fromDB.addMessage(sentMsg);
            OfyService.ofy().save().entity(fromDB);
        }
        else {
            ConversationSS conversationSS = new ConversationSS();
            conversationSS.addMessage(sentMsg);
            conversationSS.setOwnerName(ownerName);
            conversationSS.setParticipant(reciever);
            OfyService.ofy().save().entity(conversationSS);
        }

    }

    //Private method to retrieve a Conversation record by participant name
    private ConversationSS findRecord(String participantMail, String ownerName) {
        log.fine("DatabaseCommunicator: findRecord()");
        return OfyService.ofy().load().type(ConversationSS.class).filter("ownerName",ownerName)
                .filter("participant.email", participantMail).first().now();
        //or return ofy().load().type(Quote.class).filter("id",id).first.now();
    }

}
