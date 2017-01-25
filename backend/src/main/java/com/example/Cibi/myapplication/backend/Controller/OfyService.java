package com.example.Cibi.myapplication.backend.Controller;

import com.example.Cibi.myapplication.backend.Model.UserSS;
import com.example.Cibi.myapplication.backend.Model.MessageSS;
import com.example.Cibi.myapplication.backend.Model.ConversationSS;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

/**
 * Objectify service wrapper so we can statically register our persistence classes
 * More on Objectify here : https://code.google.com/p/objectify-appengine/
 *
 */
public class OfyService {

    static {
        ObjectifyService.register(UserSS.class);
        ObjectifyService.register(ConversationSS.class);
        ObjectifyService.register(MessageSS.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
