package com.cookandroid.project_diary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ReadActivity extends AppCompatActivity {

    TextView selectDate;
    EditText edtDiary;
    Button btnWrite, btnBack;
    String id, sYear,sMonth,sDay,sDate;
    WebView web;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_diary);
        setTitle("일기 작성");

        selectDate = (TextView) findViewById(R.id.selectDate);
        edtDiary = (EditText) findViewById(R.id.edtDiary);
        btnWrite = (Button) findViewById(R.id.btnWrite);
        btnBack = (Button) findViewById(R.id.btnBack);
        web = (WebView) findViewById(R.id.webView);

        // 전송받은 선택된 날짜 저장
        sYear = getIntent().getStringExtra("selectYear");
        sMonth = getIntent().getStringExtra("selectMonth");
        sDay = getIntent().getStringExtra("selectDay");
        sDate = sYear + "_" + sMonth + "_" + sDay;
        selectDate.setText(sDate);

        // 일기 읽어오기
        id = getIntent().getStringExtra("ID");
        File folder = new File(getFilesDir(), "ID/" + id + "/diary");
        if (!folder.exists()) { //폴더가 없다면 폴더 생성
            boolean result = folder.mkdirs();
        }
        File file = new File(getFilesDir(), "ID/" + id + "/diary/" + sDate + ".txt");
        // 파일이 있다면
        if (file.exists()){    //원래 적힌 일기 읽어오기
            String diaryLog = readDiary(file.toString());
            edtDiary.setText(diaryLog);
        }

        //뒤로 돌아가기
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReadActivity.this, BasicScreen.class);
                // id정보 전송
                intent.putExtra("ID", id);
                startActivity(intent);
            }
        });
        // 일기 작성하기
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 모두 입력해야함
                if(edtDiary.getText() == null) {
                    Toast.makeText(getApplicationContext(), "일기를 입력하세요",
                            Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        String diaryStr = edtDiary.getText().toString();
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(diaryStr.getBytes());
                        fos.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                Intent intent = new Intent(ReadActivity.this,
                        BasicScreen.class);
                intent.putExtra("ID", id);
                startActivity(intent);
            }
        });

        web.setWebViewClient(new CookWebViewClient());

        WebSettings webSet = web.getSettings();
        webSet.setBuiltInZoomControls(true);
        webSet.setJavaScriptEnabled(true);
        web.loadUrl("https://www.google.co.kr/maps");

    }
    String readDiary(String fName){
        String diaryStr = null;
        FileInputStream inFs;
        try{
            inFs = new FileInputStream(fName);
            byte[] txt = new byte[3000];
            inFs.read(txt);
            inFs.close();
            diaryStr = (new String(txt)).trim();
        }catch (IOException ignored){
        }
        return diaryStr;
    }

    class  CookWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
