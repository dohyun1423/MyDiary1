package com.cookandroid.project_diary;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class BasicScreen extends AppCompatActivity {

    Button btnToDo, btnDiary, btnDel;
    TextView nowDate;
    CalendarView calendarView;
    String selectYear, selectMonth, selectDay, id;
    LinearLayout linearLayout, nowLayout;
    // 삭제를 위한 변수
    int idToDelete = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basicscreen_main);
        setTitle("My Diary");

        btnToDo = (Button) findViewById(R.id.btnToDo);
        btnDiary = (Button) findViewById(R.id.btnDiary);
        btnDel = (Button) findViewById(R.id.btnDel);
        nowDate = (TextView) findViewById(R.id.nowDate);
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        nowLayout = (LinearLayout) findViewById(R.id.nowLayout);

        //id 받아오기
        id = getIntent().getStringExtra("ID");

        // 오늘 날짜 저장
        Calendar cal = Calendar.getInstance();
        int cYear = cal.get(Calendar.YEAR);
        int cMonth = cal.get(Calendar.MONTH) + 1;
        int cDay = cal.get(Calendar.DAY_OF_MONTH);
        //오늘 날짜 출력
        String today = cYear + "." + cMonth + "." + cDay;
        nowDate.setText(id + "님 반갑습니다.\n Today " + today);

        // 오늘 날짜를 선택된 상태로 설정
        calendarView.setDate(cal.getTimeInMillis(), false, true);
        selectYear = Integer.toString(cYear);
        selectMonth = Integer.toString(cMonth);
        selectDay = Integer.toString(cDay);
        showTodoList(selectYear + "_" + selectMonth + "_" + selectDay, id);

        // 저장된 배경색 불러오기
        loadBackgroundColor();

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

        // 일정 삭제
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 한 줄 삭제
                deleteTextViewById(idToDelete);
                idToDelete += 3;
                // 경로지정
                File folder = new File(getFilesDir(), "ID/" + id + "/todo");
                File file = new File(folder + "/" + selectYear + "_" + selectMonth + "_" + selectDay + ".txt");

                // 파일 존재 여부 확인
                if (file.exists()) {
                    // 파일 내용 읽어오기
                    String todoLog = readTodo(file.toString());

                    // 파일이 null이 아닌 경우에만 처리
                    if (todoLog != null) {
                        // 저장된 내용 \n으로 분리
                        String[] lines = todoLog.split("\n");

                        // 마지막 세 줄 제외하고 모두 삭제
                        StringBuilder updatedContent = new StringBuilder();
                        int linesToKeep = lines.length - 3;
                        for (int i = 0; i < linesToKeep; i++) {
                            updatedContent.append(lines[i]);
                            if (i < linesToKeep - 1) {
                                updatedContent.append("\n");
                            }
                        }

                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(updatedContent.toString().getBytes());
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            // 파일 쓰기 실패 처리
                            Toast.makeText(BasicScreen.this, "일정 삭제 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                        }

                        // 파일을 다시 읽어서 공백 여부 확인 후 파일 삭제
                        todoLog = readTodo(file.toString());
                        if (todoLog != null && todoLog.trim().isEmpty()) {
                            boolean deleteSuccess = file.delete();
                            if (deleteSuccess) {
                                Toast.makeText(BasicScreen.this, "일정이 모두 삭제되어 파일이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(BasicScreen.this, "일정이 모두 삭제되었지만 파일 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        // 파일 읽기 실패 처리
                        Toast.makeText(BasicScreen.this, "일정을 읽어오지 못했습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 파일이 존재하지 않을 경우 처리
                    Toast.makeText(BasicScreen.this, "삭제할 일정이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // todolist에 선택된 날짜 전송
        btnToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
//----------------------------------------------------------------------------------------------------------
    // 메뉴 설정
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        SubMenu subMenu = menu.addSubMenu("테마 변경>>");
        subMenu.add(0,1,0,"빨간색");
        subMenu.add(0,2,0,"초록색");
        subMenu.add(0,3,0,"파란색");
        subMenu.add(0,4,0,"보라색");
        subMenu.add(0,5,0,"노란색");
        menu.add(0,6,0, "로그아웃");
        menu.add(0,7,0, "회원탈퇴");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            // 배경 설정
            case 1:
                changeBackgroundColor("#ffdddd");
                break;
            case 2:
                changeBackgroundColor("#ddffdd");
                break;
            case 3:
                changeBackgroundColor("#c0e0ff");
                break;
            case 4:
                changeBackgroundColor("#ddddff");
                break;
            case 5:
                changeBackgroundColor("#ffffe0");
                break;
            case 6:
                Intent intent1 = new Intent(BasicScreen.this,
                        MainActivity.class);
                startActivity(intent1);

                break;
            case 7:
                // 다이얼로그
                AlertDialog.Builder dlg = new AlertDialog.Builder(BasicScreen.this);
                dlg.setTitle("회원탈퇴"); //제목
                dlg.setMessage("정말 탈퇴하시겠습니까?"); // 메시지
                dlg.setIcon(R.drawable.appdiary); // 아이콘 설정
                // 버튼 클릭시 동작
                dlg.setPositiveButton("확인",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(BasicScreen.this,
                                CheckPassword.class);
                        // checked_password로 ID 인탠트
                        intent.putExtra("ID", id);
                        startActivity(intent);
                    }
                });
                dlg.setNegativeButton("취소",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {}
                });
                dlg.show();

                break;

        }
        return super.onOptionsItemSelected(item);
    }
//----------------------------------------------------------------------------------------------------------
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
                view1.setId(i);
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
//----------------------------------------------------------------------------------------------------------
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

    // 배경색 변경 메소드
    private void changeBackgroundColor(String color) {
        calendarView.setBackgroundColor(Color.parseColor(color));
        nowDate.setBackgroundColor(Color.parseColor(color));
        nowLayout.setBackgroundColor(Color.parseColor(color));
        saveBackgroundColor(color); // 선택한 색상을 저장
    }

    // 배경색 저장 메소드
    private void saveBackgroundColor(String color) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyDiaryPrefs" + id, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("calendar_bg_color", color);
        editor.apply();
    }

    // 저장된 배경색 불러오기 메소드
    private void loadBackgroundColor() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyDiaryPrefs" + id, MODE_PRIVATE);
        String color = sharedPreferences.getString("calendar_bg_color", "#c0e0ff"); // 기본값은 파란색
        calendarView.setBackgroundColor(Color.parseColor(color));
        nowDate.setBackgroundColor(Color.parseColor(color));
        nowLayout.setBackgroundColor(Color.parseColor(color));
    }

    void deleteTextViewById(int id) {
        View viewToRemove = linearLayout.findViewById(id);
        if (viewToRemove != null && viewToRemove instanceof TextView) {
            linearLayout.removeView(viewToRemove);
        }
    }
}
