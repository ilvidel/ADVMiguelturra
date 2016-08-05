package org.advmiguelturra;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;

/**
 * An interface to perform queries to the database, in the background
 */
public class CloudantTask extends AsyncTask {

    public enum Action {
        NEW,
        EDIT,
        SEARCH,
        GET_COMPS,
        GET_COMP_DAYS,
        DELETE,
        GET_TEAM,
        ADD_NOTIFICATION,
        GET_GAME_ID,
        GET_GAME;
    }

    private Context context;
    private Action action;

    private static final String TEXT_SUCCESS = "Operación completada";

    public CloudantTask(Context context) {
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        action = (Action) params[0];
        CloudantManager cloudant = new CloudantManager();
        JSONObject response = new JSONObject();

        if(!hasConnection()) {
            return new ConnectException("ERROR: No se ha encontrado una conexión válida");
        }

        switch (action) {
            case EDIT:
                response = cloudant.modifyGame((Game) params[1]);
                break;
            case NEW:
                response = cloudant.addGame((Game) params[1]);
                break;
            case SEARCH:
                return cloudant.search((String) params[1]);
            case ADD_NOTIFICATION:
                response = cloudant.commitNotification((AdvNotification) params[1]);
                break;
            case DELETE:
                response = cloudant.delete((Game) params[1]);
                break;
            case GET_COMPS:
                return cloudant.getCompetitionList();
            case GET_COMP_DAYS:
                return cloudant.getDaysForCompetition((String) params[1]);
            case GET_TEAM:
                return cloudant.getTeam((String) params[1]);
            case GET_GAME_ID:
                return cloudant.getNewGameId((String) params[1]);
            case GET_GAME:
                return cloudant.getGame((String) params[1]);
        }

        try {
            if (response.getBoolean("ok")) return TEXT_SUCCESS;
            else return response.toString(3);
        }catch (JSONException e){
            Log.i("ADV::CloudantTask", response.toString());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        Log.i("ADV::CloudantTask", "Taks finished: " + action.toString());
    }

    public boolean hasConnection() {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(activeNetwork==null)
            return false;

        boolean isConnected = activeNetwork.isConnectedOrConnecting();
        int connType = activeNetwork.getType();
        Log.d("CONN TYPE", activeNetwork.getTypeName());

        if(connType != ConnectivityManager.TYPE_WIFI && connType != ConnectivityManager.TYPE_MOBILE) {
            isConnected = false;
            Log.i("ADV::CloudantTask", "No hay conexión");
        }

        return isConnected;
    }
}
