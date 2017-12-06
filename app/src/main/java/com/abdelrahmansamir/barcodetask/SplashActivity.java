package com.abdelrahmansamir.barcodetask;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {
    boolean isBack;
    Thread threadMainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        threadMainActivity = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1300);
                } catch (InterruptedException e) {

                }
                if (!isBack) {
                    Intent intentMainActivity = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intentMainActivity);
                    finish();
                }
            }
        });


        // check camera permission if sdk 5.1.1 or low

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(SplashActivity.this, "Camera Permission Denied.", Toast.LENGTH_LONG).show();
                finish();
            } else {
                threadMainActivity.start();
            }

            // check camera permission if sdk 6.0.1 or high

        } else {

            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
            } else {
                threadMainActivity.start();
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        // permission result

        switch (requestCode) {

            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    threadMainActivity.start();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissions[0])) {
                            showDialog(true);
                        } else {
                            showDialog(false);
                        }
                    }
                }

                break;
            }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    public void onBackPressed() {
        isBack = true;
        super.onBackPressed();
    }

    public void showDialog(final boolean showRequestDialog) {

        // dialog for permission denied
        AlertDialog builder = new AlertDialog.Builder(SplashActivity.this).create();
        builder.setMessage("Please ... Accept Camera permission to scan barcode or QR code.");
        builder.setCancelable(false);
        builder.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (showRequestDialog) {
                    // show request again
                    ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                } else {

                    //Allow permission from settings
                    Intent settingsIntent = new Intent();
                    settingsIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    settingsIntent.setData(Uri.parse("package:" + SplashActivity.this.getPackageName()));
                    SplashActivity.this.startActivityForResult(settingsIntent, 10);
                }
            }
        });

        builder.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
                Toast.makeText(SplashActivity.this, "Sorry ... You can't use this application without accept Camera permission.", Toast.LENGTH_LONG).show();
            }
        });

        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {
            if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
            } else {
                threadMainActivity.start();
            }
        }

    }


}
