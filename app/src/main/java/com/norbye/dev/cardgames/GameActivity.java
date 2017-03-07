package com.norbye.dev.cardgames;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.norbye.dev.cardgames.db.DBOpenHelper;
import com.norbye.dev.cardgames.db.TableData.*;
import com.norbye.dev.cardgames.entities.Game;
import com.norbye.dev.cardgames.entities.GameType;
import com.norbye.dev.cardgames.entities.Player;

/**
 * Created by Jonna on 04.03.2017.
 */

public class GameActivity extends AppCompatActivity {

    DBOpenHelper db;
    public Context context = this;
    private GameType gameType;
    private Game game;

    private int fontSize = 18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DBOpenHelper(context);

        //Get gametype id
        Bundle b = getIntent().getExtras();
        if(b == null || b.getInt("game_type_id", 0) == 0){
            finish();
        }
        //Initialize gametype
        gameType = new GameType(context, b.getInt("game_type_id", 0));
        setTitle(gameType.name);
        //Initialize game
        Cursor c = db.get(
                db,                                         //DB
                TableInfo.GAME_TABLE_NAME,                  //Table
                new String[]{TableInfo.GAME_ID},            //Selection
                TableInfo.GAME_TYPE + "=? AND active=1",    //whereClause
                new String[]{ gameType.id + ""},            //whereArgs
                null,                                       //orderBy
                "1"                                         //Limit
        );
        if(c.getCount() == 1){
            //Game exists
            c.moveToFirst();
            game = new Game(context, c.getInt(c.getColumnIndexOrThrow(TableInfo.GAME_ID)));
        }else{
            //Create new game
            int gameId = (int) db.insert(
                    db,                             //DB
                    TableInfo.GAME_TABLE_NAME,      //Table
                    new String[]{
                            TableInfo.GAME_TYPE,
                            TableInfo.GAME_START,
                            TableInfo.GAME_ACTIVE
                    },
                    new String[]{
                            gameType.id + "",
                            System.currentTimeMillis() + "",
                            "1"
                    }
            );
            if(gameId > 0){
                game = new Game(context, gameId);
            }else{
                finish();
            }
        }

