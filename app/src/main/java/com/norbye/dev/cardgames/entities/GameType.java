package com.norbye.dev.cardgames.entities;

import android.content.Context;
import android.database.Cursor;

import com.norbye.dev.cardgames.db.DBOpenHelper;
import com.norbye.dev.cardgames.db.TableData.*;

/**
 * Created by Jonna on 04.03.2017.
 */

public class GameType {

    DBOpenHelper db;
    public int id;
    public String name = "";
    public boolean direction_vertical = false;
    public int rounds = 0;

    public GameType(Context context, int game_type_id){
        db = new DBOpenHelper(context);
        //Get the game_type data
        this.id = game_type_id;
        defineVariables();
    }

    public void defineVariables(){
        Cursor c = db.get(
                db,                                         //DB
                TableInfo.GAMETYPE_TABLE_NAME,              //Table
                new String[]{                               //Selection
                        TableInfo.GAMETYPE_NAME,
                        TableInfo.GAMETYPE_DIRECTION_VERTICAL,
                        TableInfo.GAMETYPE_ROUNDS
                },
                TableInfo.GAMETYPE_ID + "=?",               //whereClause
                new String[]{this.id+""},                   //whereArgs
                null,                                       //orderBy
                "1"                                         //Limit
        );
        if(c.getCount() != 1)
            return;
        c.moveToFirst();
        try {
            this.name = c.getString(c.getColumnIndexOrThrow(TableInfo.GAMETYPE_NAME));
        }catch(Exception e){
            e.printStackTrace();
        }
        try {
            this.direction_vertical = c.getInt(c.getColumnIndexOrThrow(TableInfo.GAMETYPE_DIRECTION_VERTICAL)) == 1;
        }catch(Exception e){
            e.printStackTrace();
        }
        try {
            this.rounds = c.getInt(c.getColumnIndexOrThrow(TableInfo.GAMETYPE_ROUNDS));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static int getCurrentGame(){

        return 0;
    }
}
