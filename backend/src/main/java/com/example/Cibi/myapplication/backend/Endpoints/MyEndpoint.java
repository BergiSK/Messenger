/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.example.Cibi.myapplication.backend.Endpoints;

import com.example.Cibi.myapplication.backend.Controller.AISCommunicator;
import com.example.Cibi.myapplication.backend.Controller.ConversationHandler;
import com.example.Cibi.myapplication.backend.Controller.DatabaseCommunicator;
import com.example.Cibi.myapplication.backend.Model.ConversationSS;

import com.example.Cibi.myapplication.backend.Model.ResponseCode;
import com.example.Cibi.myapplication.backend.Model.UserSS;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiAuth;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.ConflictException;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;



/** Prakticky facade beana, jej metody su pristupne z klientskej aplikacie. */
@Api(
  name = "myApi",
  version = "v1",
        auth = @ApiAuth(allowCookieAuth = AnnotationBoolean.TRUE),
  namespace = @ApiNamespace(
    ownerDomain = "backend.myapplication.Cibi.example.com",
    ownerName = "backend.myapplication.Cibi.example.com",
    packagePath=""
  )
)
public class MyEndpoint {
    private static final Logger log = Logger.getLogger(AISCommunicator.class.getName());

    // Vrati konverzacie z datastoru, ktore prisluchaju danemu loginname (defaultne poslednych 10)
    @ApiMethod(name = "readConversationsDB")
    public List<ConversationSS> readConversationsDB(
            @Named("loginName") String loginName,
            @Nullable @Named("cursor") String cursorString,
            @Nullable @Named("count") Integer count) {

        log.info("MyEndpoint: reading conversations from DB");
        DatabaseCommunicator communicator = new DatabaseCommunicator();
        if (count != null) {
            return communicator.readConvesationsDB(cursorString, count, loginName);
        }
        else {
            return communicator.readConvesationsDB(cursorString, 10, loginName);
        }
    }


    /** Vrati usera s pridelenym cookie z aisu */
    @ApiMethod(name = "login")
    public UserSS login(HttpServletRequest req,@Named("loginName") String name, @Named("password") String password) {
        AISCommunicator aisCommunicator = new AISCommunicator();
        aisCommunicator.login(password, name);

        String cookieVal = req.getCookies().toString();
        UserSS user = new UserSS();
        user.setCookie(cookieVal);
        //user.setCookie("asdasdasd");

        return user;
    }

    // stiahne z AISU aktualne konverzacie a vrati pocet updatovanych riadkov DB (zisti ci su nejake nove)
    @ApiMethod(name = "updateDB")
    public ResponseCode updateDBfromAIS(@Named("loginName") String loginName,@Named("cookieVal") String cookieVal) {
        log.info("MyEndpoint: updating DB from AIS");
        UserSS loggedIn = new UserSS();
        loggedIn.setCookie(cookieVal);
        loggedIn.setLoginName(loginName);

        DatabaseCommunicator communicator = new DatabaseCommunicator();
        ConversationHandler handler = new ConversationHandler();
        List<ConversationSS> conversations = handler.getRecentConversations(loggedIn);

        return handler.checkUpdates(loginName,communicator,conversations);
    }

    // odosle spravu a vlozi ju do DB
    @ApiMethod(name = "sendMessage")
    public void sendMessage(@Named("cookieVal") String cookieVal, @Named("loginName") String loginName,
                            @Named("recEmail") String recieverEmail,@Named("msgText") String msgText) {
        log.info("MyEndpoint: sending a message");

        AISCommunicator aisCommunicator = new AISCommunicator();
        DatabaseCommunicator dbCommunicator = new DatabaseCommunicator();
        
        String[] serializace_folderNum = aisCommunicator.getSerializationFolderNum(cookieVal).split("MYsplit");
        aisCommunicator.sendMessage(serializace_folderNum[0],cookieVal,recieverEmail,msgText,serializace_folderNum[1]);
        dbCommunicator.insertSentMessage(loginName,msgText,recieverEmail);


    }


}
