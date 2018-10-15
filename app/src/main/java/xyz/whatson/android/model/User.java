package xyz.whatson.android.model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class User {
    public String name, email;
    public String art, culture, sports, music, tech, science, recreation, education;
    public String subscribedEvent;

    public User(){

    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User (String name, String email, String art, String culture, String sports, String music, String tech , String science, String recreation, String education, String subscribedEvent  ) {
        this.name = name;
        this.email = email;
        this.art = art;
        this.culture = culture;
        this.sports = sports;
        this.music= music;
        this.tech = tech;
        this.science = science;
        this.recreation = recreation;
        this.education = education;
        this.subscribedEvent = subscribedEvent;
    }
}