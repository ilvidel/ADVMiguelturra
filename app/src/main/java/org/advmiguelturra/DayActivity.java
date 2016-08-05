package org.advmiguelturra;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.concurrent.ExecutionException;


public class DayActivity extends AppCompatActivity {

    private String query;
    private ArrayList<Game> games;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        query = getIntent().getStringExtra("query");
        Log.d("DAY", "CreateDay query: " + query);
        showGameList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_day, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                Intent prefs = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(prefs);
            case R.id.action_order_category:
                orderByCategory();
                break;
            case R.id.action_order_date:
                orderByDate();
                break;
            case R.id.open_search_activity:
                open_search_activity();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void open_search_activity() {
        Intent search = new Intent(getApplicationContext(), SearchActivity.class);
        search.putExtra("competition", query.substring("competition:".length()));
        startActivity(search);
    }

    private void orderByDate() {
        Log.d("DAY", "Reordering by date");
        Collections.sort(games, new DateComparator());
        populateGUI();
    }

    private void orderByCategory() {
        Log.d("DAY", "Reordering by category");
        Collections.sort(games, new CategoryComparator());
        populateGUI();
    }

    /**
     * Retrieves all games corresponding to the current query
     */
    private void showGameList() {
        if (query == null || query.isEmpty()){
            Log.w("DAY", "No se ha indicado ninguna competición");
            return;
        }

        CloudantTask cloudant = new CloudantTask(getApplicationContext());
        cloudant.execute(CloudantTask.Action.SEARCH, query);
        ArrayList<JSONObject> list = null;
        Object obj = null;

        try {
            obj = cloudant.get();
            list = (ArrayList<JSONObject>) obj;
            if(list == null) return;
        } catch (InterruptedException e) {
            Log.e("DAY", "Operation interrupted: " + e);
        } catch (ExecutionException e) {
            Log.e("DAY", e.toString());
        } catch (ClassCastException e) { // la respuesta no es lo que se esperaba (error)
            Log.w("COMPETITIONS", obj.toString());
            Toast t = Toast.makeText(getApplicationContext(), "No se ha encontrado una conexión válida", Toast.LENGTH_LONG);
            t.show();
            return;
        }

        games = new ArrayList<>();
        for(JSONObject game : list)
            games.add(new Game(game));

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String sorting = "";
        if(sharedPref != null)  sorting = sharedPref.getString(SettingsActivity.PREF_SORTING, "date");

        if(sorting.equals("category"))
            orderByCategory();
        else
            orderByDate();

        scroll();
    }

    /**
     * Populate the list of games to display
     */
    private void populateGUI(){
        if(games.isEmpty()) {
            Log.w("DAY", "No se encontraron partidos");
            Toast t = Toast.makeText(this, "No se encontraron partidos", Toast.LENGTH_SHORT);
            t.show();
            return;
        }

        ListView listView = (ListView) findViewById(R.id.calendarList);
        listView.setAdapter(new GameListAdapter(this, games));
        scroll();
    }


    @Override
    protected void onResume() {
        super.onResume();
        showGameList();
    }

    private void scroll() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        int todayPos = 0;

        for(Game g : games) {
            if(g.getDate().before(today)) {
                todayPos++;
            } else {
                break;
            }
        }

        Log.d("DAY", "Scrolling...");
        ListView listView = (ListView) findViewById(R.id.calendarList);
        listView.setAdapter(new GameListAdapter(this, games));
        listView.setSelection(todayPos==0?0:todayPos-1);
//        listView.smoothScrollToPosition(15);
    }

}
