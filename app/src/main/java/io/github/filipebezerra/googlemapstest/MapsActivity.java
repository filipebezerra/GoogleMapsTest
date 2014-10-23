package io.github.filipebezerra.googlemapstest;

import android.app.Dialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;

import com.github.johnpersano.supertoasts.SuperToast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Base:
 *  http://wptrafficanalyzer.in/blog/gps-and-google-map-in-android-applications-series/
 *  http://developer.android.com/google/play-services/maps.html
 *  http://www.vogella.com/tutorials/AndroidLocationAPI/article.html
 *
 *
 */
public class MapsActivity extends ActionBarActivity implements LocationListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private String provider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    public void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    public void onPause() {
        locationManager.removeUpdates(this);
    }

    private void setUpMapIfNeeded() {
        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        // Showing status
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        } else {
            // Do a null check to confirm that we have not already instantiated the map.
            if (mMap == null) {
                // Try to obtain the map from the SupportMapFragment.
                mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                        .getMap();

                // Enabling MyLocation Layer of Google Map
                mMap.setMyLocationEnabled(true);
                UiSettings mMapUiSettings = mMap.getUiSettings();
                mMapUiSettings.setRotateGesturesEnabled(true);
                mMapUiSettings.setMyLocationButtonEnabled(true);
                mMapUiSettings.setCompassEnabled(true);

                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

                // Check if we were successful in obtaining the map.
                if (mMap != null) {
                    setUpMyLocation();
                    setUpListeners();
                }
            }
        }
    }

    private void setUpListeners() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                makeToast("I'm a marker man").show();
                return true;
            }
        });
    }

    private SuperToast makeToast(final String text) {
        SuperToast toast = new SuperToast(MapsActivity.this);
        toast.setAnimations(SuperToast.Animations.FLYIN);
        toast.setText(text);
        toast.setIcon(SuperToast.Icon.Light.INFO, SuperToast.IconPosition.LEFT);
        toast.setDuration(SuperToast.Duration.MEDIUM);

        return toast;
    }

    private void setUpMyLocation() {
        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        }

        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        } else {
            // Getting the name of the best provider
            if (provider == null) {
                // Creating a criteria object to retrieve provider
                Criteria criteria = new Criteria();
                provider = locationManager.getBestProvider(criteria, true);
            }

            // Getting Current Location
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                onLocationChanged(location);
            }

            locationManager.requestLocationUpdates(provider, 20000, 0, this);
        }
    }

    public void onLocationChanged(Location location) {
        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Showing the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        addMarker(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        makeToast("You turned on the GPS man").show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        makeToast("You turned off the GPS man").show();
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void addMarker(Location location) {
        mMap.addMarker(
                new MarkerOptions()
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .title("Eu")
                        .snippet("This is my location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))
        );
    }

}
