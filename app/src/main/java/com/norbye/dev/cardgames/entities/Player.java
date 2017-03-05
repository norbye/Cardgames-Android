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
        /*Cursor c = db.get(
                db,                                         //DB
                TableInfo.PLAYER_TABLE_NAME,           //Table
                new String[]{                               //Selection
                        TableInfo.PLAYER_NAME

                },
                TableInfo.GAME_PLAYER_GAME_ID + "=?",       //whereClause
                new String[]{                               //whereArgs
                        this.id + ""
                },
                TableInfo.GAME_PLAYER_ORDER,                //orderBy
                null                                        //Limit
        );
        Player[] players = new Player[c.getCount()];
        if(c.getCount() > 0) {
            c.moveToFirst();
            do {
                try {
                    players[c.getPosition()] = new Player(this.context, c.getInt(c.getColumnIndexOrThrow(TableInfo.GAME_PLAYER_PLAYER_ID)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
        }*/
    }

    public double getScore(Game game){
        double score = 0;
        return score;
    }
}
