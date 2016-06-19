package org.advmiguelturra;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by nacho on 30/06/14.
 */
public class Game implements Serializable {
    private String id, rev;
    private Calendar date; //date and time
    private Category category;
    private Division division;
    private String competition;
    private Phase phase;
    private String teamA, teamB;
    private int[] setsA, setsB;
    private char pool;

    public Game() {
        rev = "";
        date = Calendar.getInstance();
        category = Category.AFICIONADOS;
        division = Division.MEN;
        competition = "Competición";
        phase = Phase.LEAGUE;
        pool = 'A';
        teamA = "EquipoA";
        teamB = "EquipoB";
        setsA = new int[5];
        setsB = new int[5];
    }

    public Game(JSONObject game) {
        try {
            this.id = game.getString("_id");
            this.rev = game.getString("_rev");

            setCompetition(game.getString("competition"));
            setDate(game.getString("date"));
            setTime(game.getString("time"));
            setCategory(Category.valueOf(game.getString("category")));
            setDivision(Division.valueOf(game.getString("division")));
            setPhase(Phase.valueOf(game.getString("phase")));
            setPool(game.getString("pool"));

            JSONObject team1 = game.getJSONObject("team1");
            JSONObject team2 = game.getJSONObject("team2");
            setTeamA(team1.getString("name"));
            setTeamB(team2.getString("name"));

            int aux[] = new int[5];
            aux[0] = Integer.parseInt(team1.getString("set1"));
            aux[1] = Integer.parseInt(team1.getString("set2"));
            aux[2] = Integer.parseInt(team1.getString("set3"));
            aux[3] = Integer.parseInt(team1.getString("set4"));
            aux[4] = Integer.parseInt(team1.getString("set5"));
            setSetsA(aux);

            aux[0] = Integer.parseInt(team2.getString("set1"));
            aux[1] = Integer.parseInt(team2.getString("set2"));
            aux[2] = Integer.parseInt(team2.getString("set3"));
            aux[3] = Integer.parseInt(team2.getString("set4"));
            aux[4] = Integer.parseInt(team2.getString("set5"));
            setSetsB(aux);
        } catch (JSONException e) {
            Log.e("JSON to GAME", e.toString());
        }
    }

    public String getTime() {
        return String.format(Locale.getDefault(),
                "%02d:%02d",
                date.get(Calendar.HOUR_OF_DAY),
                date.get(Calendar.MINUTE));
    }

    public void setTime(String hora) {
        String[] aux = hora.split(":");
        if (date==null)
            date = Calendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(aux[0]));
        date.set(Calendar.MINUTE, Integer.parseInt(aux[1]));
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        if (date == null){
            this.date = date;
            return;
        }

