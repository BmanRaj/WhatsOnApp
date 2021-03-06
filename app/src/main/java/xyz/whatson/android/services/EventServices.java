package xyz.whatson.android.services;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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
    FirebaseAuth mAuth;

    WhatsonDB db;
    EventDao eventDao;
    long latestEventTime;

    public EventServices(){

        //create instance of whatson db and eventDao
        db = WhatsonDB.getDatabase(WhatsOn.getAppContext());
        eventDao = db.eventDao();

        mAuth = FirebaseAuth.getInstance();

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

    public void searchEventFeed(final String searchStr, final Date startTime, final Date endTime, final List<String> categories, final AppCallback callback)
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
                    List<Event> events;
                    if(categories.size() == 0)
                        events = eventDao.searchEvents(regexSearchStr, startTimeTime, endTimeTime, null);
                    else
                        events = eventDao.searchEvents(regexSearchStr, startTimeTime, endTimeTime, categories);
                    callback.call(events);
                    return null;
                }
            }.execute().get();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getStarredEvents(final AppCallback callback)
    {
        FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid()).child("subscribedEvents").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> events = new ArrayList<>();
                for(DataSnapshot key: dataSnapshot.getChildren()){
                    String eventId = key.getKey();
                    events.add(eventId);
                }
                try{
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids){
                            List<Event> starredEvents = eventDao.getStarredEvents(events, new Date().getTime());
                            callback.call(starredEvents);
                            return null;
                        }
                    }.execute().get();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void getRecommendedEvents(final AppCallback callback)
    {
        FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> categories = new ArrayList<>();
                for(DataSnapshot key: dataSnapshot.getChildren()){
                    String category = key.getKey().substring(0, 1).toUpperCase() + key.getKey().substring(1);
                    if(key.getValue().equals("true")){
                        categories.add(category);
                    }
                }
                try{
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids){
                            List<Event> starredEvents = eventDao.getRecommendedEvents(categories, new Date().getTime());
                            callback.call(starredEvents);
                            return null;
                        }
                    }.execute().get();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getHostedEvents(final AppCallback callback)
    {
        try{
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids){
                    List<Event> hostedEvents = eventDao.getHostedEvents(mAuth.getUid(), new Date().getTime());
                    callback.call(hostedEvents);
                    return null;
                }
            }.execute().get();
        } catch (Exception e){

        }

    }

    public void getPastEvents(final AppCallback callback)
    {
        FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid()).child("subscribedEvents").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> events = new ArrayList<>();
                for(DataSnapshot key: dataSnapshot.getChildren()){
                    String eventId = key.getKey();
                    events.add(eventId);
                }
                try{
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids){
                            List<Event> pastEvents = eventDao.getPastEvents(events, new Date().getTime());
                            callback.call(pastEvents);
                            return null;
                        }
                    }.execute().get();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}