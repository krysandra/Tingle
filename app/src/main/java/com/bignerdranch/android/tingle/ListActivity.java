package com.bignerdranch.android.tingle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class ListActivity extends AppCompatActivity {

    private static ThingsDB thingsDB;
    ListView listView;
    private static List<Thing> thingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listView = (ListView) findViewById(R.id.thingsList);

        thingsDB = ThingsDB.get(this);
        thingList = thingsDB.getThingsDB();

        final ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.activity_list_item, android.R.id.text1, thingList);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder adb = new AlertDialog.Builder(ListActivity.this);
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete this item?");
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        thingsDB.deleteThing(position);
                        adapter.notifyDataSetChanged();
                    }


                });

                adb.show();

            }
        });
    }

}
