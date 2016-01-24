package com.tanmaychordia.sugardaddy;

/**
 * Created by Tanmay on 1/22/16.
 */
import android.app.Application;

import com.hound.android.fd.Houndify;
import com.parse.Parse;

/**
 * Created by Tanmay on 2/18/15.
 */
public class SDApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        Parse.enableLocalDatastore(this);
//        ParseObject.registerSubclass(ParseUser.class);

        Parse.initialize(this,"FYimJTE5WFlhiSGxbvJr7nOZUChcodDWdwViIVOr","lq8acbUQG8UIBUR8obhpUjyQR3uufHKRrwV7Qpcq");

        // Normally you'd only have to do this once in your Application#onCreate
        Houndify.get(this).setClientId(Constants.CLIENT_ID);
        Houndify.get(this).setClientKey(Constants.CLIENT_KEY);
        Houndify.get(this).setRequestInfoFactory(StatefulRequestInfoFactory.get(this));

    }
}