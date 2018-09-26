package xyz.whatson.android.services;

import java.util.ArrayList;
import java.util.List;
import xyz.whatson.android.model.Event;


/**
 * Class to handle synchronisation of events between local db and firebase, and handle adding event data
 */
public class EventServices {

    public EventServices(){

    }

    public void addEvent(Event event, AppCallback callback){
        
        callback.call();
    }

    public void getEvents(AppCallback callback){
        ArrayList<Event> eventsList = new ArrayList<>();
        callback.call();
//        return eventsList;
    }

    private void getLatestEvents(AppCallback callback){
        callback.call();
    }

}
