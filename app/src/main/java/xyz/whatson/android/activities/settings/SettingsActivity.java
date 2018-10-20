package xyz.whatson.android.activities.settings;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import xyz.whatson.android.R;
import xyz.whatson.android.model.Event;

public class SettingsActivity extends AppCompatPreferenceActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupActionBar();

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }



    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            final CheckBoxPreference checkBoxArt, checkBoxCulture, checkBoxSports, checkBoxMusic, checkBoxTech, checkBoxScience, checkBoxRecreation, checkBoxEducation;
            boolean artPref, culturePref, sportsPref, musicPref, techPref, sciencePref, recreationPref, educationPref;


            FirebaseDatabase database;
            DatabaseReference databaseReference;
            FirebaseUser user;
            String userID;

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
            checkBoxTech = (CheckBoxPreference) findPreference("techKey");
            checkBoxCulture = (CheckBoxPreference)findPreference("cultureKey");
            checkBoxEducation = (CheckBoxPreference)findPreference("educationKey");
            checkBoxMusic = (CheckBoxPreference)findPreference("musicKey");
            checkBoxRecreation = (CheckBoxPreference)findPreference("recreationKey");
            checkBoxScience = (CheckBoxPreference)findPreference("scienceKey");
            checkBoxSports = (CheckBoxPreference)getPreferenceManager().findPreference("sportsKey");
            checkBoxArt = (CheckBoxPreference)findPreference("artKey");

            database = FirebaseDatabase.getInstance();
            databaseReference = database.getReference();
            user = FirebaseAuth.getInstance().getCurrentUser();
            userID = user.getUid();
            final DatabaseReference ref = databaseReference.child("Users").child(userID);


            //tick the boxes for user's preferences
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for(DataSnapshot key: dataSnapshot.getChildren()){
                        String category = key.getKey().substring(0, 1).toUpperCase() + key.getKey().substring(1);
                        if(key.getValue().equals("true")){
                            if(category.equals("Art")) {
                                checkBoxArt.setChecked(true);
                            } else if (category.equals("Sports")){
                                checkBoxSports.setChecked(true);
                            } else if (category.equals("Culture")) {
                                checkBoxCulture.setChecked(true);
                            } else if (category.equals("Education")) {
                                checkBoxEducation.setChecked(true);
                            } else if (category.equals("Music")) {
                                checkBoxMusic.setChecked(true);
                            } else if (category.equals("Recreation")) {
                                checkBoxRecreation.setChecked(true);
                            } else if (category.equals("Science")) {
                                checkBoxScience.setChecked(true);
                            } else if (category.equals("Tech")) {
                                checkBoxTech.setChecked(true);
                            }
                        } else if(key.getValue().equals("false")){
                            if(category.equals("Art")) {
                                checkBoxArt.setChecked(false);
                            } else if (category.equals("Sports")){
                                checkBoxSports.setChecked(false);
                            } else if (category.equals("Culture")) {
                                checkBoxCulture.setChecked(false);
                            } else if (category.equals("Education")) {
                                checkBoxEducation.setChecked(false);
                            } else if (category.equals("Music")) {
                                checkBoxMusic.setChecked(false);
                            } else if (category.equals("Recreation")) {
                                checkBoxRecreation.setChecked(false);
                            } else if (category.equals("Science")) {
                                checkBoxScience.setChecked(false);
                            } else if (category.equals("Tech")) {
                                checkBoxTech.setChecked(false);
                            }
                        }

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });








            //SETTING LISTENERS TO UPDATE EACH PREFERENCE IF TICKED
            //adapted from https://stackoverflow.com/questions/28721192/android-one-clicklistener-for-many-checkboxpreference

            Preference.OnPreferenceClickListener sportsListener = new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    CheckBoxPreference pref = (CheckBoxPreference) findPreference("sportsKey");
                    if(pref.isChecked()) {
                        ref.child("sports").setValue("true");
                    } else {
                        ref.child("sports").setValue("false");
                    }

                    return false;
                }
            };
            checkBoxSports.setOnPreferenceClickListener(sportsListener);

            Preference.OnPreferenceClickListener artListener = new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    CheckBoxPreference pref = (CheckBoxPreference) findPreference("artKey");
                    if(pref.isChecked()) {
                        ref.child("art").setValue("true");
                    } else {
                        ref.child("art").setValue("false");
                    }

                    return false;
                }
            };
            checkBoxArt.setOnPreferenceClickListener(artListener);

            Preference.OnPreferenceClickListener cultureListener = new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    CheckBoxPreference pref = (CheckBoxPreference) findPreference("cultureKey");
                    if(pref.isChecked()) {
                        ref.child("culture").setValue("true");
                    } else {
                        ref.child("culture").setValue("false");
                    }

                    return false;
                }
            };
            checkBoxCulture.setOnPreferenceClickListener(cultureListener);


            Preference.OnPreferenceClickListener educationListener = new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    CheckBoxPreference pref = (CheckBoxPreference) findPreference("educationKey");
                    if(pref.isChecked()) {
                        ref.child("education").setValue("true");
                    } else {
                        ref.child("education").setValue("false");
                    }

                    return false;
                }
            };
            checkBoxEducation.setOnPreferenceClickListener(educationListener);

            Preference.OnPreferenceClickListener musicListener = new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    CheckBoxPreference pref = (CheckBoxPreference) findPreference("musicKey");
                    if(pref.isChecked()) {
                        ref.child("music").setValue("true");
                    } else {
                        ref.child("music").setValue("false");
                    }

                    return false;
                }
            };
            checkBoxMusic.setOnPreferenceClickListener(musicListener);

            Preference.OnPreferenceClickListener recreationListener = new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    CheckBoxPreference pref = (CheckBoxPreference) findPreference("recreationKey");
                    if(pref.isChecked()) {
                        ref.child("recreation").setValue("true");
                    } else {
                        ref.child("recreation").setValue("false");
                    }

                    return false;
                }
            };
            checkBoxRecreation.setOnPreferenceClickListener(recreationListener);

            Preference.OnPreferenceClickListener scienceListener = new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    CheckBoxPreference pref = (CheckBoxPreference) findPreference("scienceKey");
                    if(pref.isChecked()) {
                        ref.child("science").setValue("true");
                    } else {
                        ref.child("science").setValue("false");
                    }

                    return false;
                }
            };
            checkBoxScience.setOnPreferenceClickListener(scienceListener);

            Preference.OnPreferenceClickListener techListener = new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    CheckBoxPreference pref = (CheckBoxPreference) findPreference("techKey");
                    if(pref.isChecked()) {
                        ref.child("tech").setValue("true");
                    } else {
                        ref.child("tech").setValue("false");
                    }

                    return false;
                }
            };
            checkBoxTech.setOnPreferenceClickListener(techListener);


        }
    }


    private void setupActionBar() {
        ViewGroup rootView = (ViewGroup)findViewById(R.id.action_bar_root); //id from appcompat

        if (rootView != null) {
            View view = getLayoutInflater().inflate(R.layout.app_bar_settings, rootView, false);
            rootView.addView(view, 0);

            Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            // Respond to the action bar's Up/Home button
//            case android.R.id.home:
//                if(getFragmentManager().getBackStackEntryCount()>0)
//                    getFragmentManager().popBackStack();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}