package xyz.whatson.android.activities.detail;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import xyz.whatson.android.R;
import xyz.whatson.android.activities.login.LoginActivity;
import xyz.whatson.android.model.Event;
import xyz.whatson.android.services.EventServices;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBar;
    private EditText editTextTitle, editTextDescription, editTextDate, editTextStartTime, editTextEndTime, editTextHost;
    private Spinner spinner;
    private Button btnChooseImage, fab;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private ImageView imageViewEventImage;
    private Uri filePath;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference ref;
    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    Calendar calendar;
    int currentHour;
    int currentMinute;
    String amPm;
    private SimpleDateFormat dateFormatter;
    boolean isEdit = false;
    Event editEvent = null;


    private final int PICK_IMAGE_REQUEST = 71;



    public String eventLocation = "";
    private final int MAP_REQUEST_CODE = 456;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String TAG = "CreateEventActivity";
    TextView locText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextHost = findViewById(R.id.editTextHost);
        editTextDate = findViewById(R.id.editTextDate);
        editTextStartTime = findViewById(R.id.editTextStartTime);
        editTextEndTime = findViewById(R.id.editTextEndTime);
        imageViewEventImage = findViewById(R.id.imageViewEventImage);
        locText = (TextView) findViewById(R.id.locationText);
        spinner = (Spinner) findViewById(R.id.spinner);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);




        dateFormatter = new SimpleDateFormat("dd/MM/yyyy");


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        findViewById(R.id.btnChooseImage).setOnClickListener(this);
        findViewById(R.id.fab).setOnClickListener(this);

        findViewById(R.id.editTextStartTime).setOnClickListener(this);
        findViewById(R.id.editTextEndTime).setOnClickListener(this);
        findViewById(R.id.editTextDate).setOnClickListener(this);


        //setup values for dropdown category
        List<String> categories = new ArrayList<String>();
        categories.add("Art");
        categories.add("Culture");
        categories.add("Sports");
        categories.add("Music");
        categories.add("Tech");
        categories.add("Science");
        categories.add("Recreation");
        categories.add("Education");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        spinner.setAdapter(dataAdapter);

        //populate all the old fields if editing a current event
        Intent eventIntent = getIntent();
        if(eventIntent != null && eventIntent.getStringExtra("Edit") != null && eventIntent.getStringExtra("Edit").equals("true")) {
            getSupportActionBar().setTitle("Edit Event");
            isEdit = true;
            editEvent = (Event) eventIntent.getParcelableExtra("Event");
            editTextTitle.setText(editEvent.getTitle());
            editTextDescription.setText(editEvent.getDescription());
            editTextHost.setText(editEvent.getHost());

            eventLocation = editEvent.getEventLocationText();
            locText.setText(eventLocation);

            String oldCategory = editEvent.getCategory();
            int categoryNum = 0;
            switch(oldCategory){
                case "Art": categoryNum = 0;
                            break;
                case "Culture": categoryNum = 1;
                            break;
                case "Sports": categoryNum = 2;
                    break;
                case "Music": categoryNum = 3;
                    break;
                case "Tech": categoryNum = 4;
                    break;
                case "Science": categoryNum = 5;
                    break;
                case "Recreation": categoryNum = 6;
                    break;
                case "Education": categoryNum = 7;
                    break;
            }
            spinner.setSelection(categoryNum);

            editTextDate.setText(dateFormatter.format(editEvent.getEventDate()));
            editTextStartTime.setText(new SimpleDateFormat("H:mma").format(editEvent.getEventStartTime()));
            editTextEndTime.setText(new SimpleDateFormat("H:mma").format(editEvent.getEventEndTime()));


        } else {
            getSupportActionBar().setTitle("Create Event");
        }


        //event location

        locText.setText(eventLocation);
        if(isServicesOK()) {
            initMap();
        }
    }

    //EVENT LOCATION FUNCTIONS//

    private void initMap () {
        Button btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateEventActivity.this, MapActivity.class);
                intent.putExtra("Location", eventLocation);
                intent.putExtra("Edit","true");
                startActivityForResult(intent, MAP_REQUEST_CODE);
            }
        });

    }



    public boolean isServicesOK() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(CreateEventActivity.this);

        if(available == ConnectionResult.SUCCESS) {
            //everything is fine and user can make map requests
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occurred but we can resolve it
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(CreateEventActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;

    }








    public void chooseStartTime () {
            calendar = Calendar.getInstance();
            currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            currentMinute = calendar.get(Calendar.MINUTE);

            timePickerDialog = new TimePickerDialog(CreateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                    if (hourOfDay >= 12) {
                        amPm = "PM";
                    } else {
                        amPm = "AM";
                    }
                    editTextStartTime.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm);
                }
            }, currentHour, currentMinute, false);

            timePickerDialog.show();
    }

    public void chooseEndTime () {
        calendar = Calendar.getInstance();
        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        currentMinute = calendar.get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(CreateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                if (hourOfDay >= 12) {
                    amPm = "PM";
                } else {
                    amPm = "AM";
                }
                editTextEndTime.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm);
            }
        }, currentHour, currentMinute, false);

        timePickerDialog.show();
    }


    public void chooseDate (){
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                editTextDate.setText(dateFormatter.format(newDate.getTime()));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
    }

    public void registerEvent() {
        final String title = editTextTitle.getText().toString().trim();
        final String description = editTextDescription.getText().toString().trim();
        final String host = editTextHost.getText().toString().trim();
        final String owner = mAuth.getCurrentUser().getUid();
        Date eventDate = null;
        Date eventStartTime= null;
        Date eventEndTime = null;
        final String category = spinner.getSelectedItem().toString();
        String imageURL;
        long removeOffset = 36000000;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
        try {
            eventDate = sdf.parse(editTextDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            eventStartTime = time.parse(editTextStartTime.getText().toString());

            eventStartTime = new Date(eventDate.getTime() + eventStartTime.getTime() + removeOffset);



        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            eventEndTime = time.parse(editTextEndTime.getText().toString());
            eventEndTime = new Date(eventDate.getTime() + eventEndTime.getTime() + removeOffset);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (title.isEmpty()) {
            editTextTitle.setError("Title Required");
            editTextDate.requestFocus();
            return;
        }

        if (eventDate == null) {
            editTextDate.setError("Date Required");
            editTextDate.requestFocus();
            return;
        }
        if (eventStartTime == null) {
            editTextStartTime.setError("Start Time Required");
            editTextStartTime.requestFocus();
            return;
        }
        if (eventEndTime == null) {
            editTextEndTime.setError("End Time Required");
            editTextEndTime.requestFocus();
            return;
        }
        if(eventLocation.equals("")) {
            Toast.makeText(this, "Please enter an event location", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //call the uplaod image method to upload image to firebase storage
        imageURL = uploadImage();


        if (isEdit == true) {

            DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("Events").child(editEvent.getKey());
            eventRef.child("category").setValue(category);
            eventRef.child("description").setValue(description);
            eventRef.child("eventDate").setValue(eventDate);
            eventRef.child("eventEndTime").setValue(eventEndTime);
            eventRef.child("eventLocationText").setValue(eventLocation);
            eventRef.child("eventStartTime").setValue(eventStartTime);
            eventRef.child("host").setValue(host);
            eventRef.child("imageURL").setValue(imageURL);
            eventRef.child("owner").setValue(owner);
            eventRef.child("title").setValue(title);

            Toast.makeText(CreateEventActivity.this, "Event Edited Successfully", Toast.LENGTH_LONG).show();
            goToEventsFeed();

           

        } else {
            //create new event object and save in firebase database

            Event event = new Event(title, description, host, eventDate , eventStartTime, eventEndTime, category, imageURL, owner, eventLocation);
            DatabaseReference eventRef = ref.child("Events");
            eventRef.push().setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(CreateEventActivity.this, getString(R.string.event_registration_success), Toast.LENGTH_LONG).show();
                        goToEventsFeed();

                    } else {
                        Toast.makeText(CreateEventActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    //method to select image from gallery
    public void selectImage () {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private String uploadImage() {

        if (filePath != null) {
            String randomString = UUID.randomUUID().toString();
            StorageReference ref = storageReference.child("images/" + randomString);
            ref.putFile(filePath);
            return randomString;
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageViewEventImage.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        if (requestCode == MAP_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                eventLocation = data.getStringExtra("Location");
                locText.setText(eventLocation);
               // Toast.makeText(this, "eventLocation = " + eventLocation, Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void goToEventsFeed () {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                registerEvent();
                break;
        }

        switch (v.getId()) {
            case R.id.btnChooseImage:
                selectImage();
                break;
        }

        switch (v.getId()) {
            case R.id.editTextStartTime:
                chooseStartTime();
                break;
        }

        switch (v.getId()) {
            case R.id.editTextEndTime:
                chooseEndTime();
                break;
        }
        switch (v.getId()) {
            case R.id.editTextDate:
                chooseDate();
                break;
        }
    }
}
