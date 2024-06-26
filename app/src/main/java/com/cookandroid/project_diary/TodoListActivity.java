package com.cookandroid.project_diary;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TodoListActivity extends AppCompatActivity {

    String id, sYear,sMonth,sDay,sDate, color, time;
    TimePicker timePicker;
    Button redBtn, blueBtn, greenBtn, btnToDo, btnBack;
    EditText edtToDo;
    TextView selectDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todolist);

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        redBtn = (Button) findViewById(R.id.redBtn);
        greenBtn = (Button) findViewById(R.id.greenBtn);
        blueBtn = (Button) findViewById(R.id.blueBtn);
        btnToDo = (Button) findViewById(R.id.btnToDo);
        btnBack = (Button) findViewById(R.id.btnBack);
        edtToDo = (EditText) findViewById(R.id.edtToDo);
        selectDate = (TextView) findViewById(R.id.selectDate);
        
        // 전송받은 선택된 날짜 저장
        sYear = getIntent().getStringExtra("selectYear");
        sMonth = getIntent().getStringExtra("selectMonth");
        sDay = getIntent().getStringExtra("selectDay");
        sDate = sYear + "_" + sMonth + "_" + sDay;
        selectDate.setText(sDate);

        // 시간 저장
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
                if (hour > 12) {
                    hour -= 12;
                    time = "오후" + Integer.toString(hour) + "시" +
                            Integer.toString(minute) + "분";
                } else {
                    time = "오전" + Integer.toString(hour) + "시" +
                            Integer.toString(minute) + "분";
                }
            }
        });

        // 일정의 색을 정하는 버튼
        redBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color = "#ffbbbb";
            }
        });
        blueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color = "#bbbbff";
            }
        });
        greenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color = "#bbffbb";
            }
        });
//-------------------------------------------------------------------------------------------------------
// 파일 생성, 일정 등록
        // 일정 파일 위치 확인, 생성하기
        // ID 받아옴
        id = getIntent().getStringExtra("ID");
        File folder = new File(getFilesDir(), "ID/" + id + "/todo");
        if (!folder.exists()) { //폴더가 없다면 폴더 생성
            boolean result = folder.mkdirs();
        }
        File file = new File(folder + "/" + sDate + ".txt");

        //뒤로 돌아가기
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TodoListActivity.this, BasicScreen.class);
                // id정보 전송
                intent.putExtra("ID", id);
                startActivity(intent);
            }
        });

        // 일정 등록하기
        btnToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 일정, 시간, 색이 모두 선택되었는지 확인
                if(edtToDo.getText() == null || time == null || color == null){
                    if(edtToDo.getText() == null){
                        Toast.makeText(getApplicationContext(), "일정을 입력하세요",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(time == null){
                        Toast.makeText(getApplicationContext(), "시간을 입력하세요",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(color == null){
                        Toast.makeText(getApplicationContext(), "색을 입력하세요",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else {
                    try {
                        // 이전 일정 내용
                        String todoLog = readTodo(file.toString());
                        // 이전의 일정 내용과 합쳐서 저장
                        String todoStr = edtToDo.getText().toString();
                        FileOutputStream fos = new FileOutputStream(file);
                        if (todoLog != null) {
                            fos.write((todoLog + "\n" + time + "\n" + color + "\n" +
                                    todoStr).getBytes());
                            fos.close();
                        } else {
                            fos.write((time + "\n" + color + "\n" + todoStr).getBytes());
                            fos.close();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Intent intent = new Intent(TodoListActivity.this,
                            BasicScreen.class);
                    intent.putExtra("ID", id);
                    startActivity(intent);
                }
            }
        });
    }
    // 저장된 일정 읽어오기
    String readTodo(String fName){
        String todoStr = null;
        FileInputStream inFs;
        try{
            inFs = new FileInputStream(fName);
            byte[] txt = new byte[3000];
            inFs.read(txt);
            inFs.close();
            todoStr = (new String(txt)).trim();
        }catch (IOException ignored){
        }
        return todoStr;
    }
}
