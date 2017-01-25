package com.example.cibi.messenger.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;

import com.example.cibi.messenger.GUI.ConversationActivity;
import com.example.cibi.messenger.GUI.MainScreen;
import com.example.cibi.messenger.GUI.MyFragmentPagerAdapter;
import com.example.cibi.messenger.GUI.NewMessageFragment;
import com.example.cibi.messenger.GUI.RecentConversationsFragment;
import com.example.cibi.messenger.GUI.RecentMessagesAdapter;
import com.example.cibi.messenger.Model.Conversation;
import com.example.cibi.messenger.Model.User;
import com.example.cibi.messenger.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


// Stara sa o naplnenie udajov do GUI.
public class ContentFeeder {

    public void showConversationContent (ConversationActivity conversationActivity) {
        ListView convList = (ListView) conversationActivity.findViewById(R.id.messagesListView);
        Intent i = conversationActivity.getIntent();
        Conversation c = (Conversation) i.getSerializableExtra("Conversation");
        User loggedIn = (User) i.getSerializableExtra("User");

        RecentMessagesAdapter adapter = new RecentMessagesAdapter(conversationActivity,c.getMessages(),c.getParticipant());
        convList.setAdapter(adapter);
        conversationActivity.setMyConversation(c);
        conversationActivity.setMyUser(loggedIn);
        conversationActivity.setFragment((RecentConversationsFragment) i.getSerializableExtra("Fragment"));
    }

    public void initTabHost(MainScreen mainScreenActivity) {
        Properties prop = new Properties();
        try {
            prop.load(mainScreenActivity.getAssets().open("conf.properties"));
        }
        catch (IOException e) {
            Log.e("MyLog","Properties problem",e);
        }
        int tabHostWidth1 = Integer.parseInt(prop.getProperty("tabHostWidth1"));
        int tabHostWidth2 = Integer.parseInt(prop.getProperty("tabHostWidth2"));

        TabHost tabHost =(TabHost) mainScreenActivity.findViewById(R.id.tabHost);
        tabHost.setup();
        tabHost.getTabWidget().setDividerDrawable(android.R.drawable.divider_horizontal_dark);

        String[] tabNames = {"","",""};

        for (int i = 0; i < tabNames.length; i++) {
            TabHost.TabSpec tabSpec;

            tabSpec = tabHost.newTabSpec(tabNames[i]);
            tabSpec.setIndicator(tabNames[i]);
            tabSpec.setContent(new FakeContent(mainScreenActivity));

            tabHost.addTab(tabSpec);
            ImageView tabImageView = (ImageView) tabHost.getTabWidget().getChildTabViewAt(i).findViewById(android.R.id.icon);
            tabImageView.setVisibility(View.VISIBLE);
            if (i == 0) {tabImageView.setImageResource(R.drawable.ic_menu_start_conversation);}
            if (i == 1) {tabImageView.setImageResource(R.drawable.ic_menu_compose);}
            if (i == 2) {tabImageView.setImageResource(R.drawable.ic_menu_login);}
        }

        tabHost.getTabWidget().getChildAt(0).getLayoutParams().width =
                (int)convertPixelsToDp(tabHostWidth1,mainScreenActivity);
        tabHost.getTabWidget().getChildAt(1).getLayoutParams().width =
                (int)convertPixelsToDp(tabHostWidth2,mainScreenActivity);


        tabHost.setOnTabChangedListener(mainScreenActivity);

        // Change background
        for(int i=0; i < tabHost.getTabWidget().getChildCount(); i++) {
            tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.my_tab_selector);
        }
        mainScreenActivity.setTabHost(tabHost);
    }

    public void initViewPager(MainScreen mainScreenActivity) {
        Intent i = mainScreenActivity.getIntent();
        User loggedIn = (User) i.getSerializableExtra("User");
        Log.d("MyLog","user cookieVal TEST: " + loggedIn.getCookie());
        new LoadFragments(mainScreenActivity).execute();
    }

    class FakeContent implements TabHost.TabContentFactory {
        Context context;

        FakeContent (Context context) {
            this.context = context;
        }
        @Override
        public View createTabContent(String tag) {
            View fakeView = new View(context);
            fakeView.setMinimumHeight(0);
            fakeView.setMinimumWidth(0);
            return fakeView;
        }
    }

    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public class LoadFragments extends AsyncTask<Void, List<Fragment>, List<Fragment>> {
        MainScreen screen;
        ViewPager viewPager;
        RelativeLayout loadingBar;

        public LoadFragments(MainScreen screen) {
            this.screen = screen;
        }

        @Override
        protected List<Fragment> doInBackground(Void... params) {
            List<Fragment> fragmentList = new ArrayList<>();
            RecentConversationsFragment fragment = new RecentConversationsFragment();
            screen.setFragment(fragment);
            fragmentList.add(fragment);
            fragmentList.add(new NewMessageFragment());

            Log.d("MyLog", "viewpager initialized");
            return fragmentList;
        }
        @Override
        public void onPreExecute() {
            viewPager = (ViewPager) screen.findViewById(R.id.view_pager);
            loadingBar = (RelativeLayout) screen.findViewById(R.id.loadingPanel);
            loadingBar.setVisibility(View.VISIBLE);

            // housekeeping
            screen.setLoadingBar(loadingBar);
            screen.setViewPager(viewPager);
        }
        @Override
        public void onPostExecute(List<Fragment> fragmentList) {
            MyFragmentPagerAdapter pagerAdapter = new MyFragmentPagerAdapter(screen.getSupportFragmentManager(),fragmentList);
            viewPager.setAdapter(pagerAdapter);
            viewPager.addOnPageChangeListener(screen);
            loadingBar.setVisibility(View.GONE);

            // housekeeping
            screen.setLoadingBar(loadingBar);
            screen.setViewPager(viewPager);
            Log.d("MyLog", "visibilities changed to normal");
        }
    }
}
