package com.jorgesys.fileprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;


/**
 * For this example you must have a file test.pdf located in Environment.getExternalStorageDirectory().getPath() + "/Android/Data/"
 *
 */

public class MainActivity extends AppCompatActivity {

    private final static String TAG  = MainActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST = 255;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(getApplicationContext(), PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST );
            } else {
                //WRITE_EXTERNAL_STORAGE permission granted
                checkFile();
            }
        } else {
            //WRITE_EXTERNAL_STORAGE permission not required
            checkFile();
        }



    }


    private void checkFile(){

        //Path directory of the file we want to load.
        File documentsPath = new File(Environment.getExternalStorageDirectory().getPath() + "/Android/Data/");
        //If documentsPath doesn´t exists, then create
        if (!documentsPath.exists()) {
            Log.i(TAG, "create path: " + documentsPath);
            documentsPath.mkdir();
        }else{
            Log.i(TAG, "path: " + documentsPath + " exists!");
        }
        File file = new File(documentsPath, "test.pdf");

        if(file.exists()) {
            Log.i(TAG, "The file exists!, share file.");
            shareFile(file);
        }else{
            Log.e(TAG, "The file doesn´t exists!");
        }


    }

    private void shareFile(File file){

        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);

        Intent intent = ShareCompat.IntentBuilder.from(this)
                .setType("application/pdf")
                .setStream(uri)
                .setChooserTitle("Choose bar")
                .createChooserIntent()
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(intent);
    }


    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //WRITE_EXTERNAL_STORAGE permission granted
                    checkFile();
                } else {
                    Toast.makeText(getApplicationContext(), "The app was not allowed to write in your storage", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}
