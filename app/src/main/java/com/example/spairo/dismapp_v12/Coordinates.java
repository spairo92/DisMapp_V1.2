package com.example.spairo.dismapp_v12;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Spairo on 10/25/2016.
 */

public class Coordinates {

    private LatLng origin;
    private LatLng dest;

    public Coordinates() {
    }

    public Coordinates(LatLng origin, LatLng dest) {
        this.origin = origin;
        this.dest = dest;
    }

    public LatLng getOrigin() {
        return origin;
    }

    public LatLng getDest() {
        return dest;
    }
}
