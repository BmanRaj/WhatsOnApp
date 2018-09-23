package xyz.whatson.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import xyz.whatson.android.R;
import xyz.whatson.android.model.Event;

public class ViewEventActivity extends AppCompatActivity {


    private TextView mEventNameText, mEventOrganiserNameText, mDateText, mTimeText, mLocationText, mPriceText, mDescriptionText, mOrganiserBioText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // https://github.com/blanyal/Remindly/blob/master/app/src/main/java/com/blanyal/remindme/ReminderAddActivity.java
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Favourite event
            }
        });

        // instantiate handles for content fields
        mEventNameText = (TextView) findViewById(R.id.event_name_text);
        mEventOrganiserNameText = (TextView) findViewById(R.id.event_organiser_name_text);
        mDateText = (TextView) findViewById(R.id.date_text);
        mTimeText = (TextView) findViewById(R.id.time_text);
        mLocationText = (TextView) findViewById(R.id.location_text);
        mPriceText = (TextView) findViewById(R.id.price_text);
        mDescriptionText = (TextView) findViewById(R.id.description_text);
        mOrganiserBioText = (TextView) findViewById(R.id.about_text);


        /*
            Populate content fields using the Event object passed to this activity


         */
        Intent eventIntent = getIntent();
        // TODO: throw error if no event is passed to this activity to be displayed
        if (eventIntent == null) {
            mEventNameText.setText("Sample Event");
            mEventOrganiserNameText.setText("by A Student Club");

            mDateText.setText("Today");
            mTimeText.setText("9pm");
            mLocationText.setText("Here");
            mPriceText.setText("$9,999");
            mDescriptionText.setText(R.string.lorem_ipsum);
            mOrganiserBioText.setText(R.string.lorem_ipsum);
        } else {
            Event event = (Event) eventIntent.getParcelableExtra("event");

            mEventNameText.setText(event.getTitle());
            mEventOrganiserNameText.setText("by Sample Student Club");

            mDateText.setText(new SimpleDateFormat("d MMM").format(event.getEventDate()));
            mTimeText.setText(new SimpleDateFormat("ha").format(event.getEventDate()));
            mLocationText.setText("Here");
            String price = NumberFormat.getCurrencyInstance().format(event.getPriceInCents());
            mPriceText.setText("$" + price);
            mDescriptionText.setText(R.string.lorem_ipsum);
            mOrganiserBioText.setText(R.string.lorem_ipsum);
        }



    }

}
