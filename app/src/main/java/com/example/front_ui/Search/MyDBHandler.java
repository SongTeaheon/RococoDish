package com.example.front_ui.Search;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.AlphabeticIndex;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MyDBHandler {

    private final String TAG = "TAGMyDBHandler";

    private SQLiteOpenHelper mHelper;
    private SQLiteDatabase mDB = null;

    public MyDBHandler(Context context, String name) {
        mHelper = new DBHelper(context, name, null, 1);
    }

    public static MyDBHandler open(Context context) {
        return new MyDBHandler(context, "SEARCH_TABLE");
    }

    public Cursor select()
    {
        mDB = mHelper.getReadableDatabase();
        Cursor c = mDB.query("SEARCH_TABLE", null, null, null, null, null, null);
        return c;
    }

    public void insert(String text) {

        Log.d(TAG, "insert");

        mDB = mHelper.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put("record", text);
        mDB.insert("SEARCH_TABLE", null, value);

    }

    public void delete(int id)
    {
        Log.d(TAG, "delete");
        mDB = mHelper.getWritableDatabase();
        mDB.delete("SEARCH_TABLE", "_ID=?", new String[]{Integer.toString(id)});
    }

    public void close() {
        mHelper.close();
    }

    //TODO: 예쁘게 바꿔보
    public void getAllRecordData(ArrayList<RecordData> list) {
        Log.d(TAG, "getAllRecordData");
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT _ID, RECORD FROM SEARCH_TABLE ORDER BY _ID DESC");
     // 읽기 전용 DB 객체를 만든다
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sb.toString(), null);
        List people = new ArrayList();
        RecordData recordData = null;
        // moveToNext 다음에 데이터가 있으면 true 없으면 false
        while( cursor.moveToNext() ) {
            Log.d(TAG, "id : " + cursor.getInt(0));
            Log.d(TAG, "record : " + cursor.getString(1));

            recordData = new RecordData(cursor.getInt(0),cursor.getString(1));
            list.add(recordData);

            //            person.set_id(cursor.getInt(0));
//            person.setName(cursor.getString(1));
//            person.setAge(cursor.getInt(2));
//            person.setPhone(cursor.getString(3));
//            people.add(person);
        }
//        return people;
    }

}