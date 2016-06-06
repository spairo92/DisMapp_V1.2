package com.example.spairo.dismapp_v12;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends Activity {

    // The imageButtons of the main screen
    ImageButton imBtnDirection, imBtnRoute, imBtnMark, imBtnHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ImageButtons linking with the ImageButtons existing in the
        // activity_main.xml file
        imBtnDirection = (ImageButton) findViewById(R.id.imageButtonDirection);
        imBtnRoute = (ImageButton) findViewById(R.id.imageButtonRoute);
        imBtnMark = (ImageButton) findViewById(R.id.imageButtonMark);
        imBtnHelp = (ImageButton) findViewById(R.id.imageButtonHelp);
    }

    public void onClick(View view) {
        if (view == imBtnDirection) {
            // Go to the Info activity
            Intent i = new Intent(MainActivity.this, GetDirection.class);
            startActivity(i);
        }
        if (view == imBtnRoute) {
            // Go to the Info activity
            Intent i = new Intent(MainActivity.this, AddRoute.class);
            startActivity(i);
        }
        if (view == imBtnMark) {
            // Go to the Info activity
            Intent i = new Intent(MainActivity.this, AddMark.class);
            startActivity(i);
        }
        if (view == imBtnHelp) {
            // Go to the Info activity
            Intent i = new Intent(MainActivity.this, Help.class);
            startActivity(i);
        }

    }


}
