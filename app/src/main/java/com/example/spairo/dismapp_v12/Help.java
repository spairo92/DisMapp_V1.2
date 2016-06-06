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
        String helpText = "The Decision Support Systems Laboratory (DSS Lab) was founded in the year 2001 at the Department of Informatics, University of Piraeus. The DSS Lab supports relative undergraduate and postgraduate courses, at theoretical as well as practical levels by giving the students experience in the use of decision support software. Its research activities are focused on the fields of decision support systems, performance measurement, multi-criteria decision making, knowledge management, data analysis and data mining. Often, members of the DSS Lab organize special streams and invited sessions in International Conferences on Operational Research.";
        // Change the textView content
        TextView infoTextView = (TextView) findViewById(R.id.textViewHelp);
        infoTextView.setText(helpText);
    }
}
