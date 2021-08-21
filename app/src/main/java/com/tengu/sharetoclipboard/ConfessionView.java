package com.tengu.sharetoclipboard;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.util.Date;
/**
 * TODO: document your custom view class.
 */
public class ConfessionView extends AppCompatActivity {

    TextView confText;
    private static final int REQUEST_EXTERNAL_STORAGe = 1;
    private static final String[] permissionstorage = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_confession_view);
        verifystoragepermissions(this);


        confText = (TextView)findViewById(R.id.conftextView);

        confText.setText(getIntent().getStringExtra("mytext"));

        confText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(ConfessionView.this, "Captured", Toast.LENGTH_SHORT).show();
                screenshot(getWindow().getDecorView().getRootView(), "Confession");
                return true;
            }
        });
        confText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfessionView.super.finish();
            }
        });
    }

    protected static File screenshot(View view, String filename) {
        Date date = new Date();

        // Here we are initialising the format of our image name
        CharSequence format = android.text.format.DateFormat.format("yyyy-MM-dd_hh_mm_ss", date);
        try {
            // Initialising the directory of storage
            String dirpath = Environment.getExternalStorageDirectory() + "/DCIM/Confessions/";
            File file = new File(dirpath);
            if (!file.exists()) {
                System.out.println("Folder does not exist creating folder");
                boolean mkdir = file.mkdirs();
                System.out.println(mkdir);
            }

            // File name
            String path = dirpath + "/" + filename + "-" + format + ".png";
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);
            File imageurl = new File(path);
            FileOutputStream outputStream = new FileOutputStream(imageurl);
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
            outputStream.flush();
            outputStream.close();
            return imageurl;

        } catch (FileNotFoundException io) {
            io.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // verifying if storage permission is given or not
    public static void verifystoragepermissions(Activity activity) {

        int permissions = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // If storage permission is not given then request for External Storage Permission
        if (permissions != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, permissionstorage, REQUEST_EXTERNAL_STORAGe);
        }
    }
}