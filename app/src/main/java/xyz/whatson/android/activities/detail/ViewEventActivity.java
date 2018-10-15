package xyz.whatson.android.activities.detail;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import xyz.whatson.android.R;
import xyz.whatson.android.activities.EventsFeedActivity;
import xyz.whatson.android.model.Event;

public class ViewEventActivity extends AppCompatActivity {


    private TextView mEventNameText, mEventOrganiserNameText, mStartDateText, mStartTimeText, mEndDateText, mEndTimeText, mLocationText, mPriceText, mDescriptionText, mOrganiserBioText, mCategoryText;
    private String eventLocation = "";
    private static final int ERROR_DIALOG_REQUEST = 9002;
    private FirebaseUser user;
    Event event;
    private FirebaseDatabase database;
    private DatabaseReference ref;


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
                  ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("subscribedEvents").child(event.getKey());
                  ref.setValue(event.getTitle());
            }
        });



        FloatingActionButton editEvent = (FloatingActionButton) findViewById(R.id.edit_event);
        editEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(event != null) {
                    Intent intent = new Intent(ViewEventActivity.this, CreateEventActivity.class);
                    intent.putExtra("Event", event);
                    intent.putExtra("Edit", "true");
                    startActivity(intent);

                } else {
                    Toast.makeText(ViewEventActivity.this, "no event", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FloatingActionButton deleteEvent = (FloatingActionButton) findViewById(R.id.delete_event);
        deleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(event != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewEventActivity.this);
                    builder.setTitle("Delete this event")
                            .setMessage("Are you sure you want to delete this event?")
                            .setPositiveButton("Delete", new
                                    DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("Events").child(event.getKey());
                                            eventRef.removeValue();
                                            finish();


                                            //app restart to refresh feed
                                            Intent restart = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                                            restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(restart);

                                        }
                                    })
                            .setNegativeButton("Cancel", new
                                    DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //user cancels, nothing happens
                                        }
                                    });
                    builder.create().show();




                }
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();





        // instantiate handles for content fields
       // mEventNameText = (TextView) findViewById(R.id.event_name_text);
        mEventOrganiserNameText = (TextView) findViewById(R.id.event_organiser_name_text);
        mStartDateText = (TextView) findViewById(R.id.start_date_text);
        mStartTimeText = (TextView) findViewById(R.id.start_time_text);
        mEndDateText = (TextView) findViewById(R.id.end_date_text);
        mEndTimeText = (TextView) findViewById(R.id.end_time_text);
        mLocationText = (TextView) findViewById(R.id.location_text);
       // mPriceText = (TextView) findViewById(R.id.price_text);
        mDescriptionText = (TextView) findViewById(R.id.description_text);
      //  mOrganiserBioText = (TextView) findViewById(R.id.about_text);
        mCategoryText = (TextView) findViewById(R.id.category_text);

        if(isServicesOK()) {
            initMap();
        }
        /*
            Populate content fields using the Event object passed to this activity


         */
        Intent eventIntent = getIntent();
        // TODO: throw error if no event is passed to this activity to be displayed
        if (eventIntent == null) {
            mEventNameText.setText("Sample Event");
            mEventOrganiserNameText.setText("by A Student Club");

            mStartDateText.setText("Today");
            mStartTimeText.setText("9pm");
            mEndDateText.setText("Tomorrow");
            mEndTimeText.setText("9pm");
            mLocationText.setText("Here");
            mPriceText.setText("$9,999");
            mDescriptionText.setText(R.string.lorem_ipsum);
            mOrganiserBioText.setText(R.string.lorem_ipsum);
        } else {
            event = (Event) eventIntent.getParcelableExtra("event");
            setTitle(event.getTitle());
            mEventOrganiserNameText.setText("by " + event.getHost());

            mStartDateText.setText(new SimpleDateFormat("d MMM").format(event.getEventDate()));
            mStartTimeText.setText(new SimpleDateFormat("H:mma").format(event.getEventStartTime()));
            mEndDateText.setText(new SimpleDateFormat("d MMM").format(event.getEventDate()));
            mEndTimeText.setText(new SimpleDateFormat("H:mma").format(event.getEventEndTime()));
            mLocationText.setText(event.getEventLocationText());
            eventLocation = event.getEventLocationText();
            mDescriptionText.setText(event.getDescription());
            mCategoryText.setText(event.getCategory());

            if (userID.equals(event.getOwner())) {
                editEvent.setVisibility(View.VISIBLE);
                deleteEvent.setVisibility(View.VISIBLE);
            }

        }



    }

    private void initMap () {
        Button btnMap = (Button) findViewById(R.id.view_map);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewEventActivity.this, MapActivity.class);
                intent.putExtra("Location", eventLocation);
                intent.putExtra("Edit", "false");
                startActivity(intent);
            }
        });

    }

    public boolean isServicesOK() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ViewEventActivity.this);

        if(available == ConnectionResult.SUCCESS) {
            //everything is fine and user can make map requests
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occurred but we can resolve it
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(ViewEventActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;

    }



}
