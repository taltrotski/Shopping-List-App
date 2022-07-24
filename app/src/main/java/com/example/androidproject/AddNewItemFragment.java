package com.example.androidproject;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddNewItemFragment extends Fragment {

    private static final String TAG = "MainActivity";


    private Button btnAddItem;
    private EditText edName, edQuantity;
    private MainViewModel mainViewModel;
    private String name, quantity, prevName;
    boolean update = false;
    Integer position = 0;
    private Button plusBtn, minusBtn;

    private DataBaseHelper dataBaseHelper = DataBaseHelper.instanceOfDatabase(getActivity(), getContext());

    public AddNewItemFragment(String name, String quantity, Integer position) {
        this.name = name;
        this.quantity = quantity;
        this.position = position;
        if (!(name == "")){
            prevName = name;
            update = true; // for updating existing item
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_new_item, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mainViewModel = MainViewModel.getInstance(getActivity().getApplication(), getContext(), getActivity());

        btnAddItem = view.findViewById(R.id.btnAddItem);
        edName = view.findViewById(R.id.editTextName);
        edQuantity = view.findViewById(R.id.editTextQuantity);
        plusBtn = view.findViewById(R.id.button_add);
        minusBtn = view.findViewById(R.id.button_down);

        edName.setText(name);
        edQuantity.setText(quantity);

        //Increase -  On Click
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity_value = Integer.parseInt(edQuantity.getText().toString());
                quantity_value++;
                edQuantity.setText(String.valueOf(quantity_value));
            }
        });

        //Decrease -  On Click
        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity_value = Integer.parseInt(edQuantity.getText().toString());
                if (quantity_value > 1)
                    quantity_value--;
                edQuantity.setText(String.valueOf(quantity_value));
            }
        });

        //Add new Item - On Click
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edName.getText().toString().matches("") || edQuantity.getText().toString().matches("")){
                    Toast toast = Toast.makeText(getContext(), R.string.please_fill_all_details, Toast.LENGTH_SHORT);
                    toast.show();
                }else{

                    boolean res = dataBaseHelper.checkIfItemExist(edName.getText().toString());
                    if(update){ // edit mode
                        if(res == false ||  prevName.equals(edName.getText().toString())){ // not exist or same name
                            Toast toast = Toast.makeText(getContext(), R.string.the_item_updated, Toast.LENGTH_SHORT);
                            toast.show();
                            dataBaseHelper.updateItemInDB(prevName, edName.getText().toString(), edQuantity.getText().toString());

                            mainViewModel.readDataFromDB();
                            getFragmentManager().beginTransaction().
                                    addToBackStack(null).
                                    replace(R.id.main_container, new ShoppingFragment(), "ADD_NEW_ITEM_FRAGMENT").
                                    commit();

                        }else{ // the new name are already exists
                            Toast toast = Toast.makeText(getContext(), R.string.the_item_already_exist, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                    else{ // new item mode
                        if(res == false){ // name are not exist
                            Toast toast = Toast.makeText(getContext(), R.string.new_item_added_to_the_list, Toast.LENGTH_SHORT);
                            toast.show();
                            dataBaseHelper.addItemToDatabase(edName.getText().toString(),edQuantity.getText().toString());

                            mainViewModel.readDataFromDB();
                            getFragmentManager().beginTransaction().
                                    addToBackStack(null).
                                    replace(R.id.main_container, new ShoppingFragment(), "ADD_NEW_ITEM_FRAGMENT").
                                    commit();
                        }else{ // ame are exist
                            Toast toast = Toast.makeText(getContext(), R.string.the_item_already_exist, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }
}
