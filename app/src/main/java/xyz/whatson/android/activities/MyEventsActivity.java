package xyz.whatson.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import xyz.whatson.android.R;
import xyz.whatson.android.activities.detail.CreateEventActivity;
import xyz.whatson.android.activities.detail.ViewEventActivity;
import xyz.whatson.android.activities.login.LoginActivity;
import xyz.whatson.android.activities.settings.SettingsActivity;
import xyz.whatson.android.adapter.EventAdapter;
import xyz.whatson.android.adapter.RecyclerOnClickListener;
import xyz.whatson.android.model.Event;

public class MyEventsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private EventAdapter myEventsAdapter;
    private List<Event> myEventList;

    private EventAdapter pastEventsAdapter;
    private List<Event> pastEventsList;

    private EventAdapter hostedEventsAdapter;
    private List<Event> hostedEventsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
            Initialises the activity's layout
        */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Maps the floating 'Add Event Button' to the create event activity.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyEventsActivity.this, CreateEventActivity.class));
            }
        });


        /*
            Initialises the navigation drawer layout
        */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        initMyEventsList();
        initHostedEventsList();
        initPastEventsList();
    }


    private void initMyEventsList(){
        /*
            Initialises the events list layout.
        */
        RecyclerView eventsRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //// creates a linear list
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        eventsRecyclerView.setLayoutManager(mLayoutManager);
        eventsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //// Maps clicking on an event card, to opening the event detail view in the view event activity.
        eventsRecyclerView.addOnItemTouchListener(new RecyclerOnClickListener(getApplicationContext(), eventsRecyclerView, new RecyclerOnClickListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Event event  = myEventList.get(position);
                // Go to View Event Page
                Intent intent = new Intent(getApplicationContext(), ViewEventActivity.class);
                intent.putExtra("event", event);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        /*
            Populate events lists


         */
        // Create data source and populate events feed with data
        myEventList = new ArrayList<>();
        myEventsAdapter = new EventAdapter(this, myEventList);
        eventsRecyclerView.setAdapter(myEventsAdapter);
        prepareMyEvents();
    }
    private void initHostedEventsList(){
        /*
            Initialises the events list layout.
        */
        RecyclerView eventsRecyclerView = (RecyclerView) findViewById(R.id.recycler_view2);
        //// creates a linear list
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        eventsRecyclerView.setLayoutManager(mLayoutManager);
        eventsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //// Maps clicking on an event card, to opening the event detail view in the view event activity.
        eventsRecyclerView.addOnItemTouchListener(new RecyclerOnClickListener(getApplicationContext(), eventsRecyclerView, new RecyclerOnClickListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Event event  = hostedEventsList.get(position);
                // Go to View Event Page
                Intent intent = new Intent(getApplicationContext(), ViewEventActivity.class);
                intent.putExtra("event", event);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        /*
            Populate events lists


         */
        // Create data source and populate events feed with data
        hostedEventsList = new ArrayList<>();
        hostedEventsAdapter = new EventAdapter(this, hostedEventsList);
        eventsRecyclerView.setAdapter(hostedEventsAdapter);
        prepareHostedEvents();
    }
    private void initPastEventsList(){
        /*
            Initialises the events list layout.
        */
        RecyclerView eventsRecyclerView = (RecyclerView) findViewById(R.id.recycler_view3);
        //// creates a linear list
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        eventsRecyclerView.setLayoutManager(mLayoutManager);
        eventsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //// Maps clicking on an event card, to opening the event detail view in the view event activity.
        eventsRecyclerView.addOnItemTouchListener(new RecyclerOnClickListener(getApplicationContext(), eventsRecyclerView, new RecyclerOnClickListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Event event  = pastEventsList.get(position);
                // Go to View Event Page
                Intent intent = new Intent(getApplicationContext(), ViewEventActivity.class);
                intent.putExtra("event", event);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        /*
            Populate events lists


         */
        // Create data source and populate events feed with data
        pastEventsList = new ArrayList<>();
        pastEventsAdapter = new EventAdapter(this, pastEventsList);
        eventsRecyclerView.setAdapter(pastEventsAdapter);
        preparePastEvents();
    }

    private void prepareMyEvents() {
        /*
            Adding a few events for testing

        */
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,2018);
        cal.set(Calendar.MONTH,12);

        String[] titles = {"Anime Meetup", "Lunchtime Basketball", "Lunchtime Games", "Pickup Soccer"};

        for(int i = 0 ; i < 10; i++ ) {
            cal.set(Calendar.DAY_OF_MONTH,i);
            myEventList.add(new Event(titles[i % titles.length], "test description", "test host", cal.getTime(), cal.getTime(), cal.getTime(), "category", "test URL", "test owner", "Seymour Centre"));
        }

        myEventsAdapter.notifyDataSetChanged();
    }
    private void prepareHostedEvents() {
        /*
            Adding a few events for testing

        */
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,2018);
        cal.set(Calendar.MONTH,12);

        String[] titles = { "Lunchtime Basketball", "Pickup Soccer"};

        for(int i = 0 ; i < 10; i++ ) {
            cal.set(Calendar.DAY_OF_MONTH,i);
            hostedEventsList.add(new Event(titles[i % titles.length], "test description", "test host", cal.getTime(), cal.getTime(), cal.getTime(), "category", "test URL", "test owner", "Seymour Centre"));
        }


        hostedEventsAdapter.notifyDataSetChanged();
    }
    private void preparePastEvents() {
        /*
            Adding a few events for testing

        */
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,2018);
        cal.set(Calendar.MONTH,12);

        String[] titles = { "Anime Meetup" };

        for(int i = 0 ; i < 10; i++ ) {
            cal.set(Calendar.DAY_OF_MONTH,i);
            pastEventsList.add(new Event(titles[i % titles.length], "test description", "test host", cal.getTime(), cal.getTime(), cal.getTime(), "category", "test URL", "test owner", "Seymour Centre"));
        }


        pastEventsAdapter.notifyDataSetChanged();
    }





    @Override
    protected void onResume() {
        super.onResume();

        // Highlights the current page in the navigation drawer.
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_events, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main_feed) {
            startActivity(new Intent(this, EventsFeedActivity.class));
        } else if (id == R.id.nav_my_events) {
            // do nothing
        } else if (id == R.id.nav_settings) {
            // Go to the Settings Page
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_logout) {
            // Sign out
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
