package com.example.spairo.dismapp_v12;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

    GoogleMap googleMap;

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
            googleMap = fm.getMap();

            // Invoke LoaderCallbacks to retrieve and draw already saved locations in map
            getSupportLoaderManager().initLoader(0, null, this);
        }

        googleMap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {


                // Drawing marker on the map
                drawMarker(point);

                // Creating an instance of ContentValues
                ContentValues contentValues = new ContentValues();

                // Setting latitude in ContentValues
                contentValues.put(LocationsDB.FIELD_LAT, point.latitude );

                // Setting longitude in ContentValues
                contentValues.put(LocationsDB.FIELD_LNG, point.longitude);

                // Setting zoom in ContentValues
                contentValues.put(LocationsDB.FIELD_ZOOM, googleMap.getCameraPosition().zoom);

                // Creating an instance of LocationInsertTask
                LocationInsertTask insertTask = new LocationInsertTask();

                // Storing the latitude, longitude and zoom level to SQLite database
                insertTask.execute(contentValues);

                Toast.makeText(getBaseContext(), "Marker is added to the Map", Toast.LENGTH_SHORT).show();

            }
        });


        googleMap.setOnMapLongClickListener(new OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {

                // Removing all markers from the Google Map
                googleMap.clear();

                // Creating an instance of LocationDeleteTask
                LocationDeleteTask deleteTask = new LocationDeleteTask();

                // Deleting all the rows from SQLite database table
                deleteTask.execute();

                Toast.makeText(getBaseContext(), "All markers are removed", Toast.LENGTH_LONG).show();

            }
        });
    }


    private void drawMarker(LatLng point){
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(point).title("title").snippet("this is the snippet").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

        // Adding marker on the Google Map
        googleMap.addMarker(markerOptions);
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
        float zoom=0;

        // Number of locations available in the SQLite database table
        locationCount = arg1.getCount();

        // Move the current record pointer to the first row of the table
        arg1.moveToFirst();

        for(int i=0;i<locationCount;i++){

            // Get the latitude
            lat = arg1.getDouble(arg1.getColumnIndex(LocationsDB.FIELD_LAT));

            // Get the longitude
            lng = arg1.getDouble(arg1.getColumnIndex(LocationsDB.FIELD_LNG));

            // Get the zoom level
            zoom = arg1.getFloat(arg1.getColumnIndex(LocationsDB.FIELD_ZOOM));

            // Creating an instance of LatLng to plot the location in Google Maps
            LatLng location = new LatLng(lat, lng);

            // Drawing the marker in the Google Maps
            drawMarker(location);

            // Traverse the pointer to the next row
            arg1.moveToNext();
        }

        if(locationCount>0){
            // Moving CameraPosition to last clicked position
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat,lng)));

            // Setting the zoom level in the map on last position  is clicked
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
    }
}