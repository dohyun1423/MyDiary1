package com.cookandroid.project_diary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CheckPassword extends AppCompatActivity {
    String id, pwd;
    Button btnCheckPwd;
    EditText edtCheckPwd;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checked_password);

        btnCheckPwd = (Button) findViewById(R.id.btnCheckPwd);
        edtCheckPwd = (EditText) findViewById(R.id.edtCheckPwd);

        // 아이디 전달받기
        id = getIntent().getStringExtra("ID");
        File folder = new File(getFilesDir(), "ID/" + id);
        File file = new File(getFilesDir(), "ID/" + id + "/" + id +".txt");

        if(file.exists()) {
            pwd = readPwd(file.toString());
        }else{
            Toast.makeText(getApplicationContext(), "아이디가 존재하지 않습니다.",
                    Toast.LENGTH_SHORT).show();
        }

        btnCheckPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pwd.equals(edtCheckPwd.toString())){
                    boolean delete = folder.delete();
                    Intent intent = new Intent(CheckPassword.this,
                            MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), "비밀번호가 틀립니다.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    String readPwd(String fName){
        String todoStr = null;
        FileInputStream inFs;
        try{
            inFs = new FileInputStream(fName);
            byte[] txt = new byte[3000];
            inFs.read(txt);
            inFs.close();
            todoStr = (new String(txt));
        }catch (IOException ignored){
        }
        return todoStr;
    }
}
