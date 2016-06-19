package org.advmiguelturra;

import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CloudantManager extends Thread {

    private final String SERVER = Credentials.CLOUDANT_SERVER;
    private final String DBNAME = "games";

    public CloudantManager() {
    }

    private JSONObject commit(Game game, boolean update) throws IOException, JSONException {
        JSONObject jsonGame = game.toJson();
        String url = String.format("http://%s/%s/", SERVER, DBNAME);

        if(!update) {
            //remove id and rev when adding a NEW game
            jsonGame.remove("_id");
            jsonGame.remove("_rev");
        }

        return runPOSTrequest(url, jsonGame.toString(), null);
    }

    /**
     * Add a notification. This should be later shown in the app.
     *
     * @param notification
     * @return The response from the DB
     */
    public JSONObject commitNotification(AdvNotification notification) throws IOException, JSONException {
        String url =  "https://fcm.googleapis.com/fcm/send";
        JSONObject jsonNotify = notification.toJson();

        JSONObject response =  runPOSTrequest(url, jsonNotify.toString(), Credentials.FIREBASE_API_KEY);
        return response;
    }

    /**
     * Run a POST request on the database
     * @param url The url to query
     * @param data The data to be inserted
     * @return The response from the DB
     */
    private JSONObject runPOSTrequest(String url, String data, String auth) throws JSONException, IOException {
        Log.d("RUN POST", data);

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(url);
        JSONObject json = null;

        StringEntity entity = new StringEntity(data, "UTF-8");
        entity.setContentType("application/json");
        request.setEntity(entity);

        if(auth == null)
            request.setHeader("Authorization", "Basic " + Credentials.CLOUDANT_CREDS);
        else
            request.setHeader("Authorization", "key=" + auth);

        HttpResponse response = httpClient.execute(request);
        String responseText = EntityUtils.toString(response.getEntity());
        Log.d("ADV POST", responseText);
        json = new JSONObject(responseText);
        String resp = json.toString(2);
        Log.i("ADV POST", resp);

        if(!json.has("ok") && json.has("message_id")) {
            try {
                json.put("ok", json.getInt("message_id") > 0);
            } catch (JSONException e) {
                Log.w("ADV POST", e);
            }
        }
        return json;
    }

    /**
     * Add a game to the database
     * @param game The game to be added
     * @return The response from the DB
     */
    public JSONObject addGame(Game game) throws IOException, JSONException {
        return commit(game, false);
    }

    /**
     * Modify a game in the database
     * @param game The game, with the updated information
     * @return The response from the DB
     */
    public JSONObject modifyGame(Game game) throws IOException, JSONException {
        return commit(game, true);
    }

    /**
     * Perform a search query to the database.
     *
     * @param searchBy The name of the index to use for the search. It must be defined in the database
     * @param term     The term to search for.
     */
    public ArrayList<JSONObject> search(String searchBy, String term) {
        Log.d("SEARCH", searchBy + " ** " + term);
        ArrayList<JSONObject> retval = new ArrayList<>();
        String url = String.format(
                "https://%s/%s/_design/gamesearch/_search/searchAll?q=%s:\"%s\"&include_docs=true&limit=200",
                SERVER,
                DBNAME,
                searchBy,
                term.replace(" ", "%20"));

        try {
            int total;

            do {
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                // optional default is GET
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", "Basic " + Credentials.CLOUDANT_CREDS);

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject o = new JSONObject(response.toString());
                JSONArray rows = o.getJSONArray("rows");
                total = o.getInt("total_rows");
                String book = o.getString("bookmark");

                for (int i = 0; i < rows.length(); i++) {
                    JSONObject doc = rows.getJSONObject(i);
                    JSONObject game = doc.getJSONObject("doc");

                    retval.add(game);
                }
                url += "&bookmark=" + book;
            }while(retval.size()< total);

        } catch (JSONException | IOException e) {
            Log.e("ADV SEARCH", e.toString());
        }

        return retval;
    }

    /**
     * Get the list of all competitions
     * @return A list of competition names
     */
    public ArrayList<Competition> getCompetitionList() {
        ArrayList<Competition> retval = null;
        String url = String.format(
                "https://%s/%s/_design/search/_view/compList",
                SERVER,
                "competitions" // DBNAME
        );

        String response = runGETrequest(url);

        try {
            JSONObject o = new JSONObject(response);
            JSONArray rows = o.getJSONArray("rows");

            retval = new ArrayList<>();
            List<String> exclusionList = new ArrayList<>();
            exclusionList.add("JM");
            exclusionList.add("JFA");
            exclusionList.add("JFB");
            exclusionList.add("SMA");
            exclusionList.add("SMB");
            exclusionList.add("SF");

            for (int i = 0; i < rows.length(); i++) {
                JSONObject doc = rows.getJSONObject(i);
                if(exclusionList.contains(doc.getString("id"))) continue;
                Competition competition = new Competition(doc.getJSONObject("key"));

                if(retval.contains(competition))
                    continue;

                retval.add(competition);
            }
        } catch (JSONException e) {
            Log.e("ADV cloudant get", e.toString());
        }

        return retval;
    }

    /**
     * Run a query to find the days in wich a competition will be held
     * @param competition The name of the competition whose days we want to know
     * @return The response from the DB
     */
    public ArrayList<String> getDaysForCompetition(String competition) {
        ArrayList<String> retval = new ArrayList<>();
        String url = String.format(
                "https://%s/%s/_design/search/_search/searchAll?q=name:%s&group=true",
                SERVER,
                "competitions", //DBNAME
                competition.replace(" ", "%20"));

        String response = runGETrequest(url);
        ArrayList<Calendar> dates = new ArrayList<>();

        try{
            JSONObject o = new JSONObject(response);
            JSONArray rows = o.getJSONArray("rows");

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

            for (int i = 0; i < rows.length(); i++) {
                JSONObject doc = rows.getJSONObject(i);
                String date = doc.getString("key");
                Calendar cal = Calendar.getInstance();
                cal.setTime(sdf.parse(date));
                dates.add(cal);// all done
            }
        } catch (JSONException | ParseException e) {
            Log.e("ADV get days", e.toString());
        }

        Collections.sort(dates);
        for(Calendar d : dates) {
            retval.add(String.format("%02d-%02d-%04d",
                    d.get(Calendar.DAY_OF_MONTH),
                    d.get(Calendar.MONTH)+1,
                    d.get(Calendar.YEAR)) );
        }

        return retval;
    }

    /**
     * Run a GET request to the database
     * @param url The url to query
     * @return The response from the DB
     */
    private String runGETrequest(String url) {
        StringBuilder response = new StringBuilder();

        try {
            URL obj = new URL(url);
            Log.d("URL", obj.toString());
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Basic " + Credentials.CLOUDANT_CREDS);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (ConnectException e) {
            Log.e("ADV RUN GET", e.toString());
        }catch (ProtocolException e) {
            Log.e("ADV RUN GET", e.toString());
        } catch (MalformedURLException e) {
            Log.e("ADV RUN GET", e.toString());
        } catch (IOException e) {
            Log.e("ADV RUN GET", e.toString());
        }

        return response.toString();
    }

    //TODO crear un metodo para borrar varios partidos a la vez

    /**
     * Delete a Game from Cloudant Database
     * @param game The game to delete
     * @return The response from the DB
     */
    public JSONObject delete(Game game) {
        String url = String.format("http://%s/%s/%s?rev=%s",
                SERVER,
                DBNAME,
                game.getId(),
                game.getRev());

        try {
            Log.d("DELETE", game.toString());
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpDelete request = new HttpDelete(url);
            request.setHeader("Authorization", "Basic " + Credentials.CLOUDANT_CREDS);

            HttpResponse response = httpClient.execute(request);
            String responseText = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(responseText);

            Log.d("DELETE RESPONSE", json.toString(2));
            return json;
        } catch (JSONException | IOException e) {
            Log.e("ADV cloudant delete", e.toString());
        }

        return null;
    }

    public JSONObject getTeam(String teamId) {
        JSONObject retval = null;
        String url = String.format("https://%s/%s/%s",
                SERVER,
                "competitions", //DBNAME
                teamId);
        String response = runGETrequest(url);

        try {
            retval = new JSONObject(response);
        } catch (JSONException e) {
            Log.e("ADV GET TEAM", e.toString());
        }

        return retval;
    }
}
