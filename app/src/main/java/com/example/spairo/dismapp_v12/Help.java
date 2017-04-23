package com.example.spairo.dismapp_v12;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

/**
 * Created by Spairo on 5/30/2016.
 */
public class Help extends FragmentActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        //Text explaining the use of app
        String helpText ="DisMapp is a simple helping application for people with disabilities. The whole app is designed so people can share information and help one another. Google Maps is implemented and can be modified to suggest directions and places for people with special needs. These information is gathered from users itself. The data gathered about accessible routes are stored on a cloud database, in this way are accessible from every device.  \n" +
                "There are three main actions the user can take on this app:\n" +
                "\n" +
                "GETTING DIRECTIONS\n" +
                "At first view, on the map are visible all the routes ranked as accessible and the marks, the client has saved. Through a “long touch” on the map, not accessible paths are reveled as well. The My Location button appears in the top right corner of the screen. By clicking it, the camera centers the map on the current location of the device. The location is indicated by a small blue dot. At this point we have a fully updated map.\n" +
                "\n"+
                "To get directions the user should select a starting point and the ending point. Starting point can be either his location, or any point on the map. The most efficient route with time as primary factor will be calculated. Our client checks if these directions are convenient according to accessibility too. For example the calculated route my pass through inaccessible paths. In this case, the user checks for accessible paths, and selects them by putting waypoints. In the end he presses the Directions button for the most efficient route, based on time and accessibility factors. \n" +
                "\n" +
                "REPORTING ROAD  CONDITION \n" +
                "The user selects to “Rank a Route” from the actions given, and a map implemented from Google Maps, will be displayed. Now he can navigate through it and zoom in at the area he is interested. After finding the route he is searching for, points on the map the starting and ending point. After ranking the path the app will do the rest saving the necessary data on the cloud database, to be accessible for the other users too.\n" +
                "\n" +
                "REPORTING BUILDING ACCESSIBILITY\n" +
                "First thing is to choose the action of “Mark Area”. On the map displayed, user can either navigate or go directly to his location. After finding the interested area, points on map at the coordinates he wants to indicate. Then there is the possibility to add a comment, this way he can give details about the specific spot, why it is accessible or not.";
        // Change the textView content
        TextView infoTextView = (TextView) findViewById(R.id.textViewHelp);
        infoTextView.setText(helpText);
    }
}
