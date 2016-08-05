package org.advmiguelturra;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Set;

public class SearchActivity extends AppCompatActivity {

    private String competition;
    private Spinner phaseLabel, category, division;
    private EditText teamQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        competition = getIntent().getStringExtra("competition").replace("%20", " ");
        initGUI();
    }

    private void initGUI(){

        TextView title = (TextView)findViewById(R.id.search_title);
        title.setText("Buscar partido en " + competition);

        phaseLabel = (Spinner) findViewById(R.id.phaseLabel);
        ArrayAdapter<CharSequence> phaseAdapter = ArrayAdapter.createFromResource(this, R.array.phases, android.R.layout.simple_spinner_item); // Create an ArrayAdapter using the string array and a default spinner layout
        phaseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Specify the layout to use when the list of choices appears
        phaseLabel.setAdapter(phaseAdapter); // Apply the adapter to the spinner
        phaseLabel.setSelection(phaseAdapter.getPosition("--"));

        category= (Spinner) findViewById(R.id.categoryLabel);
        ArrayAdapter<CharSequence> catAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item); // Create an ArrayAdapter using the string array and a default spinner layout
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Specify the layout to use when the list of choices appears
        category.setAdapter(catAdapter); // Apply the adapter to the spinner
        category.setSelection(catAdapter.getPosition("--"));

        division = (Spinner) findViewById(R.id.divisionLabel);
        ArrayAdapter<CharSequence> divAdapter = ArrayAdapter.createFromResource(this, R.array.divisions, android.R.layout.simple_spinner_item); // Create an ArrayAdapter using the string array and a default spinner layout
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Specify the layout to use when the list of choices appears
        division.setAdapter(divAdapter); // Apply the adapter to the spinner
        division.setSelection(divAdapter.getPosition("--"));

        teamQuery = (EditText) findViewById(R.id.teamQuery);
    }

    public void doSearch(View button) {
        HashMap terms = new HashMap();

        terms.put("competition", competition);

        String text = division.getSelectedItem().toString();
        if(text != null && !text.isEmpty() && text!="--") {
            terms.put("division", text);
        }

        text = category.getSelectedItem().toString();
        if(text != null && !text.isEmpty() && text!="--") {
            terms.put("category", text);
        }

        text = phaseLabel.getSelectedItem().toString();
        if(text != null && !text.isEmpty() && text!="--") {
            terms.put("phase", text);
        }

        text = teamQuery.getText().toString();
        if(text != null && !text.isEmpty() && text!="--") {
            terms.put("team", text);
        }

        if(terms.size() == 1) {
            Toast t = Toast.makeText(getApplicationContext(), "No has especificado ningún criterio", Toast.LENGTH_SHORT);
            t.show();
            return;
        }

        Intent day = new Intent(getApplicationContext(), DayActivity.class);
        day.putExtra("query", composeQuery(terms));
        startActivity(day);
    }

    private String composeQuery(HashMap search) {
        StringBuilder sb = new StringBuilder("");
        Set<String> terms = search.keySet();

        for(String t : terms) {
            String value = (String) search.get(t);
            if (value.equals("--"))
                continue;

            if(sb.length()>0) {
                sb.append(" AND ");
            }

            if(t.equals("team")) {
                sb.append(String.format("(team1:\"%s\" OR team2:\"%s\")", value, value));
            } else {
                sb.append(String.format("%s:%s", t, value));
            }
        }

        Log.d("SEARCH", sb.toString());
        return sb.toString().replace(" ", "%20");
    }


}
