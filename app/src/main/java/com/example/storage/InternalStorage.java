package com.example.storage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class InternalStorage extends AppCompatActivity implements View.OnClickListener{

    Button btnSave, btnDisplay;
    EditText myInputText;
    TextView responseText;
    //Tên file được tạo
    private String filename = "internalStorage.txt";

    //Thư mục do mình đặt
    private String filepath = "ThuMucCuaToi";
    File myInternalFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        ContextWrapper contextWrapper = new ContextWrapper(
                getApplicationContext());
        //Tạo (Hoặc là mở file nếu nó đã tồn tại) Trong bộ nhớ trong có thư mục là ThuMucCuaToi.
        File directory = contextWrapper.getDir(filepath, Context.MODE_PRIVATE);
        myInternalFile = new File(directory, filename);
        //Gọi hàm initView

    }

    private void initView() {
        myInputText = (EditText) findViewById(R.id.myInputText);
        responseText = (TextView) findViewById(R.id.responseText);
        // Các sự kiện
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        btnDisplay = (Button) findViewById(R.id.btnDisplay);
        btnDisplay.setOnClickListener(this);
    }

    public void onClick(View v) {

        String myData = "";
        switch (v.getId()) {
            case R.id.btnSave:
                try {
                    //Mở file
                    FileOutputStream fos = new FileOutputStream(myInternalFile);
                    //Ghi dữ liệu vào file
                    fos.write(myInputText.getText().toString().getBytes());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                myInputText.setText("");
                responseText
                        .setText("Đã được lưu vào bộ nhớ trong");
                break;

            case R.id.btnDisplay:
                try {
                    //Đọc file
                    FileInputStream fis = new FileInputStream(myInternalFile);
                    DataInputStream in = new DataInputStream(fis);
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(in));
                    String strLine;
                    //Đọc từng dòng
                    while ((strLine = br.readLine()) != null) {
                        myData = myData + strLine;
                    }
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                myInputText.setText(myData);
                responseText
                        .setText("Lấy dữ liệu từ bộ nhớ trong");
                break;
        }
    }
}