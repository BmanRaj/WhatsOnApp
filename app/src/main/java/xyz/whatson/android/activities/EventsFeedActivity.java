package xyz.whatson.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import xyz.whatson.android.R;
import xyz.whatson.android.activities.detail.CreateEventActivity;
import xyz.whatson.android.activities.detail.ViewEventActivity;
import xyz.whatson.android.activities.login.LoginActivity;
import xyz.whatson.android.activities.settings.SettingsActivity;
import xyz.whatson.android.adapter.EventAdapter;
import xyz.whatson.android.adapter.RecyclerOnClickListener;
import xyz.whatson.android.model.Event;
import xyz.whatson.android.services.AppCallback;
import xyz.whatson.android.services.EventServices;

public class EventsFeedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = EventsFeedActivity.class.getName();

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<Event> eventList;
    private EventServices eventServices;

    SwipeRefreshLayout pullToRefresh;
    boolean refreshing = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
            Decide if we need to redirect the user to the login screen.
            Else, proceed with creating this activity.
         */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // User is signed out
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
        // TODO: check if the user is logged in, but has not yet verified their email, and ask them to verify.




        /*
            Initialises the activity's layout
        */
        setTheme(R.style.AppTheme); // Change the activity background from the splash screen logo,
                                    // to the normal background.

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.title_activity_events_feed);
        // https://stackoverflow.com/a/24616650


        // Maps the floating 'Add Event Button' to the create event activity.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventsFeedActivity.this, CreateEventActivity.class);
                Event nullEvent = null;
                intent.putExtra("Event", nullEvent);
                intent.putExtra("Edit", "false");
                startActivity(intent);
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

        /*
            Initialises the events list layout.
        */
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //// creates a staggered grid
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL));
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //// Maps clicking on an event card, to opening the event detail view in the view event activity.
        recyclerView.addOnItemTouchListener(new RecyclerOnClickListener(getApplicationContext(), recyclerView, new RecyclerOnClickListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Event event  = eventList.get(position);
                // Go to View Event Page
                Intent intent = new Intent(getApplicationContext(), ViewEventActivity.class);
                intent.putExtra("event", event);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        /**
         * Initialise event services which handles background database synchronising
         */
        eventServices = EventServices.getInstance();

        eventList = new ArrayList<>();
        adapter = new EventAdapter(this, eventList);
        recyclerView.setAdapter(adapter);
        prepareEvents();

        /**
         * Add Pull to Refresh
         */
        pullToRefresh = findViewById(R.id.pullToRefreshEvents);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshing = true;
                prepareEvents();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        // Recheck if the user is logged in, when returning to this activity.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // User is signed out
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

        // Highlights the current page in the navigation drawer.
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    private void prepareEvents() {
        /*
            Adding a few events for testing
        */
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,2018);
        cal.set(Calendar.MONTH,12);

        Log.d(TAG, "getting prepare events");

        // Create data source and populate events feed with data
        eventServices.getEventsFeedEvents(new Date().getTime(), new AppCallback<List<Event>>() {
            @Override
            public void call(final List events) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        eventList.clear();
                        eventList.addAll(events);
                        adapter.notifyDataSetChanged();
                        if(refreshing){
                            pullToRefresh.setRefreshing(false);
                            refreshing = false;
                        }
                    }
                });
            }

            @Override
            public void call() {

            }
        });

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
        getMenuInflater().inflate(R.menu.events_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            startActivity(new Intent(this, SearchActivity.class));
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main_feed) {
            // do nothing
        } else if (id == R.id.nav_my_events) {
            // Go to the My Events page
            startActivity(new Intent(this, MyEventsActivity.class));
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
