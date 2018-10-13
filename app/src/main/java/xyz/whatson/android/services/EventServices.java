package xyz.whatson.android.services;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.List;

import xyz.whatson.android.WhatsOn;
import xyz.whatson.android.db.EventDao;
import xyz.whatson.android.db.WhatsonDB;
import xyz.whatson.android.model.Event;


/**
 * Singleton class to handle syncing event data with local database and querying event data
 */
public class EventServices {

    final String TAG = "Event Services";
    private static EventServices eventServices = null;

    DatabaseReference dbRef;

    WhatsonDB db;
    EventDao eventDao;
    long latestEventTime;

    public EventServices(){

        //create instance of whatson db and eventDao
        db = WhatsonDB.getDatabase(WhatsOn.getAppContext());
        eventDao = db.eventDao();

        //initialise firebase db connection
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        dbRef = db.getReference().child("Events");

        final long[] latestEventArr = { 0 };

        //get latest database entry
        Thread getSyncTime = new Thread(new Runnable() {
            @Override
            public void run() {
                List<Event> events = eventDao.listAll();
                for(Event event: events){
                    Log.d("Retrieved from db", event.getTitle());
                }

                Event latestEvent = eventDao.getLatest();
                if(latestEvent != null){
                    latestEventArr[0] = latestEvent.getEventStartTime().getTime();
                }
//                Log.d("Get latest event", latestEvent.getTitle());
            }
        });

        getSyncTime.start();
        try{
            getSyncTime.join();
        }catch(Exception e){

        }

        latestEventTime = latestEventArr[0];

        //check for data changes since last sync
        dbRef.orderByChild("eventStartTime").startAt(latestEventTime).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Event event = dataSnapshot.getValue(Event.class);
                String key = dataSnapshot.getKey();
                event.setKey(key);

                //upsert event
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        eventDao.insert(event);
                    }
                }).start();

//                Log.d("On child added", event.getTitle());

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final Event event = dataSnapshot.getValue(Event.class);
                String key = dataSnapshot.getKey();
                event.setKey(key);

                //upsert event
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        eventDao.insert(event);
                    }
                }).start();

//                Log.d("On child changed", event.getTitle());

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final Event event = dataSnapshot.getValue(Event.class);
                //Remove removed child nodes from database
                final String key = dataSnapshot.getKey();

                //Remove matching key
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        eventDao.remove(key);
                    }
                }).start();

//                Log.d("On child added", event.getTitle());

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static EventServices getInstance()
    {
        if(eventServices == null)
            eventServices = new EventServices();
        return eventServices;
    }

    //method which returns list of events to be displayed in recycler view for events feed
    public void getEventsFeedEvents(final long startDate, final AppCallback callback)
    {
        try{
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids){
                    List<Event> events = eventDao.getEventsAfterTime(startDate);
                    Log.d(TAG, "getting event feed events");
                    callback.call(events);
                    return null;
                }
            }.execute().get();
        } catch (Exception e){

        }
    }

    public void searchEventFeed(final String searchStr, final Date startTime, final Date endTime, final String category, final AppCallback callback)
    {
        try{
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids){
                    String regexSearchStr = "%" + searchStr + "%";
                    long startTimeTime = 0;
                    long endTimeTime = Long.MAX_VALUE;
                    if(startTime != null) startTimeTime = startTime.getTime();
                    if(endTime != null) endTimeTime = endTime.getTime();
                    List<Event> events = eventDao.searchEvents(regexSearchStr, startTimeTime, endTimeTime, category);
                    callback.call(events);
                    return null;
                }
            }.execute().get();
        } catch(Exception e){

        }
    }

    public void createEvent(Event event)
    {

    }

}