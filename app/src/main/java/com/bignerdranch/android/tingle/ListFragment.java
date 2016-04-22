package com.bignerdranch.android.tingle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class ListFragment extends Fragment {

    private static ThingsDB thingsDB;
    ListView listView;
    private static List<Thing> thingList;
    private ArrayAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        thingsDB = ThingsDB.get(getActivity());
        thingList = thingsDB.getThingsDB();

        adapter = new ArrayAdapter(getActivity(),
                android.R.layout.activity_list_item, android.R.id.text1, thingList);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_list, container, false);

        listView = (ListView) v.findViewById(R.id.thingsList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
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


        return v;
    }

}
