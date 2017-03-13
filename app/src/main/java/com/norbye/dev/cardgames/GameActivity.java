package com.norbye.dev.cardgames;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
            if(!newGame()){
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

    private boolean newGame(){
        //Clear earlier games
        ContentValues cv = new ContentValues();
        cv.put(TableInfo.GAME_ACTIVE, 0);
        db.update(
                db,
                TableInfo.GAME_TABLE_NAME,
                cv,
                TableInfo.GAME_TYPE + "=? AND " +
                        TableInfo.GAME_ACTIVE + "=?",
                new String[]{
                        gameType.id + "",
                        "1"
                }
        );
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
        if(gameId != 0){
            game = new Game(context, gameId);
            if(game != null){
                return true;
            }
        }
        return false;
    }

    public void loadView(){
        TableLayout tl = (TableLayout) findViewById(R.id.game_tableLayout);
        tl.removeAllViews();
        if(gameType.direction_vertical) {
            TableRow trTop = new TableRow(this);
            Player[] players = game.getPlayers();
            trTop.addView(newTextView("", ""));
            int rows = 0;
            ArrayList score = new ArrayList();
            for(int i = 0; i < players.length; i++){
                trTop.addView(newTextView(players[i].name, "namevalue"));
                score.add(i, players[i].getScore(game));
                if(((int[]) score.get(i)).length > rows){
                    rows = ((int[]) score.get(i)).length;
                }
            }
            tl.addView(trTop);
            //Add game values
            int[] sum = new int[players.length];
            for(int i = 0; i < rows + 1; i++){ //Loop rows
                TableRow tr = new TableRow(this);
                //Add label
                tr.addView(newTextView((i + 1) + "", "number"));
                //Add values
                for(int k = 0; k < score.size(); k++){ //Loop players
                    if(i < ((int[]) score.get(k)).length && ((int[]) score.get(k))[i] != -1) {
                        sum[k] += ((int[]) score.get(k))[i];
                        EditText et = newEditTextNum(((int[]) score.get(k))[i] + "");
                        et.setTag(k);
                        et.addTextChangedListener(new GameTextWatcher(game, players[k], et));
                        tr.addView(et);
                    }else{
                        EditText et = newEditTextNum("");
                        et.setTag(k);
                        et.addTextChangedListener(new GameTextWatcher(game, players[k], et));
                        tr.addView(et);
                    }
                }
                tl.addView(tr);
            }
            //Add sum and position
            TableRow trSum = new TableRow(this);
            trSum.addView(newTextView("Sum", "sum"));
            for(int i = 0; i < players.length; i++){
                trSum.addView(newTextView(sum[i] + "", "sumvalue"));
            }
            tl.addView(trSum);
            TableRow trPosition = new TableRow(this);
            trPosition.addView(newTextView("Plass", "position"));
            for(int i = 0; i < players.length; i++){
                Integer[] sum2 = new Integer[sum.length];
                for(int k = 0; k < sum.length; k++){
                    sum2[k] = Integer.valueOf(sum[k]);
                }
                Arrays.sort(sum2, Collections.reverseOrder());
                int k;
                for(k = 0; i < sum.length && k < sum2.length; k++){
                    if(sum[i] == sum2[k].intValue()){
                        break;
                    }
                }
                trPosition.addView(newTextView((k + 1) + "", "positionvalue"));
            }
            tl.addView(trPosition);
        }else{
            //Print the top layer
            TableRow trTop = new TableRow(this);
            trTop.addView(newTextView("Navn", "name"));
            if(gameType.rounds == 0){
                //TODO display all used indexes
                trTop.addView(newTextView("1", "number"));
                trTop.addView(newTextView("Sum", "sum"));
                trTop.addView(newTextView("Plass", "position"));
            }else{
                for(int i = 1; i <= gameType.rounds; i++){
                    trTop.addView(newTextView(i + "", "number"));
                }
                //Add sum and position
                trTop.addView(newTextView("Sum", "sum"));
                trTop.addView(newTextView("Plass", "position"));
            }
            tl.addView(trTop, 0);
            //Print the other layers
            Player[] players = game.getPlayers();
            int[] sum = new int[players.length];
            for(int i = 0; i < players.length; i++){
                //Get the players score
                int[] score = players[i].getScore(game);
                System.out.println(Arrays.toString(score));
                TableRow tr = new TableRow(this);
                tr.setTag("playerID-" + players[i].id);
                tr.addView(newTextView(players[i].name, "namevalue"));
                if(gameType.rounds == 0){
                    //Fetch inserted rows and add one more
                    for(int k = 0; k < score.length; k++){
                        sum[i] += score[k];
                        EditText et = newEditTextNum(score[k] + "");
                        et.setTag(k);
                        et.addTextChangedListener(new GameTextWatcher(game, players[i], et));
                        tr.addView(et);
                    }
                    EditText et = newEditTextNum("");
                    et.setTag(-1);
                    et.addTextChangedListener(new GameTextWatcher(game, players[i], et));
                    tr.addView(et);
                }else{
                    //Fetch inserted values for each row, but display all rows
                    for(int k = 0; k < gameType.rounds; k++){
                        if(k < score.length && score[k] != -1) {
                            sum[i] += score[k];
                            System.out.println("score: " + k + " " + score[k]);
                            EditText et = newEditTextNum(score[k] + "");
                            et.setTag(k);
                            et.addTextChangedListener(new GameTextWatcher(game, players[i], et));
                            tr.addView(et);
                        }else{
                            EditText et = newEditTextNum("");
                            et.setTag(k);
                            et.addTextChangedListener(new GameTextWatcher(game, players[i], et));
                            tr.addView(et);
                        }
                    }
                }
                //Add sum and position
                System.out.println("sum: " + sum[i]);
                tr.addView(newTextView(sum[i] + "", "sumvalue"));
                tr.addView(newTextView("", "positionvalue"));
                //Append to tablelayout
                tl.addView(tr);
            }
            //Edit sum
            for(int i = 0; i < players.length; i++){
                TableRow tr = (TableRow) tl.findViewWithTag("playerID-" + players[i].id);
                if(tr == null){
                    continue;
                }
                Integer[] sum2 = new Integer[sum.length];
                for(int k = 0; k < sum.length; k++){
                    sum2[k] = Integer.valueOf(sum[k]);
                }
                Arrays.sort(sum2, Collections.reverseOrder());
                System.out.println("sum2: " + Arrays.toString(sum2));
                int k;
                for(k = 0; i < sum.length && k < sum2.length; k++){
                    if(sum[i] == sum2[k].intValue()){
                        System.out.println("Foundit!" + k + " " + sum[i]);
                        break;
                    }
                }
                TextView position = (TextView) tr.getChildAt(tr.getChildCount() - 1);
                position.setText((k + 1) + "");
            }
        }
    }
    private TextView newTextView(String text, String type){
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        int padding_in_dp = 6;  // 6 dps
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
        switch(type){
            case "number":
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                break;
            case "sum":
            case "position":
                tv.setPadding(Math.round(padding_in_px/2), 0, Math.round(padding_in_px/2), 0);
                break;
            case "sumvalue":
            case "positionvalue":
                tv.setGravity(Gravity.RIGHT);
                break;
            case "name":
            case "namevalue":
                tv.setPadding(0, 0, padding_in_px, 0);
                break;

        }
        return tv;
    }
    private EditText newEditTextNum(String text){ return newEditText(text, true); }
    private EditText newEditText(String text, boolean number){
        EditText et = new EditText(this);
        et.setText(text);
        if(number){
            et.setInputType(InputType.TYPE_CLASS_NUMBER);
            float measureText = et.getPaint().measureText("000");
            et.setWidth(et.getPaddingLeft() + et.getPaddingRight() + (int) measureText);
            et.setGravity(Gravity.CENTER_HORIZONTAL);
        }
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
        acTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ll.addView(acTextView);
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        alert.setView(ll);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            //@Override
            public void onClick(DialogInterface dialog, int which) {
                AutoCompleteTextView input = (AutoCompleteTextView) ((AlertDialog) dialog).findViewById(id);
                String playerName = input.getText().toString();
                if(playerName == "" || playerName == null || playerName.isEmpty()){
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

    public class GameTextWatcher implements TextWatcher {

        private Game game;
        private Player player;
        private EditText editText;

        public GameTextWatcher(Game game, Player player, EditText editText) {
            this.game = game;
            this.player = player;
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            //Update table with new values
            try {
                int index = Integer.parseInt(editText.getTag().toString());
                int value = 0;
                if(editable.toString() != "") {
                    value = Integer.parseInt(editable.toString());
                }
                if(player.setScore(game, index, value)){
                    loadView();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
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
        }else if(id == R.id.action_new_game){
            //Create new game
            new AlertDialog.Builder(context)
                    .setMessage(getResources().getString(R.string.confirm_new_game))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            if(newGame()) {
                                loadView();
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return true;
        }else if(id == R.id.action_new_player){
            addPlayer();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}