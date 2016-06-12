package com.example.spairo.dismapp_v12;

import android.app.Dialog;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Spairo on 5/30/2016.
 */
public class AddRoute extends FragmentActivity {

    GoogleMap map;
    String[] LIST = {"good","average","bad"};
    String color = "asdf";
    String title, comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_route);

        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if(status!= ConnectionResult.SUCCESS){ // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        }else { // Google Play Services are available

            // Getting reference to the SupportMapFragment of activity_main.xml
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting GoogleMap object from the fragment
            map = fm.getMap();

            //Enabling MyLocation Layer on Map
            map.setMyLocationEnabled(true);

            //Move camera over greece
            LatLng athens = new LatLng(37.9430, 23.6470);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(athens, 13));
        }

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(final LatLng point) {
                final Dialog dialog1 = new Dialog(AddRoute.this);
                dialog1.setTitle("Report Status");
                dialog1.setContentView(R.layout.markercomment_customdialog);
                dialog1.show();
                final EditText titleText =(EditText)dialog1.findViewById(R.id.title);
                final EditText commentText =(EditText)dialog1.findViewById(R.id.comment);
                Button addButton = (Button)dialog1.findViewById(R.id.addBtn);

                addButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        title = titleText.getText().toString();
                        comment = commentText.getText().toString();

                        // Drawing marker on the map
                        drawMarker(point, color, title, comment);

                        // Creating an instance of ContentValues
                        ContentValues contentValues = new ContentValues();

                        // Setting latitude in ContentValues
                        contentValues.put(LocationsDB.FIELD_LAT, point.latitude );

                        // Setting longitude in ContentValues
                        contentValues.put(LocationsDB.FIELD_LNG, point.longitude);

                        // Setting color in ContentValues
                        contentValues.put(LocationsDB.FIELD_COLOR, color);

                        // Setting title in ContentValues
                        contentValues.put(LocationsDB.FIELD_TITLE, title);

                        // Setting comment in ContentValues
                        contentValues.put(LocationsDB.FIELD_COMMENT, comment);

                        // Creating an instance of LocationInsertTask
                        LocationInsertTask insertTask = new LocationInsertTask();

                        // Storing the latitude, longitude and zoom level to SQLite database
                        insertTask.execute(contentValues);

                        Toast.makeText(getApplicationContext(), "Marker is added to the Map", Toast.LENGTH_SHORT).show();
                        dialog1.cancel();

                    }
                });
            }

        });


        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {

                // Removing all markers from the Google Map
                map.clear();

                // Creating an instance of LocationDeleteTask
                LocationDeleteTask deleteTask = new LocationDeleteTask();

                // Deleting all the rows from SQLite database table
                deleteTask.execute();

                Toast.makeText(getBaseContext(), "All markers are removed", Toast.LENGTH_LONG).show();

            }
        });
    }


    private void drawMarker(LatLng point, String color, String title, String comment){
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title(title).snippet(comment);
        Toast.makeText(getBaseContext(), "Color problem", Toast.LENGTH_SHORT).show();

        // Adding marker on the Google Map
        map.addMarker(markerOptions);
    }


    private class LocationInsertTask extends AsyncTask<ContentValues, Void, Void> {
        @Override
        protected Void doInBackground(ContentValues... contentValues) {

            /** Setting up values to insert the clicked location into SQLite database */
            getContentResolver().insert(LocationsContentProvider.CONTENT_URI, contentValues[0]);
            return null;
        }
    }

    private class LocationDeleteTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {

            /** Deleting all the locations stored in SQLite database */
            getContentResolver().delete(LocationsContentProvider.CONTENT_URI, null, null);
            return null;
        }
    }

}
