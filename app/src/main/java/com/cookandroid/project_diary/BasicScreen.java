package com.cookandroid.project_diary;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;

public class BasicScreen extends AppCompatActivity {

    Button btnToDo, btnDiary;
    TextView nowDate;
    CalendarView calendarView;
    String selectYear, selectMonth, selectDay, id;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basicscreen_main);
        setTitle("My Diary");

        btnToDo = (Button) findViewById(R.id.btnToDo);
        btnDiary = (Button) findViewById(R.id.btnDiary);
        nowDate = (TextView) findViewById(R.id.nowDate);
        calendarView = (CalendarView) findViewById(R.id.calenderView);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        //id 받아오기
        id = getIntent().getStringExtra("ID");

        // 오늘 날짜 저장
        Calendar cal = Calendar.getInstance();
        int cYear = cal.get(Calendar.YEAR);
        int cMonth = cal.get(Calendar.MONTH) + 1;
        int cDay = cal.get(Calendar.DAY_OF_MONTH);
        //오늘 날짜 출력
        String today = cYear + "." + cMonth + "." + cDay;
        nowDate.setText("Today " + today);



        // 선택된 날짜 저장
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                // 선택된 날짜 저장
                selectYear = Integer.toString(year);
                selectMonth = Integer.toString(month+1);
                selectDay = Integer.toString(day);
                // 선택된 날짜 일정 보여주기
                String dayList = selectYear + "_" + selectMonth + "_" + selectDay;
                showTodoList(dayList, id);
            }
        });

        // todolist에 선택된 날짜 전송
        btnToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 날짜가 선택이 되지 않았으면 오늘 날짜
                if(selectYear == null || selectMonth == null || selectDay==null){
                    selectYear = Integer.toString(cYear);
                    selectMonth = Integer.toString(cMonth);
                    selectDay = Integer.toString(cDay);
                }
                // 선택된 날짜 정보 전송
                Intent intent = new Intent(BasicScreen.this, TodoListActivity.class);
                intent.putExtra("selectYear", selectYear);
                intent.putExtra("selectMonth", selectMonth);
                intent.putExtra("selectDay", selectDay);
                intent.putExtra("ID", id);
                // 화면이동
                startActivity(intent);

            }
        });

        // read_diary에 선택된 날짜 전송
        btnDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 날짜가 선택이 되지 않았으면 오늘 날짜
                if(selectYear == null || selectMonth == null || selectDay==null){
                    selectYear = Integer.toString(cYear);
                    selectMonth = Integer.toString(cMonth);
                    selectDay = Integer.toString(cDay);
                }
                // 선택된 날짜 정보 전송
                Intent intent = new Intent(BasicScreen.this, ReadActivity.class);
                intent.putExtra("selectYear", selectYear);
                intent.putExtra("selectMonth", selectMonth);
                intent.putExtra("selectDay", selectDay);
                intent.putExtra("ID", id);
                // 화면이동
                startActivity(intent);
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        menu.add(0,1,0, "로그아웃");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case 1:
                Intent intent = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 해당 날짜에 저장된 일정을 보여주는 메소드
    void showTodoList (String day, String id){
        // 기존 레이아웃의 자식 뷰 제거
        linearLayout.removeAllViews();
        // 경로지정
        File folder = new File(getFilesDir(), "ID/" + id + "/todo");
        File file = new File(folder + "/" + day + ".txt");
        // 내용 읽어오기
        String todoLog = readTodo(file.toString());
        if(file.exists()) {
            // 저장된 내용 \n으로 분리
            String [] line = todoLog.split("\n");
            int i=0;
            while(line.length > i+2){
                String time = line[i];
                String color = line[i+1];
                String list = line[i+2];
                // textview 추가
                TextView view1 = new TextView(this);
                String str = time + "   |   " + list;
                //view1 설정
                view1.setText(str);
                view1.setBackgroundColor(Color.parseColor(color));
                view1.setTextSize(20);
                Typeface typeFace = Typeface.createFromAsset(getAssets(),"bmyeonsung_ttf.ttf");
                view1.setTypeface(typeFace);
                view1.setHeight(100);
                view1.setPadding(10,10,10,10);
                view1.setTextColor(Color.BLACK);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                        .MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.CENTER;
                view1.setLayoutParams(lp);

                linearLayout.addView(view1);
                // 다음 저장된 일정 위치
                i=i+3;
            }
        }
    }

    String readTodo(String fName){
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
