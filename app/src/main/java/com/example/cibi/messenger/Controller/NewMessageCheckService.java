package com.example.cibi.messenger.Controller;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.cibi.messenger.Model.User;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Handler;

public class NewMessageCheckService extends Service {

    private final IBinder binder = new MyLocalBinder();
    private LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(this);
    private User loggedIn;
    private int refreshMilis;

    public NewMessageCheckService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent i) {
        loggedIn = (User) i.getSerializableExtra("User");
        refreshMilis = i.getIntExtra("refreshMilis",0);
        checkForMessages();
        return binder;
    }

    // Funkcia beziaca na samostatnom vlakne, z backendu vola kontrolu aktualnej DB voci AISu.
    // Kontroluje sa v intervale vyjadrenom v premennej refreshMilis, ktoru je mozne upravit v konfiguracnom subore.
    public void checkForMessages () {
        final BackEndCommunicator communicator = new BackEndCommunicator();

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while(true) {
                        int updated = communicator.updateDBfromAIS(loggedIn);
                        Log.d("MyLog", "Checking for messages:"+updated);

                        if (updated != 0) {
                            sendUpdateNotification();
                        }
                        sleep(refreshMilis);
                    }
                } catch (InterruptedException e) {
                    Log.e("MyLog", "Message checking thread interrupted", e);
                }
            }
        };

        thread.start();
    }

    public void sendUpdateNotification() {
        Intent i = new Intent("Result");
        i.putExtra("Update", "1");
        broadcaster.sendBroadcast(i);
    }


    public class MyLocalBinder extends Binder {
        public NewMessageCheckService getService() {
            return NewMessageCheckService.this;
        }
    }
}
