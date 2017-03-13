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

    public int[] getScore(Game game){
        int[] score = new int[0];
        //Get score from game
        Cursor c = db.get(
                db,                               //DB
                TableInfo.GAME_PLAYER_TABLE_NAME,      //Table
                new String[]{                     //Selection
                        TableInfo.GAME_PLAYER_ID
                },
                TableInfo.GAME_PLAYER_PLAYER_ID + "=? AND " +       //whereClause
                TableInfo.GAME_PLAYER_GAME_ID + "=?",
                new String[]{                     //whereArgs
                        this.id + "",
                        game.id + ""
                },
                null,                             //orderBy
                "1"                               //Limit
        );
        if(c.getCount() > 0) {
            c.moveToFirst();
            try {
                int game_player_id = c.getInt(c.getColumnIndexOrThrow(TableInfo.GAME_PLAYER_ID));
                c = db.get(
                        db,                               //DB
                        TableInfo.RESULT_TABLE_NAME,      //Table
                        new String[]{                     //Selection
                                TableInfo.RESULT_INDEX,
                                TableInfo.RESULT_VALUE
                        },
                        TableInfo.RESULT_GAME_PLAYER_ID + "=?",       //whereClause
                        new String[]{                     //whereArgs
                                game_player_id + ""
                        },
                        TableInfo.RESULT_INDEX,                             //orderBy
                        null                               //Limit
                );
                if(c.getCount() > 0) {
                    //Get last index
                    c.moveToLast();
                    score = new int[c.getInt(c.getColumnIndexOrThrow(TableInfo.RESULT_INDEX)) + 1];
                    //Store values
                    c.moveToFirst();
                    try {
                        score[c.getInt(c.getColumnIndexOrThrow(TableInfo.RESULT_INDEX))] = c.getInt(c.getColumnIndexOrThrow(TableInfo.RESULT_VALUE));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return score;
    }
}
