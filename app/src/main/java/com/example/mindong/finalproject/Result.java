package com.example.mindong.finalproject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

public class Result extends AppCompatActivity {

    SQLiteDatabase db;
    String dbName = "list.db";
    String tableName = "Memo";

    Spinner mSpCategory;

    int dbMode = Context.MODE_PRIVATE;

    String temp;


    ArrayAdapter<String> musicAdapter;
    ArrayList<String> nameList;

    Intent INTENT_EXTRA = new Intent();

    Button mBtCancel;
    Button mBtSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        final ListView mList = (ListView) findViewById(R.id.list_view);

        mBtCancel = (Button) findViewById(R.id.bt_cancel);
        mBtCancel = (Button) findViewById(R.id.bt_cancel);

        //취소 버튼 누르면 mainactivity로 돌아간다.
        mBtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });


        // Create listview
        nameList = new ArrayList<String>();
        musicAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, nameList);
        mList.setAdapter(musicAdapter);
        db = openOrCreateDatabase(dbName,dbMode,null);

        nameList.clear();
        selectAll();
        musicAdapter.notifyDataSetChanged();

        mSpCategory = (Spinner) findViewById(R.id.sp_re_category);
        temp = mSpCategory.getSelectedItem().toString();

        //리스트 뷰에 있는 item을 선택 하면 그 아이탬의 위치가 main으로 보내진다.
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String longitudeTemp = selectLongitudeData(position);
                String latitudeTemp = selectLatitudeData(position);
                //String temp = longitudeTemp + "," + latitudeTemp;
                INTENT_EXTRA.putExtra("long", longitudeTemp);
                INTENT_EXTRA.putExtra("lat", latitudeTemp);

                setResult(RESULT_OK, INTENT_EXTRA);

                finish();
            }
        });

        mBtSort = (Button) findViewById(R.id.bt_sort);

        //sort를 하는 함수 원하는 category만 보여준다.
        mBtSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp = mSpCategory.getSelectedItem().toString();
                Log.d("로그 확인",  temp + "@@@@@@@@@@@@@");

                nameList.clear();

               if(temp.equals("기본")) {
                    selectAll();
                }
                else{
                    selectAll_sort();
                }

                musicAdapter.notifyDataSetChanged();
            }
        });

    }

    // Data 읽기(꺼내오기)
    public String selectLongitudeData(int index) {
        String sql = "select * from " + tableName + " where id = " + (index + 1) + ";";;
        Cursor result = db.rawQuery(sql, null);


        // result(Cursor 객체)가 비어 있으면 false 리턴
        if (result.moveToFirst()) {
            int id = result.getInt(0);
            String Longitude = result.getString(1);
//            Toast.makeText(this, "index= " + id + " name=" + name, Toast.LENGTH_LONG).show();

            return Longitude;
        }

        return null;
    }
    public String selectLatitudeData(int index) {
        String sql = "select * from " + tableName + " where id = " + (index + 1) + ";";
        Cursor result = db.rawQuery(sql, null);


        // result(Cursor 객체)가 비어 있으면 false 리턴
        if (result.moveToFirst()) {
            int id = result.getInt(0);
            String Latitude = result.getString(2);

            return Latitude;
        }

        return null;


    }


    // 모든 Data 읽기
    public void selectAll() {

        String sql = "select * from " + tableName + ";";
        Cursor results = db.rawQuery(sql, null);

        results.moveToFirst();


        while (!results.isAfterLast()) {
            int id = results.getInt(0);

            String str = results.getString(3) + " | "+ results.getString(4) + " | " + results.getString(5) ;

            nameList.add(str);


            results.moveToNext();
        }
        results.close();
    }

    //원하는 category만 보이기
    public void selectAll_sort() {

        String sql = "select * from " + tableName + ";";
        Cursor results = db.rawQuery(sql, null);

        results.moveToFirst();


        while (!results.isAfterLast()) {

            int id = results.getInt(0);

            if(temp.equals(results.getString(4).toString()) || temp.equals(results.getString(5).toString())) {

                String str = results.getString(3) + " | " + results.getString(4) + " | " + results.getString(5);

                nameList.add(str);
            }

            results.moveToNext();
        }
        results.close();
    }
}
