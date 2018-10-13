package xyz.whatson.android.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import xyz.whatson.android.R;
import xyz.whatson.android.activities.detail.ViewEventActivity;
import xyz.whatson.android.adapter.EventAdapter;
import xyz.whatson.android.adapter.RecyclerOnClickListener;
import xyz.whatson.android.model.Event;
import xyz.whatson.android.services.AppCallback;
import xyz.whatson.android.services.EventServices;

public class SearchActivity extends AppCompatActivity {


    private static final String TAG = SearchActivity.class.getName();

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<Event> eventList;

    private EventServices eventServices;


    private AppCompatCheckBox checkboxArt,checkboxMusic, checkboxTech, checkboxCareers,
            checkboxCulture, checkboxSports, checkboxScience, checkboxEducation;
//
//    private String dateRangeValue;
//
//    private static String DATE_RANGE = "DATE_RANGE";
//    private static String[] DATE_RANGE_LABELS = {"Upcoming", "This Week", "Past Month", "Past"};

    private static String ART = "ART";
    private static String MUSIC = "MUSIC";
    private static String TECH = "TECH";
    private static String CAREERS = "CAREERS";
    private static String CULTURE = "CULTURE";
    private static String SPORTS = "SPORTS";
    private static String SCIENCE = "SCIENCE";
    private static String EDUCATION = "EDUCATION";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        /*
            Initialises the search filter UI.


         */
//        Spinner spinner = (Spinner) findViewById(R.id.search_date);
//        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
//                this, android.R.layout.simple_spinner_item, DATE_RANGE_LABELS);
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(spinnerAdapter);
//        spinner.setOnItemSelectedListener(this);
//        dateRangeValue = SearchActivity.DATE_RANGE_LABELS[0];


        // Get handles for the filter ui components, we'll need these to extract
        // the user's search filter choices
        checkboxArt = findViewById(R.id.checkBox1_1);
        checkboxMusic = findViewById(R.id.checkBox1_2);
        checkboxTech = findViewById(R.id.checkBox1_3);
        checkboxCareers = findViewById(R.id.checkBox1_4);
        checkboxCulture = findViewById(R.id.checkBox2_1);
        checkboxSports = findViewById(R.id.checkBox2_2);
        checkboxScience = findViewById(R.id.checkBox2_3);
        checkboxEducation = findViewById(R.id.checkBox2_4);


        /*
            Initialises the search results layout.


        */
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //// creates a staggered grid
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL));
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
            public void onLongClick(View view, int position) {}
        }));

        eventServices = EventServices.getInstance();

        eventList = new ArrayList<>();
        adapter = new EventAdapter(this, eventList);
        recyclerView.setAdapter(adapter);

        handleIntent(getIntent());
    }

    @Override
    public boolean onSearchRequested() {
        Bundle appData = new Bundle();

        appData.putBoolean(SearchActivity.ART,checkboxArt.isChecked());
        appData.putBoolean(SearchActivity.MUSIC,checkboxMusic.isChecked());
        appData.putBoolean(SearchActivity.TECH,checkboxTech.isChecked());
        appData.putBoolean(SearchActivity.CAREERS,checkboxCareers.isChecked());
        appData.putBoolean(SearchActivity.CULTURE,checkboxCulture.isChecked());
        appData.putBoolean(SearchActivity.SPORTS,checkboxSports.isChecked());
        appData.putBoolean(SearchActivity.SCIENCE,checkboxScience.isChecked());
        appData.putBoolean(SearchActivity.EDUCATION,checkboxEducation.isChecked());

        startSearch(null, false, appData, false);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent){
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

//            int dateRange = intent.getIntExtra(SearchActivity.DATE_RANGE,0);
            boolean art = intent.getBooleanExtra(SearchActivity.ART, false);
            boolean music = intent.getBooleanExtra(SearchActivity.MUSIC, false);
            boolean tech = intent.getBooleanExtra(SearchActivity.TECH, false);
            boolean careers = intent.getBooleanExtra(SearchActivity.CAREERS, false);
            boolean culture = intent.getBooleanExtra(SearchActivity.CULTURE, false);
            boolean sports = intent.getBooleanExtra(SearchActivity.SPORTS, false);
            boolean science = intent.getBooleanExtra(SearchActivity.SCIENCE, false);
            boolean education = intent.getBooleanExtra(SearchActivity.EDUCATION, false);

            //use the query to search our data somehow
            doSearch(query, art, music, tech, careers,
                    culture, sports, science, education);
        }
    }

    private void doSearch(String query, boolean art,
                          boolean music,
                          boolean tech,
                          boolean careers,
                          boolean culture,
                          boolean sports,
                          boolean science,
                          boolean education){
        /*
            Make a remote call to Firebase for searching and displays the results.
         */
        String category = "Art";
        eventServices.searchEventFeed(query, null, null, category, new AppCallback<List<Event>>() {
            @Override
            public void call(final List<Event> events) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        eventList.addAll(events);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void call() {

            }
        });

//        Log.d(TAG, "doSearch: " + query + " " + SearchActivity.DATE_RANGE_LABELS[dateRange] + " ");
        adapter.notifyDataSetChanged();
    }


    private void doRemoteSearch(String query) {
        /*
            Adding a few events for testing

        */
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,2018);
        cal.set(Calendar.MONTH,12);

        String[] titles = {"Search Party", "Lunchtime Search Basketball", "Search Games", "Pickup Soccer Search"};

        for(int i = 0 ; i < 10; i++ ) {
            cal.set(Calendar.DAY_OF_MONTH,i);
            eventList.add(new Event(titles[i % titles.length], "test description", "test host", cal.getTime(),cal.getTime(), cal.getTime(), "test category", "test URL", "test owner", "test location"));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_activity, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);//set search menu as full width
        searchView.setIconifiedByDefault(false);


        return super.onCreateOptionsMenu( menu );
    }


}
