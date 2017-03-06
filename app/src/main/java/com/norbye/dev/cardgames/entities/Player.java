package com.norbye.dev.cardgames.entities;

import android.content.Context;
import android.database.Cursor;

import com.norbye.dev.cardgames.db.DBOpenHelper;
import com.norbye.dev.cardgames.db.TableData.*;

/**
 * Created by Jonna on 04.03.2017.
 */

public class Player {

    private DBOpenHelper db;
    private Context context;

    public int id;
    public String name = "";
    public int order = 0;

    public Player(Context context, int player_id){
        this.context = context;
        this.id = player_id;
        //Grab player information
        db = new DBOpenHelper(context);
        Cursor c = db.get(
                db,                               //DB
                TableInfo.PLAYER_TABLE_NAME,      //Table
                new String[]{                     //Selection
                        TableInfo.PLAYER_NAME

                },
                TableInfo.PLAYER_ID + "=?",       //whereClause
                new String[]{                     //whereArgs
                        this.id + ""
                },
                null,                             //orderBy
                "1"                               //Limit
        );
        if(c.getCount() > 0) {
            c.moveToFirst();
            try {
                this.name = c.getString(c.getColumnIndexOrThrow(TableInfo.PLAYER_NAME));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public double getScore(Game game){
        double score = 0;
        return score;
    }
}
