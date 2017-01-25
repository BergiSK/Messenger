package com.example.Cibi.myapplication.backend.Controller;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.URL;



public class AISCommunicator {
    private static final Logger log = Logger.getLogger(AISCommunicator.class.getName());

    private static final String URL = "https://is.stuba.sk/system/login.pl";
    private static final String MSG_BOX_URL = "https://is.stuba.sk/auth/posta/slozka.pl";
    private static final String SPECIFIC_MESSAGE_URL = "https://is.stuba.sk/auth/posta/email.pl?";
    private static final String NEW_MSG_URL = "https://is.stuba.sk/auth/posta/nova_zprava.pl";
    private static final String MAILBOX_URL_SUFFIX = "%2Fauth%2Fposta%2Fslozka.pl?";

    private static final String URL_PARAMETERS_SEND_MESSAGE_1 = "zprava=1&To=";
    private static final String URL_PARAMETERS_SEND_MESSAGE_2 = "&Subject=via AISMessenger&Data=";
    private static final String URL_PARAMETERS_SEND_MESSAGE_3 = "&ulozit_do_sl=";
    private static final String URL_PARAMETERS_SEND_MESSAGE_4 = "&send=ODOSLA&#356;+SPR%C1VU&akce=schranka&serializace=";

    public String login (String password, String name) {
        log.fine("AISCommunicator: login()");
        String urlParameters = "destination=" + MAILBOX_URL_SUFFIX + "&credential_0=" + name +
                "&credential_1=" + password + "&login=Prihl%E1si%BB+sa";

        URL url = null;
        // *****************  login  ***********************
        try {
            url = new URL(URL);
        } catch (MalformedURLException e) {
            log.log(Level.SEVERE, "Malformed URL.", e);
        }

        HttpURLConnection con = null;

        try {
            con =(HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not connect to AIS.", e);
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
            log.log(Level.SEVERE, "Could not get output stream from AIS.", e);
        }

        String cookie = "default";
        return cookie;
    }

    public String getMessages (String cookieVal) {
        log.fine("AISCommunicator: getMessages()");
        URL url = null;

        try {
            url = new URL(MSG_BOX_URL);
        } catch (MalformedURLException e) {
            log.log(Level.SEVERE, "Malformed URL.", e);
        }
        HttpURLConnection con = null;

        try {
            con = (HttpURLConnection)url.openConnection();
            con.addRequestProperty("Cookie", "UISAuth=" + cookieVal);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not connect to AIS.", e);
        }

        appendRequestHeader(con);

        //Send post request
        con.setDoOutput(true);
        DataOutputStream wr = null;

        try {
            wr = new DataOutputStream(con.getOutputStream());

            wr.flush();
            wr.close();


            con.connect();
            con.getInputStream();

        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not get output stream from AIS.", e);
        }

        try {
            return printHttpsContent(con);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not convert output stream from AIS.", e);
        }

        // sem sa dostane len ak sa nepodarilo nadviazat connection
        return null;
    }

    public String getContent(String extraParams, String cookieVal)  {
        log.fine("AISCommunicator: getContent()");
        URL url = null;
        try {
            url = new URL(SPECIFIC_MESSAGE_URL);
        } catch (MalformedURLException e) {
            log.log(Level.SEVERE, "Malformed URL.", e);
        }
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection)url.openConnection();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not connect to AIS.", e);
        }
        con.addRequestProperty("Cookie", "UISAuth=" + cookieVal);
        appendRequestHeader(con);

        //Send post request
        con.setDoOutput(true);


        try {
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(extraParams);
            wr.flush();
            wr.close();

            con.connect();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not get output stream from AIS.", e);
        }


        try {
            return printHttpsContent(con);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not convert output stream from AIS.", e);
            return null;
        }
    }

    public String getSerializationFolderNum(String cookieVal)  {
        log.fine("AISCommunicator: getSerializationFolderNum()");
        URL url = null;

        try {
            url = new URL(NEW_MSG_URL);
        } catch (MalformedURLException e) {
            log.log(Level.SEVERE, "Malformed URL.", e);
        }
        HttpURLConnection con = null;

        try {
            con = (HttpURLConnection)url.openConnection();
            con.addRequestProperty("Cookie", "UISAuth=" + cookieVal);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not connect to AIS.", e);
        }

        appendRequestHeader(con);

        //Send post request
        con.setDoOutput(true);
        DataOutputStream wr = null;

        try {
            wr = new DataOutputStream(con.getOutputStream());

            wr.flush();
            wr.close();


            con.connect();

        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not get output stream from AIS.", e);
        }

        AISParser aisp = new AISParser();
        String serializaceFolderNum = null;
        try {
            serializaceFolderNum = aisp.getSerializaceFolderNum(printHttpsContent(con));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not get serialization number from AIS.", e);
        }

        return serializaceFolderNum;
    }

    public void sendMessage (String serializace, String cookieVal,String recieverEmail,String msgText,String folderDesc) {
        log.fine("AISCommunicator: sendMessage()");
        // ****************   send message   *********************
        URL url = null;

        try {
            url = new URL(NEW_MSG_URL);
        } catch (MalformedURLException e) {
            log.log(Level.SEVERE, "Malformed URL.", e);
        }
        HttpURLConnection con = null;

        try {
            con = (HttpURLConnection)url.openConnection();
            con.addRequestProperty("Cookie", "UISAuth=" + cookieVal);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not connect to AIS.", e);
        }

        appendRequestHeader(con);

        //Send post request
        con.setDoOutput(true);
        DataOutputStream wr = null;

        try {
            wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(URL_PARAMETERS_SEND_MESSAGE_1+recieverEmail+URL_PARAMETERS_SEND_MESSAGE_2+msgText+
                    URL_PARAMETERS_SEND_MESSAGE_3+folderDesc+URL_PARAMETERS_SEND_MESSAGE_4+serializace);

            wr.flush();
            wr.close();
            con.connect();
            con.getInputStream();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not get output stream from AIS.", e);
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
        con.setRequestProperty("UserSS-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");
        con.setRequestProperty("DNT", "1");
        con.setRequestProperty("Pragma", "no-cache");
        con.setRequestProperty("Cache-Control", "no-cache");
        con.setRequestProperty("Accept-Encoding", "gzip, deflate");
    }

    /**
     * Transformuje response do jedineho Stringu.
     * @param con
     * @return
     * @throws Exception
     */
    private String printHttpsContent(HttpURLConnection con) throws Exception {
        log.fine("AISCommunicator: printHttpsContent()");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "ISO-8859-2"));

        String inputLine;
        StringBuilder strBuf = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            strBuf.append(inputLine);
        }
        in.close();

        return strBuf.toString();
    }

}
