package com.example.androidproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.Map;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private DataBaseHelper dataBaseHelper = DataBaseHelper.instanceOfDatabase(this, this);
    private DateDBHelper dateDBHelper = DateDBHelper.instanceOfDatabase(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        inflater.inflate(R.menu.menu_exit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                FragmentManager fm = getSupportFragmentManager();
                Fragment toHide = fm.findFragmentById(R.id.main_container);
                FragmentTransaction ft = fm.beginTransaction();
                if (toHide != null) {
                    ft.hide(toHide);    // hide main fragment.
                }

                // This is the parent activity
                // Pay attention on note that the SettingFragment has
                ft.add(R.id.mainActivityLayoutID, new SettingFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.exit:
                MyExit newFragment = MyExit.newInstance();
                newFragment.show(getSupportFragmentManager(), "exitDialog");
                break;
        }
        return true;
    }


    // Nested class to show settings frag
    //Note: This class is Knows how to automatically and independently handle all the clicks
    // and changes that the user makes and saves them in a Preference file
    public static class SettingFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            //Create And show the settings fragemnt after press on settings
            setPreferencesFromResource(R.xml.prefernce, rootKey);


            ListPreference listPreference =(ListPreference) findPreference("listOfHoursPreference");
            listPreference.setSummary(listPreference.getValue());
            SwitchPreference switchPreference = (SwitchPreference) findPreference("remember");
            if(switchPreference.isChecked()){
                listPreference.setEnabled(true);
            }else{
                listPreference.setEnabled(false);
            }

        }

        //Listen to clicks on the any buttons/switches on the settings menu
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference pref = findPreference(key);

            if (pref instanceof ListPreference) {
                ListPreference listPref = (ListPreference) pref;
                pref.setSummary(listPref.getEntry());
                DateDBHelper dateDBHelper = DateDBHelper.instanceOfDatabase(getContext());
                String date = dateDBHelper.getDateFromDB();
                String time = dateDBHelper.getTimeFromDB();
                if((date.equals("") || date == null) && (time.equals("") || time == null)){
                    //DO NOT START ANY NEW ALARM
                    Log.e("onSharedPreferenceChanged", "Not start new alarm because not selected date and time");
                }else{ // Can cancel previous timer and start new
                    AlarmNotification alarmNotification = AlarmNotification.getAlarmInstance(getContext());
                    alarmNotification.setNewAlarm(date, time);
                    Log.e("onSharedPreferenceChanged", "Start new alarm");
                }
            }

            if(pref instanceof SwitchPreference){
                SwitchPreference switchPreference = (SwitchPreference) pref;

                ListPreference listPreference = (ListPreference) findPreference("listOfHoursPreference");

                if(switchPreference.isChecked()){
                    listPreference.setEnabled(true);
                }else{
                    listPreference.setEnabled(false);
                }


            }

            if(pref instanceof CheckBoxPreference){
                CheckBoxPreference alwaysNotification = (CheckBoxPreference) pref;
                if(alwaysNotification.isChecked()){
                    startService();
                }else{
                    stopService();
                }
            }
        }

        public void startService() {
            Intent serviceIntent = new Intent(getContext(), ForegroundService.class);
            ContextCompat.startForegroundService(getContext(), serviceIntent);
        }
        public void stopService() {
            Intent serviceIntent = new Intent(getContext(), ForegroundService.class);
            serviceIntent.setAction("StopService");
            ContextCompat.startForegroundService(getContext(), serviceIntent);

        }

        @Override
        public void onResume() {
            super.onResume();

            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();

            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

    }
}