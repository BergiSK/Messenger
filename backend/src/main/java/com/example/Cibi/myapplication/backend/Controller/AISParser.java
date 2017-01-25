package com.example.Cibi.myapplication.backend.Controller;

import com.example.Cibi.myapplication.backend.Model.MessageSS;
import com.example.Cibi.myapplication.backend.Model.UserSS;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class AISParser {

    public List<MessageSS> getMessageList (String html, UserSS loggedIn) {
        Document doc = Jsoup.parse(html, "ISO-8859-2");

        Elements navigationAndMessages = doc.select("form > table > tbody > tr > td");
        Elements messageRows = navigationAndMessages.get(1).select("table#tmtab_1 tbody tr");

        List<MessageSS> messages = new ArrayList<>();

        for(Element curElm : messageRows) {
            Elements columns = curElm.select("td");

            String senderName = columns.get(3).text().replace("Ing. ","").replace("PhD.","").replace(",","");
            senderName = senderName.substring(0, Math.min(senderName.length(),20));

            String topic = columns.get(4).select("a").text();
            String deliveryTime = columns.get(5).select("small").text();
            String sufixUrl = columns.get(4).select("a").attr("href").toString().
                    substring(11);

            MessageSS m = new MessageSS();
            UserSS sender = new UserSS();
            sender.setName(senderName);
            sender.setId(OfyService.factory().allocateId(UserSS.class).getId());
            loggedIn.setId(OfyService.factory().allocateId(UserSS.class).getId());

            m.setSender(sender);
            m.setReciever(loggedIn);
            m.setDeliveryTime(deliveryTime);
            m.setTopic(topic);
            m.setLinkSuffix(sufixUrl);
            m.setId(OfyService.factory().allocateId(MessageSS.class).getId());
            messages.add(m);
        }

        return messages;
    }

    public String getMessageText (String html) {
        Document doc = Jsoup.parse(html);

        Elements messageRows = doc.select("form > table > tbody > tr > td table tbody tr");
        String messageContent = messageRows.get(messageRows.size() - 1).text();

        return messageContent;
    }

    public String getMessageSenderEmail (String html) {
        Document doc = Jsoup.parse(html);

        Elements messageRows = doc.select("form > table > tbody > tr > td table tbody tr");
        String rawText = messageRows.get(0).select("a").text();

        // kvoli noreply@ubytovanie.stuba.sk
        if (rawText.contains("<")) {
            String email = rawText.split("<")[1].replace(">", "");
            return email;
        }
        else {
            return rawText;
        }
    }

    public String getSerializaceFolderNum (String html) {
        Document doc = Jsoup.parse(html);
        Elements serializace = doc.select("input[name=serializace]");
        Elements folderSave = doc.select("select[name=ulozit_do_sl] option");

        return serializace.val() + "MYsplit" + folderSave.get(1).val();
    }

}
