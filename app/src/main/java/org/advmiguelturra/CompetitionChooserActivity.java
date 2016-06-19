package org.advmiguelturra;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;


public class CompetitionChooserActivity extends AppCompatActivity {

    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competition_chooser);

        gridView = (GridView) findViewById(R.id.competitionList);
        buildGUI();
    }

//     @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private void buildGUI() {
        CloudantTask task = new CloudantTask(getApplicationContext());
        task.execute(CloudantTask.Action.GET_COMPS);
        ArrayList<Competition> list = null;
        Object obj = new Object();

        try {
            obj = task.get();
            list = (ArrayList<Competition>) obj;
        } catch (InterruptedException e) {
            Log.e("COMP CHOOSER", "Operation interrupted: " + e.toString());
        } catch (ExecutionException e) {
            Log.e("COMP CHOOSER", e.toString());
        } catch (ClassCastException e) { // la respuesta no es lo que se esperaba (error)
            Log.w("COMPETITIONS", obj.toString());
            Toast t = Toast.makeText(getApplicationContext(), "No se ha encontrado una conexión válida", Toast.LENGTH_LONG);
            t.show();
        }

        if(list == null || list.isEmpty()) {
            Toast t = Toast.makeText(getApplicationContext(), "No se encontraron resultados", Toast.LENGTH_SHORT);
            t.show();
            return;
        }

        Collections.sort(list);
        gridView.setAdapter(new CompetitionListAdapter(this, list));
    }
}
