package com.example.cibi.messenger.GUI;

import android.content.Intent;
import android.net.Uri;
import android.os.*;
import android.os.Process;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;


import com.example.cibi.messenger.Controller.EventHandler;
import com.example.cibi.messenger.Model.User;
import com.example.cibi.messenger.R;

// Zakladna obrazovka pozostavajuca z 3 tabov a nim prisluchajucich fragmentov umiestnenych do viewpageru.
public class MainScreen extends AppCompatActivity implements ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener,
        RecentConversationsFragment.OnFragmentInteractionListener,NewMessageFragment.OnMessageFragmentInteractionListener {

    private ViewPager viewPager;
    private TabHost tabHost;
    private RelativeLayout loadingBar;
    private User loggedIn;
    private RecentConversationsFragment fragment;

    EventHandler eventHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MyLog", "MainScreen creating");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        viewPager = null;
        loggedIn = null;

        Intent i = this.getIntent();
        loggedIn = (User) i.getSerializableExtra("User");

        // zobraz obsah
        eventHandler = new EventHandler();
        eventHandler.showMainScreenContent(this);

        Log.d("MyLog", "MainScreen created");
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateConversationsFragment();
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    public TabHost getTabHost() {
        return tabHost;
    }

    public void setTabHost(TabHost tabHost) {
        this.tabHost = tabHost;
    }

    public RelativeLayout getLoadingBar() {
        return loadingBar;
    }

    public void setLoadingBar(RelativeLayout loadingBar) {
        this.loadingBar = loadingBar;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * Zabezpecuje odoslanie spravy na pozadovany ais email z fragmentu NewMessage.
     * @param reciever - prijemca spravy
     * @param msgText - text spravy
     * @param fragment - fragment kde sa akcia vykonala
     */
    @Override
    public void onMsgFragmentInteraction(String reciever,String msgText, NewMessageFragment fragment) {
        eventHandler.sendMessageStranger(msgText, reciever, this, fragment, this.fragment, loggedIn);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageSelected(int position) {
        tabHost.setCurrentTab(position);
    }

    // Zabezpecuje prehodenie obsahu pri kliknuti na prislusny tab. Tab 2 - logout.
    @Override
    public void onTabChanged(String tabId) {
        int selectedItem = tabHost.getCurrentTab();

        if (selectedItem != 2) {
            viewPager.setCurrentItem(selectedItem);
        } else {
            // LOGOUT
            // zabije beziace sluzby
            Process.killProcess(Process.myPid());
            startActivity(new Intent(MainScreen.this, LoginActivity.class));
        }
    }

    public RecentConversationsFragment getFragment() {
        return fragment;
    }

    public void setFragment(RecentConversationsFragment fragment) {
        this.fragment = fragment;
    }

    // Metoda zodpoveda za update obsahu fragmentu konverzacii z aktualneho listu konverzacii.
    public void updateConversationsFragment () {
        if (fragment != null && fragment.getView() != null) {

            ListView convList = (ListView) fragment.getView().findViewById(R.id.listView);
            RecentConversationsAdapter conversationsAdapter = new RecentConversationsAdapter(
                    fragment.getActivity(), fragment.getConversationList());
            convList.setAdapter(conversationsAdapter);
            Log.d("MyLog", "Fragment updated.");
        }
        Log.d("MyLog", "Update run.");
    }
}


