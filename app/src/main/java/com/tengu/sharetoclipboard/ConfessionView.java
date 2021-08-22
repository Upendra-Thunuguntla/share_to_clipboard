package com.tengu.sharetoclipboard;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

/**
 * TODO: document your custom view class.
 */
public class ConfessionView extends AppCompatActivity {

    LinearLayout bg;
    TextView confText;
    private static final int REQUEST_EXTERNAL_STORAGe = 1;
    private static final String[] permissionstorage = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confession);
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

        bg = (LinearLayout) findViewById(R.id.bgLayout);
        bg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(ConfessionView.this, "Captured", Toast.LENGTH_SHORT).show();
                screenshot(getWindow().getDecorView().getRootView(), "Confession");
                return true;
            }
        });
        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfessionView.super.finish();
            }
        });
    }

    protected File screenshot(View view, String filename) {
        Date date = new Date();

        // Here we are initialising the format of our image name
        CharSequence format = android.text.format.DateFormat.format("yyyy-MM-dd_hh_mm_ss", date);
        try {
            // Initialising the directory of storage
            String dirPath = Environment.getExternalStorageDirectory() + "/Confessions/";
            File confessionsDirectory = new File(dirPath);
            if (!confessionsDirectory.exists()) {
                System.out.println("Folder does not exist creating folder");
                boolean mkdir = confessionsDirectory.mkdirs();
                System.out.println(mkdir);
            }

            // File name
            String path = dirPath + "/" + filename + "-" + format + ".png";
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);
            File imageFile = new File(path);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            bitmap.recycle();

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Photo");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Edited");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis ());
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
            values.put(MediaStore.Images.ImageColumns.BUCKET_ID, imageFile.toString().toLowerCase(Locale.US).hashCode());
            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, imageFile.getName().toLowerCase(Locale.US));
            values.put("_data", imageFile.getAbsolutePath());

            ContentResolver cr = getContentResolver();
            cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            return imageFile;

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