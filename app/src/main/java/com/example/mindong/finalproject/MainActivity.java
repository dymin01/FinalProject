package com.example.mindong.finalproject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //DB에서 쓰이는 변수들
    SQLiteDatabase db;
    String tableName = "Memo";
    String dbName = "list.db";
    String KEY_ID = "id";
    String KEY_LONGITUDE = "longitude";
    String KEY_LATITUDE = "latitude";
    String KEY_EVENT = "event";
    String KEY_EMOTION = "emotion";
    String KEY_CATEGORY = "CATEGORY";

    final static int RESULT = 100;

    ArrayList<String> nameList;
    ArrayAdapter<String> musicAdapter;

    //spinner 변수
    Spinner mSpCategoey;
    Spinner mSpEmotion;

    //버튼 변수
    Button mBtSave;
    Button mBtList;

    //맵 변수
    private GoogleMap map;
    static final LatLng SEOUL = new LatLng( 37.56, 126.97);
    //경도, 위도
    String strLongitude;
    String strLatitude;

    EditText mEtEvent;
    String strEvent;

    int dbMode = Context.MODE_PRIVATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create databases
        db = openOrCreateDatabase(dbName,dbMode,null);
        // create table;
        createTable();

        mSpCategoey = (Spinner) findViewById(R.id.sp_category);
        mSpCategoey.setPrompt("cotegory를 선택하세요.");
        addListenerOnSpinnerItemSelection();

        mSpEmotion = (Spinner) findViewById(R.id.sp_emotion);
        mSpEmotion.setPrompt("감정을 선택하세요.");



        //Map
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        map = mapFragment.getMap();

        //현재 위치로 가는 버튼 표시
        map.setMyLocationEnabled(true);
        Log.d("setmyloc ", "실행확인 1235123");
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 15));//초기 위치...수정필요


        //시작하자마자 자신의 위치를 찾아 marking한다.
        MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {

                strLongitude = Double.toString(location.getLongitude());
                strLatitude = Double.toString(location.getLatitude());

                drawMarker(location);

            }
        };

        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(getApplicationContext(), locationResult);

        mSpCategoey = (Spinner) findViewById(R.id.sp_category);
        mBtSave = (Button) findViewById(R.id.bt_save);
        mBtList = (Button) findViewById(R.id.bt_list);
        mEtEvent = (EditText) findViewById(R.id.et_event);

        //list버튼을 누르면 리스트가 나온다.
        mBtList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(MainActivity.this, Result.class);
                startActivityForResult(intent, RESULT);
            }
        });

        //save버튼을 누르면 db에 저장된다.
        mBtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                strEvent = mEtEvent.getText().toString();

                insertData();

                Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_LONG).show();

            }
        });
    }


    public void addListenerOnSpinnerItemSelection() {
        mSpCategoey = (Spinner) findViewById(R.id.sp_category);
        mSpCategoey.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        mSpEmotion = (Spinner) findViewById(R.id.sp_emotion);
        mSpEmotion.setOnItemSelectedListener(new CustomOnItemSelectedListener());

    }


    //마커 그리기
    private void drawMarker(Location location) {

        //기존 마커 지우기
        map.clear();
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

        //currentPosition 위치로 카메라 중심을 옮기고 화면 줌을 조정한다. 줌범위는 2~21, 숫자클수록 확대
        map.moveCamera(CameraUpdateFactory.newLatLngZoom( currentPosition, 17));
        map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);

        //마커 추가
        map.addMarker(new MarkerOptions()
                .position(currentPosition)
                .snippet("Lat:" + location.getLatitude() + "Lng:" + location.getLongitude())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("현재위치"));
    }

    //    // Database 생성 및 열기
//    public void createDatabase(String dbName, int dbMode) {
//        db = openOrCreateDatabase(dbName, dbMode, null);
//    }

    // Table 생성
    public void createTable() {
        try {
            String sql = "create table if not exists " + tableName + "(id integer primary key autoincrement, " + KEY_LONGITUDE + " text, "  + KEY_LATITUDE + " text, " + KEY_EVENT + " text, " + KEY_EMOTION + " text, " + KEY_CATEGORY + " text)";
            db.execSQL(sql);
        } catch (android.database.sqlite.SQLiteException e) {
            Log.d("Lab sqlite","error: "+ e);
        }
    }

    // Data 추가
    public void insertData() {

        String sql = "insert into " + tableName + " values(NULL, '" + strLongitude + "',  '" + strLatitude + "', '" + strEvent +"', '" + mSpEmotion.getSelectedItem() + "', '" + mSpCategoey.getSelectedItem() + "');";
        db.execSQL(sql);
    }

    //엑티비티 이동후 돌아온 후 받아온 위치 값으로 마킹 다시 하기
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {

            Double double_longitude = Double.parseDouble(data.getStringExtra("long"));
            Double double_latitude = Double.parseDouble(data.getStringExtra("lat"));

            map.clear();
            LatLng latLng = new LatLng(double_latitude, double_longitude);

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);

            //마커 추가
            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .snippet("Lat:" + double_latitude + "Lng:" + double_longitude)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title("현재위치"));

        }

    }

}
