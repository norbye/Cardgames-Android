package com.norbye.dev.cardgames.entities;

import android.content.Context;
import android.database.Cursor;

import com.norbye.dev.cardgames.db.DBOpenHelper;
import com.norbye.dev.cardgames.db.TableData.*;

/**
 * Created by Jonna on 04.03.2017.
 */

public class Game {

    private DBOpenHelper db;
    private Context context;

    public int id;
    public String name = "";

    public Game(Context context, int game_id){
        this.context = context;
        this.id = game_id;
        db = new DBOpenHelper(context);
    }

    public long addPlayer(Player player) {
        int player_id = 0;
        if (player != null) {
            player_id = player.id;
        }
        return db.insert(
                db,
                TableInfo.GAME_PLAYER_TABLE_NAME,
                new String[]{
                        TableInfo.GAME_PLAYER_GAME_ID,
                        TableInfo.GAME_PLAYER_PLAYER_ID,
                        TableInfo.GAME_PLAYER_ORDER
                },
                new String[]{
                        this.id + "",
                        player_id + "",
                        System.currentTimeMillis() + ""
                }
        );
    }

    public boolean updatePlayer(Player p, int timeInMillis){
        //Check if name belongs to a player, if not, create a new one
        return false;
    }

    public Player[] getPlayers(){
        Cursor c = db.get(
                db,                                         //DB
                TableInfo.GAME_PLAYER_TABLE_NAME,           //Table
                new String[]{                               //Selection
                        TableInfo.GAME_PLAYER_PLAYER_ID
                },
                TableInfo.GAME_PLAYER_GAME_ID + "=?",       //whereClause
                new String[]{                               //whereArgs
                        this.id + ""
                },
                TableInfo.GAME_PLAYER_ORDER,                //orderBy
                null                                        //Limit
        );
        Player[] players = new Player[c.getCount()];
        System.out.println("GamePlayercount: " + c.getCount());
        if(c.getCount() == 0)
            return players;
        c.moveToFirst();
        do{
            try {
                players[c.getPosition()] = new Player(this.context, c.getInt(c.getColumnIndexOrThrow(TableInfo.GAME_PLAYER_PLAYER_ID)));
            }catch(Exception e){
                e.printStackTrace();
            }
        }while(c.moveToNext());
        return players;
    }
}
