package com.androidapp.achillion_security_scanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.loader.app.LoaderManager;

import android.Manifest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.audiofx.Equalizer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScannerView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

//import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CAMERA = 0;

    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Button qrCodeFoundButton;
    private String qrCode;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean requestingLocationUpdates = false;


    //private ZXingScannerView zXingScannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  zXingScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
      //  setContentView(zXingScannerView);


        if(isConnected()){
            previewView = findViewById(R.id.activity_main_previewView);

            qrCodeFoundButton = findViewById(R.id.activity_main_qrCodeFoundButton);
            qrCodeFoundButton.setVisibility(View.INVISIBLE);
            qrCodeFoundButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), qrCode, Toast.LENGTH_LONG).show();
                    Log.i(MainActivity.class.getSimpleName(), "Scanning QR Code: " + qrCode);

                }
            });


            cameraProviderFuture = ProcessCameraProvider.getInstance(this);
            requestCamera();

            IntentFilter intentFilter = new IntentFilter("com.androidapp.achillion_security_scanner");
            GpsLocationReceiver gpsLocationReceiver = new GpsLocationReceiver();
            registerReceiver(gpsLocationReceiver, intentFilter);
            senDataToReceiver();


            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            requestLocation();
        }else{
            showDialog();
        }



    }

    @Override
    protected void onStop() {
        super.onStop();
        requestingLocationUpdates = false;
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        requestingLocationUpdates = true;
        requestLocation();

    }


    private void requestCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            }
        }
    }

    private void requestLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //getLocation();
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            //fusedLocationProviderClient.requestLocationUpdates(manage)
            boolean managerEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);


            if (!managerEnabled || !GpsLocationReceiver.gpsOn) {
                buildAlertMessageNoGps();

            }else {

                requestingLocationUpdates = true;
                locationRequest = LocationRequest.create();
                locationRequest.setInterval(10000);
                locationRequest.setFastestInterval(5000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult == null) {
                            return;
                        }
                        for (Location location : locationResult.getLocations()) {
                            // Update UI with location data
                            // ...
                        }
                    }
                };
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location != null) {

                            try {
                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                Config.user_address = addresses.get(0).getAddressLine(0);
                                Config.lat = addresses.get(0).getLatitude();
                                Config.lon = addresses.get(0).getLongitude();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                if(requestingLocationUpdates){
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                            locationCallback,
                            Looper.getMainLooper());
                }


            }
        } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "Error starting camera " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraPreview(@NonNull ProcessCameraProvider cameraProvider) {
        previewView.setPreferredImplementationMode(PreviewView.ImplementationMode.SURFACE_VIEW);

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.createSurfaceProvider());

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new QRCodeImageAnalyzer(new QRCodeFoundListener() {
            @Override
            public void onQRCodeFound(String _qrCode) {
                qrCode = _qrCode;

                qrCodeFoundButton.setVisibility(View.VISIBLE);
                Intent i = new Intent(MainActivity.this, ResultActivity.class);
//Pass data to the SayHelloNewScreen Activity through the Intent
                CharSequence resultText = _qrCode;
                i.putExtra("savedResultText", resultText);
//Ask Android to start the new Activity
                startActivity(i);
            }

            @Override
            public void qrCodeNotFound() {
                qrCodeFoundButton.setVisibility(View.INVISIBLE);
            }
        }));

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageAnalysis, preview);
    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Location is required. Please enable your location and restart the application!");
                /*.setCancelable(false)
                .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        finish();
                    }
                });*/
        final AlertDialog alert = builder.create();
        alert.show();
        Thread terminate = new Thread() {
            public void run() {
                try {
                    // Thread will sleep for 5 seconds
                    sleep(10000);

                    // After 5 seconds redirect to another intent

                    //Remove activity
                    finish();
                } catch (Exception e) {
                }
            }
        };
        // start thread
        terminate.start();
    }



    private boolean isConnected(){
         ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
         NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
         NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

         if((wifiConn !=null && wifiConn.isConnected()) || (mobileConn !=null && mobileConn.isConnected())){
           return true;
        }else{
             return false;
         }
     }


     private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Network connection and location are required.\n Please enable your location," +
                "connect to the internet and restart the application!");
              /*  .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        finish();
                    }
                });*/
         final AlertDialog alert = builder.create();
         alert.show();
         Thread terminate = new Thread() {
             public void run() {
                 try {
                     // Thread will sleep for 5 seconds
                     sleep(10000);

                     // After 5 seconds redirect to another intent

                     //Remove activity
                     finish();
                 } catch (Exception e) {
                 }
             }
         };
         // start thread
         terminate.start();
     }


     private void senDataToReceiver(){
         final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
         //fusedLocationProviderClient.requestLocationUpdates(manage)
         boolean managerEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
         Intent intent = new Intent();
         intent.setAction("com.androidapp.achillion_security_scanner");
         intent.putExtra("data", managerEnabled);
         sendBroadcast(intent);
     }




}