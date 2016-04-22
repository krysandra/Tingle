package com.bignerdranch.android.tingle;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.ActivityNotFoundException;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class TingleFragment extends Fragment{


    private Button addThing;
    private Button listAll;
    private Button scanBarcode;
    private Button takePicture;
    private ImageView lastAddedImage;
    private TextView lastAdded;
    private TextView newWhat, newWhere;
    private String barcodeName = null;

    //fake database
    private static ThingsDB thingsDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fillThingsDB();

        //lastAdded = (TextView) findViewById(R.id.last_thing);
        //updateUI();

        thingsDB = ThingsDB.get(getActivity());

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_tingle, container, false);

        //button
        addThing = (Button) v.findViewById(R.id.add_button);
        listAll = (Button) v.findViewById(R.id.list_button);
        try {
        scanBarcode = (Button) v.findViewById(R.id.barcode_button);
        scanBarcode.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
                startActivityForResult(intent, 0);
            }

        });

        } catch (ActivityNotFoundException anfe) {
            Log.e("onCreate", "Scanner Not Found", anfe);
        }
        takePicture = (Button) v.findViewById(R.id.picture_button);

        takePicture.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1);
            }

        });

        newWhat = (TextView) v.findViewById(R.id.what_text);
        newWhere = (TextView) v.findViewById(R.id.where_text);

        addThing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((newWhat.getText().length() > 0 || barcodeName != null) && (newWhere.getText().length() > 0))
                {
                    String whatThing;
                    if(barcodeName != null) {
                        whatThing = barcodeName;
                        barcodeName = null;
                    }
                    else {
                        whatThing = newWhat.getText().toString();
                    }

                    Thing newThing = new Thing(whatThing,
                    newWhere.getText().toString());
                    thingsDB.addThing(newThing);
                    newWhat.setText(""); newWhere.setText("");
                    updateUI(newThing.getId());

                    if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        //Call the Fragmentmanager
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        Fragment fragment_list = (ListFragment) manager.findFragmentById(R.id.fragment_list);

                        //Use the manager to begin transaction, and remove the fragment
                        manager.beginTransaction()
                                .remove(fragment_list)
                                .commit();

                        //Create a new Fragment (ListFragment.java)
                        fragment_list = new ListFragment();
                        //Use the manager to begin transaction, and add the new(updated) fragment
                        manager.beginTransaction()
                                .add(R.id.fragment_list, fragment_list)
                                .commit();
                    }

                }


            }
        });

        listAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ListActivity.class);
                startActivity(i);
            }
        });

        lastAdded = (TextView) v.findViewById(R.id.last_thing);

        return v;
    }

    private void updateUI(UUID id) {

        int s = thingsDB.size();
        if(s>0) {
            lastAdded.setText(thingsDB.get(id).toString());
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                // Handle successful scan
                new FetchOutpanTask().execute(contents);

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Handle cancel
                Toast toast = Toast.makeText(getActivity(), "Scan was Cancelled!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
            }
        }
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String contents = intent.getStringExtra("CAMERA_RESULT");
                String format = intent.getStringExtra("CAMERA_RESULT_FORMAT");
                // Handle successful camera action
                Toast toast = Toast.makeText(getActivity(), "Camera contents: " + contents + ", format: " +format, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Handle cancel
                Toast toast = Toast.makeText(getActivity(), "Camera was Cancelled!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
            }
        }

    }

    private void barcodeName(String name) {
        if(name != "null") {
            barcodeName = name;
            Toast toast = Toast.makeText(getActivity(), "Barcode scanned:" + name , Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();
            newWhat.setText(name);
        }
        else {
            barcodeName = null;
            Toast toast = Toast.makeText(getActivity(), "The barcode scanner did unfortunately not find the item", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();
        }


    }

    private class FetchOutpanTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            /*
            try
            {
                byte[] result = new NetworkFetcher().getProductInfo("https://api.outpan.com/v2/products/"+params[0]+"?apikey=3acf04d2d2ae779b6471a8ff9efa0356");
                Log.i(TAG, "Fetched contents of URL: " + result);
                return result;
            }
            catch (IOException ioe)
            {
                Log.e(TAG, "Failed to fetch URL: ", ioe);
            }
            */
            return new NetworkFetcher().fetchItems(params[0]);
        }

        @Override
        protected void onPostExecute(String name) {
            barcodeName(name);
        }

    }

}