        //Add player
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlayer();
            }
        });
        loadView();
    }

    public void loadView(){
        TableLayout tl = (TableLayout) findViewById(R.id.game_tableLayout);
        tl.removeAllViews();
        if(gameType.direction_vertical) {

        }else{
            //Print the top layer
            TableRow trTop = new TableRow(this);
            trTop.addView(newTextView("Navn"));
            if(gameType.rounds == 0){
                trTop.addView(newTextView("1"));
                trTop.addView(newTextView("Sum"));
                trTop.addView(newTextView("Plass"));
            }else{
                for(int i = 1; i <= gameType.rounds; i++){
                    trTop.addView(newTextView(i + ""));
                }
                //Add sum and position
                trTop.addView(newTextView("Sum"));
                trTop.addView(newTextView("Plass"));
            }
            tl.addView(trTop, 0);
            //Print the other layers
            Player[] players = game.getPlayers();
            int[] sum = new int[players.length];
            for(int i = 0; i < players.length; i++){
                TableRow tr = new TableRow(this);
                tr.addView(newTextView(players[i].name));
                if(gameType.rounds == 0){
                    //Fetch inserted rows and add one more
                    Cursor c = db.get(
                            db,                                         //DB
                            TableInfo.GAME_PLAYER_TABLE_NAME,           //Table
                            new String[]{                               //Selection
                                    TableInfo.GAME_PLAYER_ID
                            },
                            TableInfo.GAME_PLAYER_PLAYER_ID + "=? AND " //whereClause
                            + TableInfo.GAME_PLAYER_GAME_ID + "=?",                               
                            new String[]{                               //whereArgs
                                    players[i].id + "",
                                    game.id + ""
                            },
                            null,                                       //orderBy
                            null                                        //Limit
                    );
                    if(c.getCount() > 0){
                        c.moveToFirst();
                        try{
                            int game_player_id = c.getInt(c.getColumnIndexOrThrow(TableInfo.GAME_PLAYER_ID));
                        }catch(Exception e){
                            e.printStackTrace(e);
                            //skip the player and continue with other players
                            continue;
                        }
                        c = db.get(
                                db,                                         //DB
                                TableInfo.RESULT_TABLE_NAME,           //Table
                                new String[]{                               //Selection
                                        TableInfo.RESULT_INDEX,
                                        TableInfo.RESULT_VALUE
                                },
                                TableInfo.RESULT_GAME_PLAYER_ID + "=?", //whereClause                               
                                new String[]{                               //whereArgs
                                        game_player_id + "",
                                },
                                TableInfo.RESULT_INDEX,                                       //orderBy
                                null                                        //Limit
                        );
                        if(c.getCount() > 0){
                            c.moveToFirst();
                            do{
                                try {
                                    sum[i] += c.getInt(c.getColumnIndexOrThrow(TableInfo.RESULT_VALUE));
                                    EditText et = newEditText("");
                                    et.setTag(c.getInt(c.getColumnIndexOrThrow(TableInfo.RESULT_INDEX)) + "");
                                    et.setText(c.getInt(c.getColumnIndexOrThrow(TableInfo.RESULT_VALUE)) + "");
                                    et.addTextChangedListener(twGame);
                                    tr.addView(et);
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }while(c.moveToNext());
                        }
                    }
                    tr.addView(newEditText(""));
                }else{
                    //Fetch inserted values for each row, but display all rows
                    for(int k = 0; k < gameType.rounds; k++){
                        tr.addView(newEditText(""));
                    }
                }
                //Add sum and position
                tr.addView(newTextView(sum[i] + ""));
                tr.addView(newTextView("-"));
                //Append to tablelayout
                tl.addView(tr);
            }
        }
    }
    private TextView newTextView(String text){
        return newTextViewFull(text, null, false);
    }
    private TextView newTextViewFull(String text, int[] padding, boolean centerText){
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        return tv;
    }
    private EditText newEditText(String hint){
        EditText et = new EditText(this);
        et.setHint(hint);
        return et;
    }

    private void addPlayer(){
        LinearLayout ll = new LinearLayout(this);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final int id = View.generateViewId();
        //Get all player names
        Cursor c = db.get(
                db,                                 //DB
                TableInfo.PLAYER_TABLE_NAME,        //Table
                new String[]{                       //Selection
                        TableInfo.PLAYER_ID,
                        TableInfo.PLAYER_NAME
                },
                null,                               //whereClause
                null,                               //whereArgs
                null,                               //orderBy
                null                                //Limit
        );
        final String[] playerNames = new String[c.getCount()];
        final int[] playerIds = new int[c.getCount()];
        if(c.getCount() > 0){
            c.moveToFirst();
            do{
                try {
                    playerIds[c.getPosition()] = c.getInt(c.getColumnIndexOrThrow(TableInfo.PLAYER_ID));
                    playerNames[c.getPosition()] = c.getString(c.getColumnIndexOrThrow(TableInfo.PLAYER_NAME));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }while(c.moveToNext());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice, playerNames);
        AutoCompleteTextView acTextView =  new AutoCompleteTextView(this);
        acTextView.setAdapter(adapter);
        acTextView.setThreshold(1);
        acTextView.setId(id);
        acTextView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        ll.addView(acTextView);
        alert.setView(ll);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            //@Override
            public void onClick(DialogInterface dialog, int which) {
                AutoCompleteTextView input = (AutoCompleteTextView) ((AlertDialog) dialog).findViewById(id);
                String playerName = input.getText().toString();
                if(playerName == ""){
                    //Snackbar.make()
                    return;
                }
                //Check for matches in playerNames array
                long playerId = 0;
                for(int i = 0; i < playerNames.length; i++){
                    if(playerName.equalsIgnoreCase(playerNames[i])){
                        playerId = playerIds[i];
                    }
                }
                if(playerId == 0){
                    //Create new player
                    playerId = db.insert(
                            db,
                            TableInfo.PLAYER_TABLE_NAME,
                            new String[]{
                                    TableInfo.PLAYER_NAME
                            },
                            new String[]{
                                    playerName
                            }
                    );
                    if(playerId < 0){
                        //Failed to create new player
                        return;
                    }
                }
                //Connect player to game
                long rowId = db.insert(
                        db,
                        TableInfo.GAME_PLAYER_TABLE_NAME,
                        new String[]{
                                TableInfo.GAME_PLAYER_GAME_ID,
                                TableInfo.GAME_PLAYER_PLAYER_ID,
                                TableInfo.GAME_PLAYER_ORDER
                        },
                        new String[]{
                                game.id + "",
                                playerId + "",
                                System.currentTimeMillis() + ""
                        }
                );
                if(rowId > 0){
                    //Success
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    loadView();
                    return;
                }
                //Failure
                Toast.makeText(getApplicationContext(), "Failure " + rowId, Toast.LENGTH_SHORT).show();
            }
        });
        alert.show();
    }

    private TextWatcher twGame = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            //Update table with new values
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}