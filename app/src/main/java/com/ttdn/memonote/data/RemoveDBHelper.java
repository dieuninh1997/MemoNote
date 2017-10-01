package com.ttdn.memonote.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by ttdn1 on 9/29/2017.
 */

public class RemoveDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "trash_database.db";
    public static final String TABLE_NAME = "trash_data";
    public static final String COL_TITLE = "title";
    public static final String COL_CONTENT ="content";
    public static final String COL_DATE = "date";
    public static final String COL_COLOR = "color";
    public static final String COL_ID = "_id";
    public static final String COL_IMAGE_LINK = "image_link";
    public static final String COL_ALARM = "alarm";

    public RemoveDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME +
                "("+COL_ID+" integer primary key ,"
                +COL_TITLE+" text ,"
                +COL_CONTENT+" text ,"
                +COL_DATE+" text,"
                +COL_COLOR+" text,"
                +COL_IMAGE_LINK+" text,"
                + COL_ALARM + " TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME+" where "+COL_ID+"="+id+"", null );
        return res;
    }

    public ArrayList<Note> getAllDataDesc(){

        ArrayList<Note> arrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME +" order by "+COL_ID+" desc",null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false){
            String title = cursor.getString(cursor.getColumnIndex(COL_TITLE));
            String content = cursor.getString(cursor.getColumnIndex(COL_CONTENT));
            String id = cursor.getString(cursor.getColumnIndex(COL_ID));
            String date = cursor.getString(cursor.getColumnIndex(COL_DATE));
            String color = cursor.getString(cursor.getColumnIndex(COL_COLOR));
            Note obj = new Note(id,title,content,date,color);
            arrayList.add(obj);
            cursor.moveToNext();
        }
        cursor.close();
        return arrayList;

    }
    public ArrayList<Note> getAllDataAsc(){

        ArrayList<Note> arrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME +" order by "+COL_ID+" asc",null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false){
            String title = cursor.getString(cursor.getColumnIndex(COL_TITLE));
            String content = cursor.getString(cursor.getColumnIndex(COL_CONTENT));
            String id = cursor.getString(cursor.getColumnIndex(COL_ID));
            String date = cursor.getString(cursor.getColumnIndex(COL_DATE));
            String color = cursor.getString(cursor.getColumnIndex(COL_COLOR));
            Note obj = new Note(id,title,content,date,color);
            arrayList.add(obj);
            cursor.moveToNext();
        }
        cursor.close();
        return arrayList;

    }
    public void insertData(String title ,String content,String date,String color) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TITLE,title);
        contentValues.put(COL_CONTENT,content);
        contentValues.put(COL_DATE,date);
        contentValues.put(COL_COLOR,color);
        db.insert(TABLE_NAME,null,contentValues);
        db.close();

    }

    public void insertNote(Note note){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, note.getTitle());
        values.put(COL_CONTENT, note.getContent());
        values.put(COL_DATE, note.getDate());
        values.put(COL_COLOR, note.getColor());
        values.put(COL_IMAGE_LINK, note.picture);
        values.put(COL_ALARM, note.getAlarm());
        database.insert(TABLE_NAME, null, values);
        database.close();
    }

    public void deleteData (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,COL_ID + " = ?",new String[]{String.valueOf(id)});
        db.close();

    }

    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);

    }
}
