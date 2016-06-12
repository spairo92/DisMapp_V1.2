package com.example.spairo.dismapp_v12;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AddMark extends FragmentActivity implements LoaderCallbacks<Cursor> {

    GoogleMap map;
    String[] LIST = {"good","average","bad"};
    String color = "asdf";
    String title, comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_mark);

        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        }else { // Google Play Services are available

            // Getting reference to the SupportMapFragment of activity_main.xml
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting GoogleMap object from the fragment
            map = fm.getMap();

            //Enabling MyLocation Layer on Map
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            map.setMyLocationEnabled(true);

            // Invoke LoaderCallbacks to retrieve and draw already saved locations in map
            getSupportLoaderManager().initLoader(0, null, this);

            //Move camera over greece
            LatLng athens = new LatLng(37.9430, 23.6470);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(athens, 13));
        }

        map.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapClick(final LatLng point) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AddMark.this);
                builder.setTitle("Select building's state ");
                builder.setItems(LIST,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(LIST[which].equals("good")) {
                            color="green";
                            onChoiceClick(point, color);
                        }
                        else if(LIST[which].equals("average")){
                            color="orange";
                            onChoiceClick(point, color);
                        }
                        else if(LIST[which].equals("bad")){
                            color="red";
                            onChoiceClick(point, color);
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
            public void onChoiceClick(final LatLng point, final String color){
                final Dialog dialog1 = new Dialog(AddMark.this);
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
                        dialog1.cancel();;

                    }
                });
            }

        });


        map.setOnMapLongClickListener(new OnMapLongClickListener() {
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

        if(color.equals("green")) {
            // Setting latitude and longitude for the marker
            markerOptions.position(point).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title(title).snippet(comment);
        }
        else if(color.equals("orange")){
            markerOptions.position(point).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).title(title).snippet(comment);
        }
        else if(color.equals("red")){
            markerOptions.position(point).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title(title).snippet(comment);
        }else{
            markerOptions.position(point).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title(title).snippet(comment);
        }
        // Adding marker on the Google Map
        map.addMarker(markerOptions);
    }


    private class LocationInsertTask extends AsyncTask<ContentValues, Void, Void>{
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


    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {

        // Uri to the content provider LocationsContentProvider
        Uri uri = LocationsContentProvider.CONTENT_URI;

        // Fetches all the rows from locations table
        return new CursorLoader(this, uri, null, null, null, null);

    }


    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        int locationCount = 0;
        double lat=0;
        double lng=0;
        String col, tit, com;

        // Number of locations available in the SQLite database table
        locationCount = arg1.getCount();

        // Move the current record pointer to the first row of the table
        arg1.moveToFirst();

        for(int i=0;i<locationCount;i++){

            // Get the latitude
            lat = arg1.getDouble(arg1.getColumnIndex(LocationsDB.FIELD_LAT));

            // Get the longitude
            lng = arg1.getDouble(arg1.getColumnIndex(LocationsDB.FIELD_LNG));

            // Get color
            col = arg1.getString(arg1.getColumnIndex(LocationsDB.FIELD_COLOR));

            // Get title
            tit = arg1.getString(arg1.getColumnIndex(LocationsDB.FIELD_TITLE));

            // Get comment
            com = arg1.getString(arg1.getColumnIndex(LocationsDB.FIELD_COMMENT));

            // Creating an instance of LatLng to plot the location in Google Maps
            LatLng location = new LatLng(lat, lng);

            // Drawing the marker in the Google Maps
            drawMarker(location, col, tit, com);

            // Traverse the pointer to the next row
            arg1.moveToNext();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
    }
}