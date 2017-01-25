package com.example.cibi.messenger.Controller;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import javax.net.ssl.HttpsURLConnection;


// Stara sa o komunikaciu s AISom za ucelom prihlasenia.
// Logika tejto triedy by v idealnom pripade bola umiestnena na server, no ten zakazuje pouzite tried ako CookieManager
// a ani ziadnym inym sposobom cookie z na nom vykonanej response nesli prebrat.
public class AISCommunicator {

    private static final String URL = "https://is.stuba.sk/system/login.pl";
    private static final String MAILBOX_URL_SUFFIX = "%2Fauth%2Fposta%2Fslozka.pl?";

    public String login (String password, String name) {
        String urlParameters = "destination=" + MAILBOX_URL_SUFFIX + "&credential_0=" + name +
                "&credential_1=" + password + "&login=Prihl%E1si%BB+sa";

        CookieManager manager = new CookieManager();
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);

        // *****************  login  ***********************
        URL url = null;

        try {
            url = new URL(URL);
        } catch (MalformedURLException e) {
            Log.e("MyLog", "Wrong login URL", e);
        }
        HttpURLConnection con = null;

        try {
            con = (HttpURLConnection)url.openConnection();
        } catch (IOException e) {
            Log.e("MyLog", "Could not establish connection", e);
        }

        appendRequestHeader(con);

        //Send post request
        con.setDoOutput(true);
        DataOutputStream wr = null;

        try {
            wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);

            wr.flush();
            wr.close();


            con.connect();
            con.getInputStream();

        } catch (IOException e) {
            Log.e("MyLog", "Could not get outputstream", e);
        }



        if (manager.getCookieStore().getCookies().size() != 0) {
            Log.d("MyLog",manager.getCookieStore().getCookies().get(0).getName()
                    + ":" + manager.getCookieStore().getCookies().get(0).getValue());
            return manager.getCookieStore().getCookies().get(0).getValue();
        }
        else {
            return null;
        }
    }

    /**
     * Doplni hlavicky HTTP requestu. Tymto sa v podstate Java program napodobnuje funkcionalitu browsera.
     * @param con
     */
    private void appendRequestHeader(HttpURLConnection con) {
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("Accept-Language", "sk-SK");
        con.setRequestProperty("Accept", "text/html, application/xhtml+xml, */*");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");
        con.setRequestProperty("DNT", "1");
        con.setRequestProperty("Pragma", "no-cache");
        con.setRequestProperty("Cache-Control", "no-cache");
        //con.setRequestProperty("Referer", "https://is.stuba.sk/auth/nucitel/kontakty.pl?predmet=282288");
        con.setRequestProperty("Accept-Encoding", "gzip, deflate");
    }


}
