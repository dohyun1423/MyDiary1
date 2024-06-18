package com.cookandroid.project_diary;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class ReadActivity<PlacesClient, FindCurrentPlaceRequest> extends AppCompatActivity {

    TextView selectDate;
    EditText edtDiary, edtLocation;
    Button btnWrite, btnBack, btnSearch, btnZoomIn, btnZoomOut;
    String id, sYear,sMonth,sDay,sDate;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_diary);
        setTitle("일기 작성");

        selectDate = (TextView) findViewById(R.id.selectDate);
        edtDiary = (EditText) findViewById(R.id.edtDiary);
        edtLocation = (EditText) findViewById(R.id.edtLocation);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnWrite = (Button) findViewById(R.id.btnWrite);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnZoomIn = findViewById(R.id.btnZoomIn);
        btnZoomOut = findViewById(R.id.btnZoomOut);

        // 전송받은 선택된 날짜 저장
        sYear = getIntent().getStringExtra("selectYear");
        sMonth = getIntent().getStringExtra("selectMonth");
        sDay = getIntent().getStringExtra("selectDay");
        sDate = sYear + "_" + sMonth + "_" + sDay;
        selectDate.setText(sDate);

// 변수 초기화 및 날짜 저장
//-------------------------------------------------------------------------------------------------------------
        // SupportMapFragment를 초기화합니다.
        FragmentManager fragmentManager = getSupportFragmentManager();
        mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fragmentManager.beginTransaction().replace(R.id.map_fragment, mapFragment).commit();
        }else{
            initMap();
        }


        // 구글맵에 검색한 위치 띄우기
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 위치를 설정하는 메소드
                showLocation(edtLocation);
            }
        });
        // 줌인
        btnZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });
        // 줌아웃
        btnZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
// 지도        
//-------------------------------------------------------------------------------------------------------------
// 저장, 뒤로가기 버튼 // 일기에 저장된 내용 읽어오기
        id = getIntent().getStringExtra("ID");
        // diary 폴더
        File folderDi = new File(getFilesDir(), "ID/" + id + "/diary");
        if (!folderDi.exists()) { //폴더가 없다면 폴더 생성
            boolean result = folderDi.mkdirs();
        }
        // 일기 읽어오기
        File fileDi = new File(folderDi + "/" + sDate + ".txt");
        // 저장된 일기 파일이 있다면
        if (fileDi.exists()){    //원래 적힌 일기 읽어오기
            String diaryLog = readDiary(fileDi.toString());
            edtDiary.setText(diaryLog);
        }

        // Location 폴더
        File folderLoc = new File(getFilesDir(), "ID/" + id + "/Location");
        if (!folderLoc.exists()) { //폴더가 없다면 폴더 생성
            boolean result1 = folderLoc.mkdirs();
        }
        // 저장된 위치 읽어오기
        File fileLoc = new File(folderLoc + "/" + sDate + "_" +
                edtLocation.getText().toString() + ".txt");
        // 저장된 위치 파일이 있다면
        if(fileLoc.exists()){   // 위치와 지도에 표시
            String diaryLoc = readDiary(fileLoc.toString());
            edtLocation.setText(diaryLoc);
            showLocationOnMap(edtLocation.getText().toString().trim());
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
                // 일기 검사
                if(edtDiary.getText() == null) {
                    Toast.makeText(getApplicationContext(), "일기를 입력하세요",
                            Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        String diaryStr = edtDiary.getText().toString();
                        FileOutputStream fosDi = new FileOutputStream(fileDi);
                        fosDi.write(diaryStr.getBytes());
                        fosDi.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                // 위치 검사 입력이 있을 때만 실행
                if(edtLocation.getText() != null){
                    try {
                        String diaryLoc = edtLocation.getText().toString().trim();
                        FileOutputStream fosLoc = new FileOutputStream(fileLoc);
                        fosLoc.write(diaryLoc.getBytes());
                        fosLoc.close();
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
    }
//---------------------------------------------------------------------------------------------------------
    // 일기에 저장된 내용을 출력하는 메소드
    String readDiary(String fName){
        String Str = null;
        FileInputStream inFs;
        try{
            inFs = new FileInputStream(fName);
            byte[] txt = new byte[3000];
            inFs.read(txt);
            inFs.close();
            Str = (new String(txt)).trim();
        }catch (IOException ignored){
        }
        return Str;
    }
//-------------------------------------------------------------------------------------------------------------
// 지도 설정 메소드
    // 지도 초기화
    private void initMap() {
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    LatLng seoul = new LatLng(37.5665, 126.9780);
                    mMap.addMarker(new MarkerOptions().position(seoul).title("Seoul"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 20));
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            });
        }
    }

    // 위치를 받아 지도에 표시하는 메소드
    private void showLocationOnMap(String location) {
        if (mMap != null) {
            Geocoder geocoder = new Geocoder(this);
            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocationName(location, 1);
                if (addresses == null || addresses.size() == 0) {
                    Toast.makeText(getApplicationContext(), "위치를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
                    return;
                }
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "위치를 찾는 동안 오류가 발생했습니다", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "지도를 초기화하는 중입니다. 잠시만 기다려주세요.", Toast.LENGTH_SHORT).show();
            initMap();
        }
    }
    // 위치를 표시하는 메소드
    private void showLocation(EditText edtLocaiton) {
        // 입력된 위치 가져오기
        String location = edtLocation.getText().toString().trim();
        // 위치가 비어 있는지 확인
        if (location == null) {
            Toast.makeText(getApplicationContext(), "검색할 위치를 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        // 위치를 이용하여 지도에 마커 표시
        showLocationOnMap(location);
    }
}
