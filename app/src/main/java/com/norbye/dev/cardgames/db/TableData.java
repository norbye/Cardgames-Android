package com.norbye.dev.cardgames.db;

import android.provider.BaseColumns;

/**
 * Created by Jonna on 04.03.2017.
 */

public class TableData {
    //This class cannot be instantiated
    private TableData(){}

    public static abstract class TableInfo implements BaseColumns{
        //This class cannot be instantiated
        private TableInfo(){};

        public static final String DATABASE_NAME = "cardgames";
        public static final int DATABASE_VERSION = 1;

        public static final String GAMETYPE_TABLE_NAME = "game_type";

        public static final String GAMETYPE_ID = "game_type_id";
        public static final String GAMETYPE_NAME = "name";
        public static final String GAMETYPE_DIRECTION_VERTICAL = "direction_vertical";
        public static final String GAMETYPE_ROUNDS = "rounds";

        public static final String GAME_TABLE_NAME = "game";
        public static final String GAME_ID = "game_id";
        public static final String GAME_TYPE = "game_type";
        public static final String GAME_START = "start";
        public static final String GAME_ACTIVE = "active";

        public static final String PLAYER_TABLE_NAME = "player";

        public static final String PLAYER_ID = "player_id";
        public static final String PLAYER_NAME = "name";

        public static final String GAME_PLAYER_TABLE_NAME = "game_player";

        public static final String GAME_PLAYER_ID = "game_player_id";
        public static final String GAME_PLAYER_GAME_ID = "game_id";
        public static final String GAME_PLAYER_PLAYER_ID = "player_id";
        public static final String GAME_PLAYER_ORDER = "\"order\"";

        public static final String RESULT_TABLE_NAME = "result";

        public static final String RESULT_ID = "result_id";
        public static final String RESULT_GAME_PLAYER_ID = "game_player_id";
        public static final String RESULT_INDEX = "index";
        public static final String RESULT_VALUE = "value";
    }
}