        this.date.set(date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH));
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public String getCompetition() {
        return competition;
    }

    public void setCompetition(String competition) {
        this.competition = competition.toUpperCase();
    }

    public String getTeamA() {
        return teamA;
    }

    public void setTeamA(String teamA) {
        this.teamA = teamA;
    }

    public String getTeamB() {
        return teamB;
    }

    public void setTeamB(String teamB) {
        this.teamB = teamB;
    }

    public char getPool() {
        return pool;
    }

    public void setPool(char pool) {
        this.pool = pool;
    }

    public void setPool(String pool) {
        this.pool = Character.toUpperCase(pool.charAt(0));
    }

    public int[] getSetsA() {
        return setsA;
    }

    public void setSetsA(int[] setsA) {
        this.setsA = Arrays.copyOf(setsA, setsA.length);
    }

    public int[] getSetsB() {
        return setsB;
    }

    public void setSetsB(int[] setsB) {
        this.setsB = Arrays.copyOf(setsB, setsB.length);
    }

    public int getWonA() {
        int won = 0;
        if(setsA==null) return 0;

        for(int i=0; i<setsA.length; i++) {
            if(setsA[i]>setsB[i]) {
                won++;
            }
        }

        return won;
    }

    public int getWonB() {
        int won = 0;
        if(setsB==null) return 0;

        for(int i=0; i<setsA.length; i++) {
            if(setsB[i]>setsA[i]) {
                won++;
            }
        }

        return won;
    }

    public int getPointsA() {
        return setsA[0] + setsA[1] + setsA[2];
    }

    public int getPointsB() {
        return setsB[0] + setsB[1] + setsB[2];
    }

    @Override
    protected Game clone() {
        Game p = new Game();
        p.setCategory(category);
        p.setCompetition(competition);
        p.setPool(pool);
        p.setTeamA(teamA);
        p.setTeamB(teamB);
        p.setSetsA(setsA.clone());
        p.setSetsB(setsB.clone());
        p.setPhase(phase);
        p.setDate((Calendar) date.clone());

        return p;
    }

    public void setDate(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        try {
            date.setTime(sdf.parse(s));// all done
        } catch (ParseException e) {
            String[] aux = s.split("[-/ ]");
            if (date==null)
                date = Calendar.getInstance();
            date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(aux[0]));
            date.set(Calendar.MONTH, Integer.parseInt(aux[1]) - 1);
            date.set(Calendar.YEAR, Integer.parseInt(aux[2]));
        }
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "%s - %02d:%02d\t%s\t%s\tvs\t%s\t%d-%d",
                getDateAsString(),
                date.get(Calendar.HOUR_OF_DAY),
                date.get(Calendar.MINUTE),
                category.toString(),
                teamA, teamB,
                getWonA(), getWonB());
    }

    public String jsonToString() {
        String ret = "";
        try {
            ret= toJson().toString(4);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public String getDateAsString() {
        return String.format(Locale.getDefault(),
                "%02d %s %04d",
                date.get(Calendar.DAY_OF_MONTH),
                date.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                date.get(Calendar.YEAR)
        );
    }

    public String getCanonicalDate() {
        return String.format(Locale.getDefault(),
                "%02d-%02d-%04d",
                date.get(Calendar.DAY_OF_MONTH),
                date.get(Calendar.MONTH)+1,
                date.get(Calendar.YEAR)
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    @Override
    public boolean equals(Object o) {
        boolean eq = false;
        Game other = (Game) o;
        eq = date.equals(other.date) &&
                category == other.category &&
                division == other.division &&
                teamA == other.teamA &&
                teamB == other.teamB &&
                Arrays.equals(setsA, other.setsA) &&
                Arrays.equals(setsB, other.setsB) &&
                phase == other.phase &&
                pool == other.pool;

        return eq;
    }

    public JSONObject toJson() {
        JSONObject team1 = new JSONObject();
        JSONObject team2 = new JSONObject();
        JSONObject partido = new JSONObject();

        try {
            team1.put("name", teamA);
            team1.put("set1", setsA[0]);
            team1.put("set2", setsA[1]);
            team1.put("set3", setsA[2]);
            team1.put("set4", setsA[3]);
            team1.put("set5", setsA[4]);

            team2.put("name", teamB);
            team2.put("set1", setsB[0]);
            team2.put("set2", setsB[1]);
            team2.put("set3", setsB[2]);
            team2.put("set4", setsB[3]);
            team2.put("set5", setsB[4]);

            partido.put("_id", getId());
            partido.put("_rev", getRev());
            partido.put("date", String.format(Locale.getDefault(),
                    "%02d-%02d-%04d",
                    date.get(Calendar.DAY_OF_MONTH),
                    date.get(Calendar.MONTH) + 1,
                    date.get(Calendar.YEAR)));
            partido.put("time", String.format(Locale.getDefault(),
                    "%02d:%02d",
                    date.get(Calendar.HOUR_OF_DAY),
                    date.get(Calendar.MINUTE)));
            partido.put("category", category.toString());
            partido.put("division", division.toString());
            partido.put("pool", String.valueOf(pool));
            partido.put("phase", phase.toString());
            partido.put("competition", competition);

            partido.put("team1", team1);
            partido.put("team2", team2);
        } catch (JSONException e) {
            Log.e("JSON", e.toString());
        }

        return partido;
    }

    public static Game random() {
        Game g = new Game();
        g.category = Category.values()[random(Category.values().length)];
        g.date = Calendar.getInstance();
        g.date.set(random(2014, 2015), random(12), random(28), random(10, 20), 0);
        g.division = Division.values()[random(Division.values().length)];
        g.phase = Phase.values()[random(Phase.values().length)];
        g.pool = (char) ('A' + random(5));
        g.competition = "pruebas";

        g.setsA = new int[]{25, 20, 25, 0, 0};
        g.setsB = new int[]{22, 25, 23, 0, 0};

        return g;
    }


    private static int random(int top) {
        return random(0, top);
    }

    private static int random(int bottom, int top) {
        int aux = (int) (Math.random()* (top-bottom));
        return aux+bottom;
    }
}
