package com.example.cibi.messenger.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.FrameLayout;

import com.example.cibi.messenger.GUI.ConversationActivity;
import com.example.cibi.messenger.GUI.MainScreen;
import com.example.cibi.messenger.GUI.NewMessageFragment;
import com.example.cibi.messenger.GUI.RecentConversationsFragment;
import com.example.cibi.messenger.Model.Conversation;
import com.example.cibi.messenger.Model.User;

import java.util.List;


// Hlavna riadiaca trieda posuvajuca GUI eventy spracuvajucim triedam.
public class EventHandler {

    // Stara sa o zobrazenie obsahu hlavnej obrazovky aplikacie.
    public void showMainScreenContent(MainScreen mainScreenActivity) {
        ContentFeeder feeder = new ContentFeeder();
        feeder.initTabHost(mainScreenActivity);
        feeder.initViewPager(mainScreenActivity);
    }

    // Zobrazi obsah konverzacie.
    public void showConversationContent (ConversationActivity conversationActivity) {
        ContentFeeder contentFeeder = new ContentFeeder();
        contentFeeder.showConversationContent(conversationActivity);
    }

    // Inicializuje zoznam koverzacii pri zapnuti aplikacie.
    public void initRecentConversations (FrameLayout frameLayout, List<Conversation> conversationList,
                                         RecentConversationsFragment recentConversationsFragmentFragment, User loggedIn) {
        // fill the ListView with conversations
        if (conversationList !=null) {
            if (conversationList.size() == 0) {
                this.getRecentConversations(frameLayout, loggedIn, recentConversationsFragmentFragment);
            }
        }
    }

    // Stara sa o update zoznamu konverzacii.
    public void updateRecentConversations (FrameLayout frameLayout,
                                         RecentConversationsFragment recentConversationsFragmentFragment, User loggedIn) {
        this.getRecentConversations(frameLayout, loggedIn, recentConversationsFragmentFragment);
    }

    // Stara sa o spustenie obrazovky konkretnej konverzacie.
    public void openSpecificConversation (List<Conversation> conversationList, int position, final Activity convActivity,
                                          User loggedIn) {
        Intent i = new Intent(convActivity, ConversationActivity.class);
        i.putExtra("Conversation", conversationList.get(position));
        i.putExtra("User",loggedIn);

        convActivity.startActivity(i);
    }

    // Stara sa o odoslanie spravy v konverzacii.
    public void sendMessage (String msgText, Conversation conversation, ConversationActivity activity,
                             User loggedIn) {
        MessageSendController controller = new MessageSendController(activity);
        controller.sendMessageInConversation(msgText, conversation, activity,loggedIn);

        Log.d("MyLog", "Message send to: " + conversation.getParticipant().getName() +
                " on address: " + conversation.getParticipant().getEmail() + " with text: " +
                msgText);
    }

    // Stara sa o odoslanie spravy v samostatnom Tabe.
    public void sendMessageStranger (String msgText, String recieverMail, MainScreen activity, NewMessageFragment messageFragment,
                                     RecentConversationsFragment conversationsFragment, User loggedIn) {
        MessageSendController controller = new MessageSendController(activity);
        controller.sendMessageToStranger(msgText, recieverMail, activity, messageFragment, conversationsFragment,loggedIn);
    }

    // Stara sa o prihlasenie pouzivatela.
    public void login (final String name, final String password, final Activity loginActivity) {
        LoginController loginController = new LoginController();
        loginController.login(name, password, loginActivity);
    }

    // Obnovi aktualny zoznam konverzacii.
    public void getRecentConversations(FrameLayout frameLayout,User loggedIn, RecentConversationsFragment conversations) {
        ConversationHandler handler = new ConversationHandler();
        handler.getRecentConversations(frameLayout,loggedIn,conversations);
    }


}
