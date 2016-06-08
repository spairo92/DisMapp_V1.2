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
        String helpText ="DisMapp is a simple helping application for people with disabilities. The whole app is designed so people can share information and help one another. Google Maps is implemented and can be modified to suggest directions and places for people with special needs. These information is gathered from users itself. The data gathered should be stored on a cloud database (this app is using the local database), in this way will be accessible from every device.  \n" +
                "There are three main actions the user can take on this app:\n" +
                "\n" +
                "GETTING DIRECTIONS\n" +
                "With the press of the first button the map appears with all the saved markers and information save in the database. The user sees his location on the map and can get directions to different locations. The first click on the map registers the starting point, the second the destination point and after that the user can choose till 8 waypoints to modify the destination. With the click of the floating button the destination will appear. On-long-click the map clears itself.\n" +
                "\n" +
                "REPORTING ROAD  CONDITION \n" +
                "The second button is responsible on the report of road conditions. The users valuate ways on the map. They enter their comments and these informations are saved on the database to be shown at the main map.\n" +
                "\n" +
                "REPORTING BUILDING ACCESSIBILITY\n" +
                "This action is similar with the one from the second button. Just in this case the user reports buildings by clicking on their location and adding comments about their accessibility for people with disabilities.";
        // Change the textView content
        TextView infoTextView = (TextView) findViewById(R.id.textViewHelp);
        infoTextView.setText(helpText);
    }
}
