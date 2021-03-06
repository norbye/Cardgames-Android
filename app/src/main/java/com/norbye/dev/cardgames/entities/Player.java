package com.norbye.dev.cardgames.entities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

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
                Cursor c2 = db.get(
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
                if(c2.getCount() > 0) {
                    //Get last index
                    c2.moveToLast();
                    if(c2.getInt(c2.getColumnIndexOrThrow(TableInfo.RESULT_INDEX.replace("\"", ""))) + 1 > c2.getCount()){
                        score = new int[c2.getInt(c2.getColumnIndexOrThrow(TableInfo.RESULT_INDEX.replace("\"", ""))) + 1];
                    }else{
                        score = new int[c2.getCount()];
                    }
                    //Insert -1 values
                    for(int i = 0; i < score.length; i++){
                        score[i] = -1;
                    }
                    //Store values
                    c2.moveToFirst();
                    try {
                        do {
                            if(c2.getInt(c2.getColumnIndexOrThrow(TableInfo.RESULT_INDEX.replace("\"", ""))) != -1) {
                                score[c2.getInt(c2.getColumnIndexOrThrow(TableInfo.RESULT_INDEX.replace("\"", "")))] = c2.getInt(c2.getColumnIndexOrThrow(TableInfo.RESULT_VALUE));
                            }else{
                                score[c2.getPosition()] = c2.getInt(c2.getColumnIndexOrThrow(TableInfo.RESULT_VALUE));
                            }
                        }while(c2.moveToNext());
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

    public boolean setScore(Game game, int index, int value){
        //Get the game_player
        Toast.makeText(context, "gameID: " + game.id + " index " + index + "val " + value, Toast.LENGTH_SHORT).show();
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
                //Check for existing row
                c = db.get(
                        db,                               //DB
                        TableInfo.RESULT_TABLE_NAME,      //Table
                        new String[]{                     //Selection
                                TableInfo.RESULT_ID
                        },
                        TableInfo.RESULT_GAME_PLAYER_ID + "=? AND " +       //whereClause
                                TableInfo.RESULT_INDEX + "=?",
                        new String[]{                     //whereArgs
                                game_player_id + "",
                                index + ""
                        },
                        null,                             //orderBy
                        "1"                               //Limit
                );
                if(index == 0){
                    //TODO Get the next possible index and replace the index int
                }
                if(c.getCount() == 0){
                    //Insert new row
                    int newRow = (int) db.insert(
                            db,                             //DB
                            TableInfo.RESULT_TABLE_NAME,      //Table
                            new String[]{
                                    TableInfo.RESULT_GAME_PLAYER_ID,
                                    TableInfo.RESULT_INDEX,
                                    TableInfo.RESULT_VALUE
                            },
                            new String[]{
                                    game_player_id + "",
                                    index + "",
                                    value + ""
                            }
                    );
                    if(newRow > 0){
                        return true;
                    }else{
                        return false;
                    }
                }else{
                    //Update existing row
                    ContentValues cv = new ContentValues();
                    cv.put(TableInfo.RESULT_VALUE, value);
                    int update = db.update(
                            db,
                            TableInfo.RESULT_TABLE_NAME,
                            cv,
                            TableInfo.RESULT_GAME_PLAYER_ID + "=? AND " +
                                    TableInfo.RESULT_INDEX + "=?",
                            new String[]{
                                    game_player_id + "",
                                    index + ""
                            });
                    if(update != 0){
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
