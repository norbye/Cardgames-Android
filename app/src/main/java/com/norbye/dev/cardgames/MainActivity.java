package com.norbye.dev.cardgames;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.norbye.dev.cardgames.db.DBOpenHelper;
import com.norbye.dev.cardgames.db.TableData;

public class MainActivity extends AppCompatActivity {

    Context context = this;
    DBOpenHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //context.deleteDatabase("cardgames");
        db = new DBOpenHelper(context);

        //Action when floating button is clicked
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add custom game", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        //Setup listview adapter
        final ListView listview = (ListView) findViewById(R.id.game_list);
        //Fetch values
        Cursor c = db.get(db,
                TableData.TableInfo.GAMETYPE_TABLE_NAME,
                new String[]{
                        TableData.TableInfo.GAMETYPE_ID,
                        TableData.TableInfo.GAMETYPE_NAME},
                null,
                null,
                TableData.TableInfo.GAMETYPE_NAME,
                null);
        String[] values = new String[c.getCount()];
        if(values.length > 0) {
            c.moveToFirst();
            do {
                try {
                    values[c.getPosition()] = c.getString(c.getColumnIndexOrThrow(TableData.TableInfo.GAMETYPE_ID)) + "," +
                            c.getString(c.getColumnIndexOrThrow(TableData.TableInfo.GAMETYPE_NAME));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
        }
        for(int i = 0; i < values.length; i++){
            if(values[i] != null)
                Log.d("Cardgames", values[i]);
        }
        //Insert values into listview
        final CustomArrayAdapter adapter = new CustomArrayAdapter(this,
                values);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                //Send user to a view where it can start a game
                try{
                    Intent i = new Intent(context, GameActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("game_type_id", Integer.parseInt(view.getTag().toString()));
                    i.putExtras(b);
                    startActivity(i);
                }catch(NullPointerException e) {
                    Snackbar.make(view, "Ugylidig spill id", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            }

        });
    }

    public class CustomArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] values;

        public CustomArrayAdapter(Context context, String[] values) {
            super(context, -1, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.game_list_1, parent, false);
            //Separate ID and title
            if(values[position] == null){
                return rowView;
            }
            String[] split = values[position].split(",", 2);
            int id = Integer.parseInt(split[0]);
            String text = split[1];
            TextView textView = (TextView) rowView.findViewById(R.id.list_first_line);
            //ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
            textView.setText(text);
            rowView.setTag(id);
            // change the icon for Windows and iPhone
            /*String s = values[position];
            if (s.startsWith("iPhone")) {
                imageView.setImageResource(R.drawable.no);
            } else {
                imageView.setImageResource(R.drawable.ok);
            }*/

            return rowView;
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
