package com.example.androidproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> {

    private static ArrayList<Item> itemList;
    private int selectedRow = -1;
    private MainViewModel myViewModel;
    private Application Mycontext;
    private IItemsAdapterListener editListener;
    private ItemViewHolder viewHolder;
    private Context context;
    private Activity activity;
    private DataBaseHelper dataBaseHelper = DataBaseHelper.instanceOfDatabase(activity , context);


    public ItemsAdapter(Application application, Context context, Activity activity,IItemsAdapterListener editListener) {
        Mycontext = application;
        myViewModel = MainViewModel.getInstance(application, Mycontext, activity);
        itemList = myViewModel.getItemsLiveData().getValue();
        this.context = context;
        this.editListener = editListener;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      //  listener = (IItemsAdapterListener)parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext()); // instance of the inflater
        View countryView = inflater.inflate(R.layout.item_row, parent, false); // get view of the item view object
        viewHolder = new ItemViewHolder(countryView);
        return viewHolder; // return item view holder
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.bindData(item.getName(),item.getQuantity());

        //Remove Button - remove item from the shopping list
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage(R.string.remove_item);
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                int position = holder.getAdapterPosition();

                                itemList.remove(position);
                                //myViewModel.setItemLiveData(itemList);
                                dataBaseHelper.deleteItemFromDB(item.getName());
                                // Here we do some logic
                                // if the position equals to the current selected row so we need to unselected completely the selected row
                                if(position == myViewModel.getPositionSelected().getValue())
                                    myViewModel.setPositionSelected(-1);

                                if(position < myViewModel.getPositionSelected().getValue())
                                    myViewModel.setPositionSelected(myViewModel.getPositionSelected().getValue()-1);

                                notifyDataSetChanged();
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();


            }
        });

        //Edit Button - edit item details
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editListener.editClicked(holder.itemName.getText().toString(),holder.itemQuantity.getText().toString(),holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // Each row in RecyclerView will get reference of this ItemsViewHolder
    // *** Include the function that can remove the row
    public class ItemViewHolder extends RecyclerView.ViewHolder
    {
        private final Context context;
        private final TextView itemName;
        private final TextView  itemQuantity;
        private final Button btnEdit;
        private final Button btnRemove;

        private LinearLayout row_linearLayout;

        public LinearLayout getRow(){
            return row_linearLayout;
        }

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            itemName = itemView.findViewById(R.id.itemName);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            btnEdit = itemView.findViewById(R.id.btnEditItem);
            btnRemove = itemView.findViewById(R.id.btnRemoveItem);

            //row_linearLayout    = itemView.findViewById(R.id.item_row);
        }

        //******** This function bind\connect the row widgets with the data
        public void bindData(String name, int quantity){
            itemName.setText(name);
            itemQuantity.setText(String.valueOf(quantity));
        }
    }

    // interface for edit item
    public interface IItemsAdapterListener {
        void editClicked(String name,String quantity,Integer position);
    }
}
