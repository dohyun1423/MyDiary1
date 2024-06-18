package com.cookandroid.project_diary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FindPassword extends AppCompatActivity {

    EditText findId;
    Button btnFindPwd, btnToLogin;
    TextView tvPwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_password);
        setTitle("비밀번호 찾기");

        findId = (EditText) findViewById(R.id.findId);
        btnFindPwd = (Button) findViewById(R.id.btnFindPwd);
        btnToLogin = (Button) findViewById(R.id.btnToLogin);
        tvPwd = (TextView) findViewById(R.id.tvPwd);

        btnFindPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = findId.getText().toString();
                if(id.isEmpty()){
                    Toast.makeText(getApplicationContext(),"아이디를 입력하세요",
                            Toast.LENGTH_SHORT).show();
                }else{
                    File folder = new File(getFilesDir(), "ID/" + id);
                    File fId = new File(folder, id + ".txt");
                    String pwd = readPassword(id);
                    if (!fId.exists()) {
                        Toast.makeText(getApplicationContext(), "아이디가 없습니다.",
                                Toast.LENGTH_SHORT).show();
                    }else {
                        tvPwd.setText("비밀번호 : " + pwd);
                        tvPwd.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        btnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(intent);
            }
        });
    }
    String readPassword(String ID){
        String password = null;
        FileInputStream inFs;
        try {
            File file = new File(getFilesDir() + "/ID" + "/" + ID, ID + ".txt");
            inFs = new FileInputStream(file);
            byte [] pwd = new byte[100];
            inFs.read(pwd);
            inFs.close();
            password = (new String(pwd)).trim();
        }catch (IOException ignored){
        }
        return password;
    }
}
