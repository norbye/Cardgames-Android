package com.norbye.dev.cardgames.entities;

import com.norbye.dev.cardgames.entities.Game;

/**
 * Created by Jonna on 04.03.2017.
 */

public class Player {

    public int id;
    public String name = "";

    public Player(int player_id){
        this.id = player_id;
        //Grab player information

    }

    public double getScore(Game game){
        double score = 0;
        return score;
    }
}
