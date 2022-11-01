package com.example.storage;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ExternalStorage extends Activity implements OnClickListener {

    Button btnSave, readFromExternalStorage;
    private String filename = "MySampleFile.txt";
    private String filepath = "MyFileStorage";
    TextView responseText;
    EditText myInputText;
    File myExternalFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_external_storage);
        initView();
        // check if external storage is available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            btnSave.setEnabled(false);
        } else {
            myExternalFile = new File(getExternalFilesDir(filepath), filename);
        }

    }

    private void initView() {
        myInputText = (EditText) findViewById(R.id.myInputText);
        responseText = (TextView) findViewById(R.id.responseText);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        readFromExternalStorage = (Button) findViewById(R.id.btnDisplay);
        readFromExternalStorage.setOnClickListener(this);
    }
    public void onClick(View v) {

        String myData = "";
        switch (v.getId()) {
            case R.id.btnSave:
                try {
                    FileOutputStream fos = new FileOutputStream(myExternalFile);
                    fos.write(myInputText.getText().toString().getBytes());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                myInputText.setText("");
                responseText.setText("Dữ liệu đã được lưu vào bộ nhớ ngoài");
                break;

            case R.id.btnDisplay:
                try {
                    FileInputStream fis = new FileInputStream(myExternalFile);
                    DataInputStream in = new DataInputStream(fis);
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(in));
                    String strLine;
                    while ((strLine = br.readLine()) != null) {
                        myData = myData + strLine;
                    }
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                myInputText.setText(myData);
                responseText.setText("Được lấy ra từ bộ nhớ ngoài");
                break;
        }
    }

    /*
     * Kiểm tra xe bộ nhớ ngoài SDCard có readonly không vì nếu là readonly thì
     * không thể tạo file trên đó được
     */
    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    /*
     * Kiểmtra xem device có bộ nhớ ngoài không
     */
    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }
}