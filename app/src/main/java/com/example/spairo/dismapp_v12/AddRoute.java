package com.example.spairo.dismapp_v12;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Spairo on 5/30/2016.
 */
public class AddRoute extends FragmentActivity {
    GoogleMap map;
    Polyline polyline;
    ArrayList<LatLng> markerPoints;
    String[] LIST = {"good","bad"};
    String lineColor;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference goodRef = database.getReference("GoodState");
    DatabaseReference badRef = database.getReference("BadState");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_route);

        // Initializing
        markerPoints = new ArrayList<LatLng>();

        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting reference to float button
        ImageButton fab = (ImageButton) findViewById(R.id.fab);

        // Getting Map for the SupportMapFragment
        map = fm.getMap();

        //Enabling MyLocation Layer on Map
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);

        //initializing an invisible polyline
        polyline = map.addPolyline(new PolylineOptions().add(new LatLng(51.5, -0.1), new LatLng(40.7, -74.0)).visible(false));

        //Move camera over greece
        LatLng athens = new LatLng(37.9430, 23.6470);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(athens, 18));

        // Setting onclick event listener for the map
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {

                // Already 10 locations with 8 waypoints and 1 start location and 1 end location.
                // Upto 8 waypoints are allowed in a query for non-business users
                if(markerPoints.size()>=2){
                    return;
                }

                // Adding new item to the ArrayList
                markerPoints.add(point);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(point);

                /*
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED and
                 * for the rest of markers, the color is AZURE
                 */
                if(markerPoints.size()==1){
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }else if(markerPoints.size()==2){
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }else{
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                }

                // Add new marker to the Google Map Android API V2
                map.addMarker(options);
            }
        });

        // The map will be cleared on long click
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng point) {
                // Removes all the points from Google Map
                map.clear();
                // Removes all the points in the ArrayList
                markerPoints.clear();
            }
        });

        // Click event handler for Button btn_draw
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Checks, whether start and end locations are captured
                if(markerPoints.size() >= 2){
                    //remove previous direction line
                    polyline.remove();

                    //Initialize choices dialog box
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddRoute.this);
                    builder.setTitle("Select road's conditions ");
                    builder.setItems(LIST,new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(LIST[which].equals("good")) {
                                lineColor="green";
                                LatLng origin = markerPoints.get(0);
                                LatLng dest = markerPoints.get(1);

                                //object with origin and destination coordinates
                                Coordinates orgDestCoords = new Coordinates(origin, dest);
                                //push (creates distinct id) to firebase database
                                goodRef.push().setValue(orgDestCoords);

                                // Getting URL to the Google Directions API
                                String url = getDirectionsUrl(origin, dest);

                                AddRoute.DownloadTask downloadTask = new AddRoute.DownloadTask();

                                // Start downloading json data from Google Directions API
                                downloadTask.execute(url);
                            }
                            else if(LIST[which].equals("bad")){
                                lineColor="red";
                                LatLng origin = markerPoints.get(0);
                                LatLng dest = markerPoints.get(1);

                                //object with origin and destination coordinates
                                Coordinates orgDestCoords = new Coordinates(origin, dest);
                                //push (creates distinct id) to firebase database
                                badRef.push().setValue(orgDestCoords);

                                // Getting URL to the Google Directions API
                                String url = getDirectionsUrl(origin, dest);

                                AddRoute.DownloadTask downloadTask = new AddRoute.DownloadTask();

                                // Start downloading json data from Google Directions API
                                downloadTask.execute(url);
                            }
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();




                }
            }
        });
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Waypoints
        String waypoints = "";
        for(int i=2;i<markerPoints.size();i++){
            LatLng point  = (LatLng) markerPoints.get(i);
            if(i==2)
                waypoints = "waypoints=";
            waypoints += point.latitude + "," + point.longitude + "|";
        }

        //Travel Mode
        String mode ="mode=walking";
        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+waypoints+"&"+mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception downld url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            AddRoute.ParserTask parserTask = new AddRoute.ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

                ArrayList<LatLng> points = null;
                PolylineOptions lineOptions = null;

                // Traversing through all the routes
                for(int i=0;i<result.size();i++){
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for(int j=0;j<path.size();j++){
                        HashMap<String,String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    if(lineColor.equals("green")) {
                        lineOptions.color(Color.GREEN);
                        lineOptions.width(12);
                    }else if(lineColor.equals("red")){
                        lineOptions.color(Color.RED);
                        lineOptions.width(12);
                    }else{
                        lineOptions.color(Color.BLUE);
                        lineOptions.width(8);
                    }
                }

                // Drawing polyline in the Google Map for the i-th route
                polyline = map.addPolyline(lineOptions);

        }
    }

}