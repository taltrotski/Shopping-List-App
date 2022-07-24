package com.example.androidproject;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class MainViewModel extends AndroidViewModel  {

    private static MainViewModel instance;

    public Context context;
    public Activity activity;

    // ******* The observable vars *********************
    //  The MutableLiveData class has the setValue(T) and postValue(T)
    //  methods publicly and you must use these if you need to edit the value stored in a LiveData object.
    //  Usually, MutableLiveData is used in the ViewModel and then the ViewModel only exposes

    private MutableLiveData<ArrayList<Item>>  itemLiveData ;
    private MutableLiveData<Integer> positionSelected;
    // *****************************

    public MainViewModel(@NonNull Application application, Context context, Activity activity) {
        super(application);
        // call your Rest API in init method
        this.activity = activity;
        this.context = context;

        init();
    }

    public MutableLiveData<ArrayList<Item>>  getItemsLiveData() {
        return itemLiveData;
    }

    public void setPositionSelected(Integer index){
        positionSelected.setValue(index);
    }

    public MutableLiveData<Integer> getPositionSelected(){
        return positionSelected;
    }

    // Pay attention that MainViewModel is singleton it helps
    public static MainViewModel getInstance(Application application, Context context, Activity activity){
        if(instance ==null){
            instance = new MainViewModel(application, context, activity);
        }
        return instance;
    }
    // This use the setValue
    public void init(){
        itemLiveData = new MutableLiveData<>();
        positionSelected = new MutableLiveData<>();
        positionSelected.setValue(-1);
        readDataFromDB();
    }

    public void readDataFromDB() {
        DataBaseHelper dataBaseHelper = DataBaseHelper.instanceOfDatabase(activity,context);
        itemLiveData.setValue(dataBaseHelper.getDataFromDB());
    }
}
