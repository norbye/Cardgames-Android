package com.norbye.dev.cardgames.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.norbye.dev.cardgames.db.TableData.*;

/**
 * Created by Jonna on 04.03.2017.
 */

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String GAMETYPE_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TableInfo.GAMETYPE_TABLE_NAME + " (" +
                    TableInfo.GAMETYPE_ID + " INTEGER PRIMARY KEY," +
                    TableInfo.GAMETYPE_NAME + " TEXT," +
                    TableInfo.GAMETYPE_DIRECTION_VERTICAL + " INTEGER," +
                    TableInfo.GAMETYPE_ROUNDS + " INTEGER);";
    private static final String GAME_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TableInfo.GAME_TABLE_NAME + " (" +
                    TableInfo.GAME_ID + " INTEGER PRIMARY KEY," +
                    TableInfo.GAME_TYPE + " INTEGER," +
                    TableInfo.GAME_START + " INTEGER," +
                    TableInfo.GAME_ACTIVE + " INTEGER);";
    private static final String PLAYER_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TableInfo.PLAYER_TABLE_NAME + " (" +
                    TableInfo.PLAYER_ID + " INTEGER PRIMARY KEY," +
                    TableInfo.PLAYER_NAME + " TEXT);";
    private static final String GAME_PLAYER_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TableInfo.GAME_PLAYER_TABLE_NAME + " (" +
                    TableInfo.GAME_PLAYER_ID + " INTEGER PRIMARY KEY," +
                    TableInfo.GAME_PLAYER_GAME_ID + " INTEGER," +
                    TableInfo.GAME_PLAYER_PLAYER_ID + " INTEGER," +
                    TableInfo.GAME_PLAYER_ORDER + " INTEGER);";
    private static final String RESULT_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TableInfo.RESULT_TABLE_NAME + " (" +
                    TableInfo.RESULT_ID + " INTEGER PRIMARY KEY," +
                    TableInfo.RESULT_GAME_PLAYER_ID + " INTEGER," +
                    TableInfo.RESULT_INDEX + " INTEGER," +
                    TableInfo.RESULT_VALUE + " INTEGER);";

    public DBOpenHelper(Context context){
        super(context, TableInfo.DATABASE_NAME, null, TableInfo.DATABASE_VERSION);
        Log.d("DBOpenHelper", "Database created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(GAMETYPE_TABLE_CREATE);
        db.execSQL(GAME_TABLE_CREATE);
        db.execSQL(PLAYER_TABLE_CREATE);
        db.execSQL(GAME_PLAYER_TABLE_CREATE);
        db.execSQL(RESULT_TABLE_CREATE);
        Log.d("DBOpenHelper", "Tables created");
        System.out.println("Tables created");

        //Populate the table if empty
        Cursor cr = db.query(TableInfo.GAMETYPE_TABLE_NAME, new String[]{TableInfo.GAMETYPE_NAME},
                null, null, null, null, TableInfo.GAMETYPE_NAME);
        if(cr.getCount() == 0){
            String[] keys = new String[]{
                    TableInfo.GAMETYPE_NAME,
                    TableInfo.GAMETYPE_DIRECTION_VERTICAL,
                    TableInfo.GAMETYPE_ROUNDS
            };
            String[][] default_games = new String[][]{
                    new String[]{"Kontinental", "0", "5"},
                    new String[]{"Amerikaner", "1", "0"},
                    new String[]{"Janif", "1", "0"},
                    new String[]{"Spardam", "1", "0"}
            };
            for(int i = 0; i < default_games.length; i++){
                ContentValues cv = new ContentValues();
                for(int k = 0; k < default_games[i].length; k++){
                    cv.put(keys[k], default_games[i][k]);
                }
                db.insert(TableInfo.GAMETYPE_TABLE_NAME, null, cv);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean insert(DBOpenHelper db, String table, String[] columns, String[] values){
        try{
            SQLiteDatabase SQDB = db.getWritableDatabase();
            ContentValues cv = new ContentValues();

            for(int i = 0; i < columns.length && i < values.length; i++){
                cv.put(columns[i], values[i]);
            }

            SQDB.insert(table, null, cv);

            Log.d("DBOpenHelper", "One row inserted");
            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public Cursor get(DBOpenHelper db, String table, String[] columns, String whereClause, String[] whereArgs, String orderBy, String limit){
        SQLiteDatabase SQDB = db.getReadableDatabase();
        /*
        columns - columns to select
        whereClause - where clause
        whereArgs - content that fills the ?s in the whereClause
        orderBY order
         */
        Cursor cr = SQDB.query(table, columns,
                whereClause, whereArgs, null, null, orderBy, limit);
        return cr;
    }
}
