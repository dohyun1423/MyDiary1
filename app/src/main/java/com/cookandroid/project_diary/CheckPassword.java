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
    Button btnCheckPwd, btnBack;
    EditText edtCheckPwd;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checked_password);

        btnCheckPwd = (Button) findViewById(R.id.btnCheckPwd);
        btnBack = (Button) findViewById(R.id.btnBack);
        edtCheckPwd = (EditText) findViewById(R.id.edtCheckPwd);

        // 아이디 전달받기
        id = getIntent().getStringExtra("ID");
        File folder = new File(getFilesDir(), "ID/" + id);

        pwd = readPassword(id);

        btnCheckPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredPwd = edtCheckPwd.getText().toString();
                if(pwd.equals(enteredPwd)){
                    // 비밀번호 일치 시 폴더 내 파일들을 모두 삭제하고 폴더를 삭제
                    deleteFolderContentAndFolder(folder);

                    // MainActivity로 이동
                    Intent intent = new Intent(CheckPassword.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // 현재 액티비티 종료
                } else {
                    Toast.makeText(getApplicationContext(), "비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckPassword.this,
                        BasicScreen.class);
                intent.putExtra("ID", id);
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
    // 폴더 내 파일들을 모두 삭제하고 폴더도 삭제하는 메서드
    private void deleteFolderContentAndFolder(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteFolderContentAndFolder(file); // 재귀적으로 폴더 내 파일들 삭제
                    } else {
                        file.delete(); // 파일 삭제
                    }
                }
            }
        }
        folder.delete(); // 폴더 삭제
    }
}
