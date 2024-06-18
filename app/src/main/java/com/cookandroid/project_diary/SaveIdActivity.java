package com.cookandroid.project_diary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveIdActivity extends AppCompatActivity {

    EditText edtJoinId, edtJoinPasswd;
    Button btnJoinMember;
    TextView backToLogin, searchPwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_the_membership);
        setTitle("회원가입");

        Intent intent = getIntent();

        edtJoinId = (EditText) findViewById(R.id.edtJoinId);
        edtJoinPasswd = (EditText) findViewById(R.id.edtJoinPasswd);
        backToLogin = (TextView) findViewById(R.id.backToLogin);
        searchPwd = (TextView) findViewById(R.id.searchPwd);

        btnJoinMember = (Button) findViewById(R.id.btnJoinMember);

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(intent);
            }
        });

        searchPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),
                        FindPassword.class);
                startActivity(intent);
            }
        });


        btnJoinMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = edtJoinId.getText().toString();
                String passwd = edtJoinPasswd.getText().toString();
                //아이디를 입력하지 않았다면
                if(id.isEmpty()){
                    Toast.makeText(getApplicationContext(),"아이디를 입력하세요",
                            Toast.LENGTH_SHORT).show();
                }else if(passwd.isEmpty()){ //비밀번호를 입력하지 않았다면
                    Toast.makeText(getApplicationContext(),"비밀번호를 입력하세요",
                            Toast.LENGTH_SHORT).show();
                }else { // 아이디와 비밀번호를 입력했다면
                    File folder = new File(getFilesDir(), "ID");
                    if (!folder.exists()) { //폴더가 없다면
                        // ID폴더 생성
                        boolean result = folder.mkdirs();
                        if (!result) {
                            Toast.makeText(getApplicationContext(), "폴더 생성 실패", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    // folder 안에 id폴더 생성
                    File fId = new File(folder, id);
                    if (fId.exists()) {
                        Toast.makeText(getApplicationContext(), "이미 존재하는 ID입니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        boolean result2 = fId.mkdirs();
                        try {
                            FileOutputStream fos = new FileOutputStream(fId + "/" + id + ".txt");
                            fos.write(passwd.getBytes());
                            fos.close();
                            Toast.makeText(getApplicationContext(), "회원가입 완료, 로그인 하세요", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(), "파일 쓰기 오류 발생", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
}
