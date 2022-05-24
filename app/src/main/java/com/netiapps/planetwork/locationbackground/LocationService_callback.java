package com.netiapps.planetwork.locationbackground;


import com.google.android.gms.maps.model.LatLng;

public interface LocationService_callback {

    void onDataReceived(LatLng data);
}
