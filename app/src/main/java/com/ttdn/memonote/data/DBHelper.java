package com.ttdn.memonote.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ttdn1 on 9/29/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "database_memo_note.db";
    public static final String TABLE_NAME = "memonote";
    public static final int VERSION = 1;

    public static final String COL_TITLE = "title";
    public static final String COL_CONTENT = "content";
    public static final String COL_DATE = "date";
    public static final String COL_ID = "_id";
    public static final String COL_COLOR = "color";
    public final static String COL_IMAGE_LINK = "image_link";
    public static final String COL_ALARM = "alarm";

    private final Context context;
    private static final String TAG = DBHelper.class.getSimpleName();

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }

    public String query = "create table " + TABLE_NAME
            + " (" + COL_ID + " integer primary key autoincrement, "
            + COL_CONTENT + " TEXT, "
            + COL_TITLE + " TEXT, "
            + COL_DATE + " TEXT, "
            + COL_COLOR + " TEXT, "
            + COL_IMAGE_LINK+" text, "
            + COL_ALARM + " TEXT)";

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("Drop table if exists " + TABLE_NAME);
        onCreate(db);
    }
    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + TABLE_NAME + " where " + COL_ID + "=" + id, null);

    }

    public Cursor getData(String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + TABLE_NAME + " where " + COL_TITLE + "=" + title, null);

    }

    public ArrayList<Note> getAllDataDesc() {
        ArrayList<Note> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME +" order by "+COL_ID+" desc", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT));
            String id = cursor.getString(cursor.getColumnIndexOrThrow(COL_ID));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE));
            String color = cursor.getString(cursor.getColumnIndexOrThrow(COL_COLOR));

            Note note = new Note(id, title, content, date, color);
            arrayList.add(note);
            cursor.moveToNext();
        }
        cursor.close();
        return arrayList;
    }
    public ArrayList<Note> getAllDataAsc() {
        ArrayList<Note> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME +" order by "+COL_ID+" asc", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT));
            String id = cursor.getString(cursor.getColumnIndexOrThrow(COL_ID));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE));
            String color = cursor.getString(cursor.getColumnIndexOrThrow(COL_COLOR));

            Note note = new Note(id, title, content, date, color);
            arrayList.add(note);
            cursor.moveToNext();
        }
        cursor.close();
        return arrayList;
    }

    //chen
    public boolean insertData(String title, String content, String date, String color) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TITLE, title);
        cv.put(COL_CONTENT, content);
        cv.put(COL_DATE, date);
        cv.put(COL_COLOR, color);
        long result = db.insert(TABLE_NAME, null, cv);
        if (result != -1)
            return true;

        // db.close();
        Log.d(TAG, "result: " + result);
        return false;
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

    //update
    public boolean updateData(int id, String title, String content, String date, String color) {
        String[] update_id = new String[]{Integer.toString(id)};
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TITLE, title);
        cv.put(COL_CONTENT, content);
        cv.put(COL_DATE, date);
        cv.put(COL_COLOR, color);
        if (db.update(TABLE_NAME, cv, COL_ID + "=?", update_id) > 0) {
            Log.d(TAG, " -updateData, true");
            return true;
        }
        db.close();
        return false;
    }
    public void updateNote(int id, Note note) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, note.getTitle());
        values.put(COL_CONTENT, note.getContent());
        values.put(COL_DATE, note.getDate());
        values.put(COL_COLOR, note.getColor());
        values.put(COL_IMAGE_LINK, note.picture);
        values.put(COL_ALARM, note.getAlarm());
        database.insert(TABLE_NAME, null, values);
        database.update(TABLE_NAME, values, COL_ID + " = " + id, null);
        database.close();
    }
    //delete
    public void deleteData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);

    }
}
