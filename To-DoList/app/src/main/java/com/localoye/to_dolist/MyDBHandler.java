package com.localoye.to_dolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Harsh on 26-05-2015.
 */
public class MyDBHandler extends SQLiteOpenHelper {

    // Database table
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "todoDB.db";
    public static final String TABLE_TODO = "todo";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DATE = "creationdate";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_TODO
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_CATEGORY + " text not null, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_DESCRIPTION + " text not null,"
            + COLUMN_DATE
            + " datetime default current_timestamp"
            + ");";

    public MyDBHandler(Context context, String name,
                       SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    // Database creation -> executing SQL Query
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(MyDBHandler.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        onCreate(database);
    }

    // Function for current date and time
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    // Function for a adding a new task
    public void addList(String[] note) {
        Date date=null,date1=null;

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, note[0]);
        values.put(COLUMN_DESCRIPTION, note[1]);
        values.put(COLUMN_DATE, note[2]);

        // Checking if the input date < current
        // If true then pending else current category
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try{
            date=formatter.parse(note[2]);
            date1=formatter.parse(getDateTime());
        }catch(ParseException e){
            Log.e("Error",e.toString());
        }
        if(date.before(date1)){
            values.put(COLUMN_CATEGORY, "pending");
        }else {
            values.put(COLUMN_CATEGORY, "new");
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_TODO, null, values);
        db.close();
    }

    // Function for updating the task and moving it from
    // one category to another
    public void updateList(String category, String title) {
        String query = "Update " + TABLE_TODO + " SET " + COLUMN_CATEGORY + " =  \"" + category + "\"" + " WHERE " + COLUMN_TITLE + " =  \"" + title + "\"";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    // Function for fetching tasks from DB acc. to category
    public String[][] fetchList(String category) {
        String query,query1;
        if(category=="pending"){
            // If fetching for pending select all new tasks
            // check if date < currentdate, if yes update category to pending
            // else category remains new
            category="new";
            query = "Select " + COLUMN_ID +" FROM " + TABLE_TODO + " AS t1 WHERE t1." + COLUMN_CATEGORY + " =  \"" + category + "\"" + " AND t1." + COLUMN_DATE + " < \"" + getDateTime() + "\"";
            category="pending";
            query1 = "Update " + TABLE_TODO + " SET " + COLUMN_CATEGORY + " =  \"" + category + "\"" + " WHERE " + COLUMN_ID + " IN (" + query + ")";
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(query1);
            db.close();
        }
            query = "Select * FROM " + TABLE_TODO + " WHERE " + COLUMN_CATEGORY + " =  \"" + category + "\"";
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            String[][] list = new String[3][cursor.getCount()];
            int i = 0;
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String desc = cursor.getString(cursor.getColumnIndex("description"));
                String datetime = cursor.getString(cursor.getColumnIndex("creationdate"));
                list[0][i] = title;
                list[1][i] = desc;
                list[2][i] = datetime;
                i++;
            }
            cursor.close();
            db.close();
            return list;
    }
    // Function for deleting a task
    public boolean deleteList(String title, String desc) {

        boolean result = false;

        String query = "Select * FROM " + TABLE_TODO;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            db.delete(TABLE_TODO, COLUMN_TITLE + " = '" + title + "' AND " + COLUMN_DESCRIPTION + " = '" + desc + "'", null);
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }
}
