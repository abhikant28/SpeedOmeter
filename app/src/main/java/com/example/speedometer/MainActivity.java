package com.example.speedometer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    TextView tv_speed_mps,tv_speed_mph,tv_speed_kmph;
    LatLng a;
    LatLng b;
    double speed = 0;
    boolean first = true;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            startListening();
        }
    }

    public void startListening(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED);{
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if(location!=null) {
                    updateLocation(location.getLongitude(), location.getLatitude());
                }
            }
        };

        if (Build.VERSION.SDK_INT < 23) {
            startListening();
        }else{
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,locationListener);

            }
        }

        tv_speed_mps=findViewById(R.id.textViewMPS);
        tv_speed_kmph=findViewById(R.id.textViewKMPH);
        tv_speed_mph=findViewById(R.id.textViewMPH);

        Timer timer = new Timer();
        a=b;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(first){
                    a = b;
                    first=false;
                    Log.i("FIRST :::: ", String.valueOf(first));
                }
                Log.i("Location:::: b :::", b.toString());
                Log.i("Location:::: a :::", a.toString());
                speed = SphericalUtil.computeDistanceBetween(a, b);
                Log.i("SPEED:::", String.valueOf(speed));
                tv_speed_mps.post(new Runnable() {
                    public void run() {
                        tv_speed_mps.setText(String.valueOf((int)speed)+" m/s");
                    }
                });
                tv_speed_mph.post(new Runnable() {
                    public void run() {
                        tv_speed_mph.setText(String.valueOf((int)(speed*2.23694))+"  M/Hr");
                    }
                });
                tv_speed_kmph.post(new Runnable() {
                    public void run() {
                        tv_speed_kmph.setText(String.valueOf((int)(speed*3.6)+"  Km/Hr"));
                    }
                });
                a=b;
            }
        }, 5000, 1000);//wait 0 ms before doing the action and do it evry 1000ms (1second)
    }

    private void updateLocation(double longitude, double latitude) {
        b= new LatLng(latitude, longitude);
    }
}