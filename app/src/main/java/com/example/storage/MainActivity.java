package com.example.storage;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    Button btnTakePhoto, btnSave, btnDisplay;
    ImageView imgViewTakePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnSave = findViewById(R.id.btnSave);
        btnDisplay = findViewById(R.id.btnDisplay);
        imgViewTakePhoto = findViewById(R.id.imgViewTakePhoto);

        // Kiểm tra quyền truy cập
        ActivityCompat.requestPermissions(
                MainActivity.this,
                new String[] {Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                0);

        btnTakePhoto.setOnClickListener(v -> pick_from_camera(v));

        btnDisplay.setOnClickListener(v -> pick_from_gallery(v));

        btnSave.setOnClickListener(v -> {
            try {
                saveToGallery();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // Nhận kết quả từ hai hàm pick_from_camera và pick_from_gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: // Hàm pick_from_camera
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras(); // getExtras() trả về một Bundle chứa dữ liệu
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imgViewTakePhoto.setImageBitmap(imageBitmap);
                }
                break;
            case 1: // Hàm pick_from_gallery
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    imgViewTakePhoto.setImageURI(selectedImage);
                }
                break;
        }
    }

    public void pick_from_camera(View view) {
        Intent takepicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takepicture, 0);
    }

    public void pick_from_gallery(View view) {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // URL
        startActivityForResult(pickPhoto, 1);
    }

    private void saveToGallery() throws IOException {

        // Các code dưới đây chỉ chạy được trên Android 10 trở lên
        Uri imagesUri;

        ContentResolver contentResolver = getContentResolver();

        // Lấy đường dẫn của thư mục Pictures
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imagesUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        // k1 - value 1
        // k2 - value 2
        // Lưu dữ liệu trong ContentValues
        ContentValues contentValues = new ContentValues();
        //putting file information in content values
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME,
                System.currentTimeMillis() + ".jpg"); //Generating a file name
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*");

        // Inserting the contentValues to contentResolver and getting the Uri
        Uri uri = contentResolver.insert(imagesUri, contentValues);

        try {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) imgViewTakePhoto.getDrawable();

            Bitmap bitmap = bitmapDrawable.getBitmap();

            //Opening an outputStream with the Uri that we got
            OutputStream outputStream = contentResolver.openOutputStream(Objects.requireNonNull(uri));

            //Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            // Closing the output stream
            Objects.requireNonNull(outputStream);

            Toast.makeText(this, "Lưu thành công", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}




