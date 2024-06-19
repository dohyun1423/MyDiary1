package com.cookandroid.project_diary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

// 아이디와 비밀번호를 치고 로그인하는 메인시작화면
public class MainActivity extends AppCompatActivity {

    EditText edtId, edtPasswd;
    Button btnLogin;
    TextView joinMembership, searchPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        edtId = (EditText) findViewById(R.id.edtId);
        edtPasswd = (EditText) findViewById(R.id.edtPasswd);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        searchPwd = (TextView) findViewById(R.id.searchPwd);
        joinMembership = (TextView) findViewById(R.id.joinMembership);

        //로그인 버튼 클릭 시 아이디, 비밀번호 받아서 확인
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = edtId.getText().toString();
                String passwd = edtPasswd.getText().toString();

                // if 아이디가 없다면 경고문구
                File folder = new File(getFilesDir(), "ID");
                File fId = new File(folder, id);
                if (!fId.exists()) {
                    Toast.makeText(getApplicationContext(), "아이디가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                }else {   // 아이디가 맞을 때
                    String pwd = checkPassword(id);
                    if (passwd.equals(pwd)) { //비밀번호가 맞다면
                        Intent intent = new Intent(MainActivity.this, BasicScreen.class);
                        // id정보 전송
                        intent.putExtra("ID", id);
                        startActivity(intent);
                    }else if(passwd.isEmpty()){
                        Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요.",
                                Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "비밀번호가 틀립니다.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //비밀번호 찾는 화면으로 전환
        searchPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),
                        FindPassword.class);
                startActivity(intent);
            }
        });

        joinMembership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),
                        SaveIdActivity.class);
                startActivity(intent);
            }
        });
    }
    String checkPassword(String ID){
        String password = null;
        FileInputStream inFs;
        try {
            File file = new File(getFilesDir() + "/ID/", ID + "/" + ID + ".txt");
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