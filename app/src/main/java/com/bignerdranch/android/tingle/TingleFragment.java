package com.bignerdranch.android.tingle;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.content.ActivityNotFoundException;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class TingleFragment extends Fragment{


    private Button addThing;
    private Button listAll;
    private Button scanBarcode;
    private Button takePicture;
    private File mPhotoFile;
    private ImageView lastAddedImage;
    private TextView lastAdded;
    private TextView newWhat, newWhere;
    private String barcodeName = null;
    private String image;
    private static final String TAG = "TingleFragment";
    private String searchWord;

    private static ThingsDB thingsDB;

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
                image = "tmp.jpg";
                // Handle successful camera action
                Toast toast = Toast.makeText(getActivity(), "Picture saved", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Handle cancel
                image = null;
                Toast toast = Toast.makeText(getActivity(), "Camera was Cancelled!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
            }
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fillThingsDB();

        //lastAdded = (TextView) findViewById(R.id.last_thing);
        //updateUI();
        setHasOptionsMenu(true);
        thingsDB = ThingsDB.get(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_tingle, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "QueryTextSubmit: " + s);
                Toast toast = Toast.makeText(getActivity(), "Searched for: " + s , Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
                searchWord = s;
                Intent i = new Intent(getActivity(), ListActivity.class);
                i.putExtra("searchWord", searchWord);
                startActivity(i);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(TAG, "QueryTextChange: " + s);
                return false;
            }
        });
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
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(dir);
        newdir.mkdirs();

        takePicture.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String file = dir + "tmp.jpg";
                File newfile = new File(file);
                try {
                    newfile.createNewFile();
                } catch (IOException e) {
                    Toast toast = Toast.makeText(getActivity(), "File was not created", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 25, 400);
                    toast.show();
                }
                Uri outputFileUri = Uri.fromFile(newfile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
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

                    File externalFilesDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/");
                    File imagefile = new File(externalFilesDir, "tmp.jpg");

                    if(imagefile.exists()) {
                        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/");
                        File from      = new File(directory, "tmp.jpg");
                        File to        = new File(directory, "IMG_" + newThing.getId() + ".jpg");
                        from.renameTo(to);
                    }

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
                i.putExtra("searchWord", (String) null);
                startActivity(i);
            }
        });

        lastAdded = (TextView) v.findViewById(R.id.last_thing);
        lastAddedImage = (ImageView) v.findViewById(R.id.last_thing_image);

        return v;
    }

    private void updateUI(UUID id) {

        int s = thingsDB.size();
        if(s>0) {
            lastAdded.setText(thingsDB.get(id).toString());
            mPhotoFile = thingsDB.getPhotoFile(thingsDB.get(id));
            if(mPhotoFile == null || !mPhotoFile.exists()) {
                lastAddedImage.setImageDrawable(null);
            }
            else {
                Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
                lastAddedImage.setImageBitmap(bitmap);
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
