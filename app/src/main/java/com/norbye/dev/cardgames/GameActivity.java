package com.norbye.dev.cardgames;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
    Context context = this;
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
            db.insert(
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
        }

        //Add player
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(game.addPlayer(null)) {
                    Snackbar.make(view, "Added player", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    loadView();
                }
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
            for(int i = 0; i < players.length; i++){
                TableRow tr = new TableRow(this);
                if(players[i].name != ""){
                    tr.addView(newTextView(players[i].name));
                }else {
                    EditText etName = newEditText("Navn");
                    etName.setTag(players[i]);
                    //Get all player names
                    Cursor c = db.get(
                            db,                                         //DB
                            TableInfo.PLAYER_TABLE_NAME,                  //Table
                            new String[]{                               //Selection
                                    TableInfo.PLAYER_ID,
                                    TableInfo.PLAYER_NAME
                            },
                            null,                               //whereClause
                            null,            //whereArgs
                            null,                                       //orderBy
                            null                                         //Limit
                    );
                    //Add TextWatch to etName to automatically update name when edittext is left, and reload the view
                    etName.addTextChangedListener(new EditNameTextWatcher(etName));
                    tr.addView(etName);
                }
                if(gameType.rounds == 0){
                    tr.addView(newEditText(""));
                }else{
                    for(int k = 0; k < gameType.rounds; k++){
                        tr.addView(newEditText(""));
                    }
                }
                //Add sum and position
                tr.addView(newTextView("0"));
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

    private TextWatcher gameTw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private class EditNameTextWatcher implements TextWatcher{

        private View view;
        private EditNameTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void afterTextChanged(Editable editable) {
            String name = editable.toString();
            //Check if name corresponds with any registered players
        }
    }

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