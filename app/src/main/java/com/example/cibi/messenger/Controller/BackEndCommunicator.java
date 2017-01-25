package com.example.cibi.messenger.Controller;

import android.util.Log;

import com.example.cibi.messenger.Model.User;
import com.example.cibi.myapplication.backend.myApi.MyApi;
import com.example.cibi.myapplication.backend.myApi.model.ConversationSS;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.List;

// Trieda zodpovedna za volanie metod z server logiky.
public class BackEndCommunicator {
    private static MyApi myApiService = null;
    private String rootUrl = "http://10.0.2.2:8080/_ah/api/";

    public BackEndCommunicator () {

    }

    public String login (String loginName, String password) {
        String cookieVal = null;
        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl(rootUrl)
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver

            myApiService = builder.build();
        }

        try {
            cookieVal =  myApiService.login(loginName,password).execute().getCookie();
        } catch (IOException e) {
            Log.e("MyLog", "API problem", e);
        }

        return cookieVal;
    }

    public void sendMessage (String ownerName, String cookieVal, String recieverEmail, String msgText) {
        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl(rootUrl)
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver

            myApiService = builder.build();
            Log.d("MyLog", "Building API Service");
        }

        try {
            myApiService.sendMessage(cookieVal,ownerName,recieverEmail,msgText).execute();
        } catch (IOException e) {
            Log.e("MyLog", "API problem", e);
        }
    }

    public List<ConversationSS> getRecent (User loggedIn) {
        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl(rootUrl)
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver

            myApiService = builder.build();
            Log.d("MyLog", "Building API Service");
        }

        try {
            return myApiService.readConversationsDB(loggedIn.getLoginName()).execute().getItems();
        } catch (IOException e) {
            Log.e("MyLog", "API problem", e);
            return null;
        }
    }

    public int updateDBfromAIS (User loggedIn) {
        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl(rootUrl)
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver
            myApiService = builder.build();
        }

        try {
            return myApiService.updateDB(loggedIn.getLoginName(),loggedIn.getCookie()).execute().getCode();
        } catch (IOException e) {
            Log.e("MyLog", "API problem", e);
            return -1;
        }
    }
}
