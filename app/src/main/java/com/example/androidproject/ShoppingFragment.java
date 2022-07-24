package com.example.androidproject;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ShoppingFragment extends Fragment implements ItemsAdapter.IItemsAdapterListener {

    private static final String TAG = "ShoppingFragment";

    private DateDBHelper dateDBHelper = DateDBHelper.instanceOfDatabase(getContext());

    private RecyclerView recyclerView;
    private ItemsAdapter itemsAdapter;


    private TextView tvDatePicker, tvTimePicker;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimerSetListener;

    private Button btnAddNewItem, btnSaveDateAndTime;

    private String date, time;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.shopping_fragment, container, false);
    }

    //*******************************************************************
    // After activity created we can set the ADAPTER for the recycle view
    //*******************************************************************
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        itemsAdapter = new ItemsAdapter(getActivity().getApplication(), getContext(), getActivity(), this); // create an instance of the adapter
        recyclerView.setAdapter(itemsAdapter); // set that adapter for the recycle view
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); // What is the position of the list vertical or linear
        Log.e("COUNT", String.valueOf((itemsAdapter.getItemCount())));

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        btnSaveDateAndTime = view.findViewById(R.id.btnSaveDateAndTime);
        recyclerView = view.findViewById(R.id.recycle_view);
        tvDatePicker = (TextView) view.findViewById(R.id.tvDate);
        tvTimePicker = (TextView) view.findViewById(R.id.tvTime);

        btnAddNewItem = (Button) view.findViewById(R.id.btnAddNewButton);

        LoadDateFromDB();

        /********************************************************************/
        /*********************** Date Picker ********************************/
        /********************************************************************/
        tvDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeZone(TimeZone.getTimeZone("GMT+3"));

                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(getContext(),
                        R.style.DateTimeDialog,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                month = month + 1;
                                Log.d(TAG, "onDateSet: dd/mm/yyy: " + day + "/" + month + "/" + year);

                                String date = day + "/" + month + "/" + year;
                                tvDatePicker.setText(date);
                                tvDatePicker.setTextColor(Color.parseColor("#ff2d57"));
                                if (tvTimePicker.getText().toString().length() >= 1)
                                    btnSaveDateAndTime.setVisibility(View.VISIBLE);
                            }
                        },
                        year, month, day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();


            }
        });


        /********************************************************************/
        /*********************** Time Picker ********************************/
        /********************************************************************/
        tvTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeZone(TimeZone.getTimeZone("GMT+3"));

                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minutes = cal.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(getContext(), R.style.DateTimeDialog,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                String time = String.format("%02d", sHour) + ":" + String.format("%02d", sMinute);
                                tvTimePicker.setText(time);
                                tvTimePicker.setTextColor(Color.parseColor("#ff2d57"));
                                if (tvDatePicker.getText().toString().length() >= 1)
                                    btnSaveDateAndTime.setVisibility(View.VISIBLE);
                            }
                        }, hour, minutes, true);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();
            }
        });

        /********************************************************************/
        /*********************** Save date and time ********************************/
        /********************************************************************/
        btnSaveDateAndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTimeToDB(tvTimePicker.getText().toString());
                addDateToDB(tvDatePicker.getText().toString());
                btnSaveDateAndTime.setVisibility(View.INVISIBLE);

                AlarmNotification alarmNotification = AlarmNotification.getAlarmInstance(getContext());
                alarmNotification.setNewAlarm(tvDatePicker.getText().toString(), tvTimePicker.getText().toString());
            }
        });

        /********************************************************************/
        /*********************** Add new Item ********************************/
        /********************************************************************/
        btnAddNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getFragmentManager().beginTransaction().
                        addToBackStack(null).
                        replace(R.id.main_container, new AddNewItemFragment("", "1", 0), "ADD_NEW_ITEM_FRAGMENT").
                        commit();
            }
        });


        super.onViewCreated(view, savedInstanceState);
    }

    //editClicked - react to edit button from item adapter
    @Override
    public void editClicked(String name, String quantity, Integer position) {

        getFragmentManager().beginTransaction().
                addToBackStack(null).
                replace(R.id.main_container, new AddNewItemFragment(name, quantity, position), "ADD_NEW_ITEM_FRAGMENT").
                commit();


    }

    public void LoadDateFromDB() {
        date = dateDBHelper.getDateFromDB();
        time = dateDBHelper.getTimeFromDB();
        if (date != "") {
            tvDatePicker.setText(date);
            tvDatePicker.setTextColor(Color.parseColor("#ff2d57"));
        }
        if (time != "") {
            tvTimePicker.setText(time);
            tvTimePicker.setTextColor(Color.parseColor("#ff2d57"));
        }
    }

    public void addDateToDB(String newDate) {
        if (time != "" || date != "")
            dateDBHelper.updateItemInDB(date, newDate, time);
        else
            dateDBHelper.addItemToDatabase(newDate, time);
        date = newDate;
    }

    public void addTimeToDB(String newTime) {
        if (time != "" || date != "")
            dateDBHelper.updateItemInDB(date, date, newTime);
        else
            dateDBHelper.addItemToDatabase(date, newTime);
        time = newTime;
    }

}
