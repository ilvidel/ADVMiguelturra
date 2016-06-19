package org.advmiguelturra;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;


public class RankingActivity extends ActionBarActivity {

    private ArrayList<Game> partidos;
    private ArrayList<Team> ranking;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ranking, menu);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        String cat = getIntent().getStringExtra("category");
        CloudantManager cloudant = new CloudantManager();
        ArrayList<JSONObject> games = cloudant.search("searchComp", "VoleyPlaya 2015");
        getRelevantGames(games);
        getRanking();
        buildGlobalRanking();
    }

    private void buildGlobalRanking() {
        ScrollView scroll = (ScrollView) findViewById(R.id.scrollRanking);
        TableLayout list = new TableLayout(this);
        list.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TableRow row = (TableRow) findViewById(R.id.headerRow);
        ((LinearLayout) row.getParent()).removeView(row);
        row.setVisibility(View.VISIBLE);
        row.setBackgroundColor(getResources().getColor(R.color.accent_material_dark));
        LinearLayout.LayoutParams rowLayoutParams = (LinearLayout.LayoutParams) row.getLayoutParams();

        LinearLayout.LayoutParams thin = (LinearLayout.LayoutParams) row.getChildAt(0).getLayoutParams();
        LinearLayout.LayoutParams wide = (LinearLayout.LayoutParams) row.getChildAt(1).getLayoutParams();

        list.addView(row);

        int i=1;
        for(Team e : ranking) {
            row = new TableRow(this);
            row.setLayoutParams(rowLayoutParams);
            TextView cell = new TextView(this);
            cell.setText(String.valueOf(i++));
            cell.setLayoutParams(thin);
            cell.setGravity(Gravity.CENTER);
            row.addView(cell);

            cell = new TextView(this);
            cell.setText(e.getName());
            cell.setLayoutParams(wide);
            cell.setEllipsize(TextUtils.TruncateAt.END);
            cell.setSingleLine(true);
            row.addView(cell);

            cell = new TextView(this);
            cell.setText(String.valueOf(e.getPlayed()));
            cell.setLayoutParams(thin);
            cell.setGravity(Gravity.CENTER);
            row.addView(cell);

            cell = new TextView(this);
            cell.setText(String.valueOf(e.getWon()));
            cell.setLayoutParams(thin);
            cell.setGravity(Gravity.CENTER);
            row.addView(cell);

            cell = new TextView(this);
            cell.setText(String.valueOf(e.getLost()));
            cell.setLayoutParams(thin);
            cell.setGravity(Gravity.CENTER);
            row.addView(cell);

            cell = new TextView(this);
            cell.setText(String.valueOf(e.getSetsWon()));
            cell.setLayoutParams(thin);
            cell.setGravity(Gravity.CENTER);
            row.addView(cell);

            cell = new TextView(this);
            cell.setText(String.valueOf(e.getSetsLost()));
            cell.setLayoutParams(thin);
            cell.setGravity(Gravity.CENTER);
            row.addView(cell);

            cell = new TextView(this);
            cell.setText(String.valueOf(e.getPointsWon()));
            cell.setLayoutParams(thin);
            cell.setGravity(Gravity.CENTER);
            row.addView(cell);

            cell = new TextView(this);
            cell.setText(String.valueOf(e.getPointsLost()));
            cell.setLayoutParams(thin);
            cell.setGravity(Gravity.CENTER);
            row.addView(cell);

            cell = new TextView(this);
            cell.setText(String.valueOf(e.getTotal()));
            cell.setLayoutParams(thin);
            cell.setGravity(Gravity.CENTER);
            row.addView(cell);

            list.addView(row);
        }

        scroll.addView(list);
    }

    private void getRelevantGames(ArrayList<JSONObject> games) {
        partidos = new ArrayList<>();
        for (JSONObject o : games) {
            Game g = new Game(o);
            if(g.getPhase() == Phase.LEAGUE)
                partidos.add(g);
        }
    }

    private void getRanking() {
        HashMap<String, Team> equipos = new HashMap<>();

        for(Game game : partidos) {
            String teama = game.getTeamA();
            String teamb = game.getTeamB();
            if(!equipos.containsKey(teama)) {
                equipos.put(teama, new Team(teama));
            }

            if(!equipos.containsKey(teamb)) {
                equipos.put(teamb, new Team(teamb));
            }

            if(game.getWonA() > game.getWonB()) {
                equipos.get(teama).ganar(game.getPointsA(), game.getPointsB(), game.getWonB());
                equipos.get(teamb).perder(game.getPointsB(), game.getPointsA(), game.getWonB());
            }
            else if(game.getWonA() < game.getWonB()) {
                equipos.get(teama).perder(game.getPointsA(), game.getPointsB(), game.getWonA());
                equipos.get(teamb).ganar(game.getPointsB(), game.getPointsA(), game.getWonA());
            }
        }

        ranking = getAsList(equipos.values());
        Collections.sort(ranking);
    }

    private ArrayList<Team> getAsList(Collection<Team> teams) {
        ArrayList<Team> retval = new ArrayList<>();
        for(Team e : teams)
            retval.add(e);

        return retval;
    }

}
