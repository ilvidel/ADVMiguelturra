package org.advmiguelturra;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class GameActivity extends Activity {

    private EditText team1, team2,
            scoreA1, scoreA2, scoreA3, scoreA4, scoreA5,
            scoreB1, scoreB2, scoreB3, scoreB4, scoreB5,
            dateLabel, poolLabel, compLabel;
    private Spinner category, division, phaseLabel;
    private TextView scoreA, scoreB;
    private Game game;
    private EditText timeClock;
    private boolean adding;


    View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if(hasFocus) {
                EditText edit = (EditText) view;
                if("0".equals(edit.getText().toString())) {
                    edit.setText("");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //hide commit button
        if(!Administrator.ADMIN_MODE) {
            findViewById(R.id.commitGameButton).setVisibility(View.GONE);
            findViewById(R.id.notificationRadioGroup).setVisibility(View.GONE);
            findViewById(R.id.textView5).setVisibility(View.GONE);
        }


        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        adding = b.getBoolean("adding", false);
        if(adding)
            game = new Game();
        else
            game = (Game) b.getSerializable("game");
        initGUI();
    }

    public void initGUI() {
        compLabel = (EditText) findViewById(R.id.competitionLabel);
        compLabel.setText(game.getCompetition());
        int bg = game.getCompetition().hashCode();
        bg |= 0xFF000000; //set alpha to max
        compLabel.setBackgroundColor(bg);

        team1 = (EditText) findViewById(R.id.localTeamLabel);
        team1.setText(game.getTeamA());

        team2 = (EditText) findViewById(R.id.visitorTeamLabel);
        team2.setText(game.getTeamB());

        scoreA = (TextView) findViewById(R.id.localSets);
        scoreA.setText(String.valueOf(game.getWonA()));
        scoreA.setOnFocusChangeListener(focusListener);

        scoreB = (TextView) findViewById(R.id.visitorSets);
        scoreB.setText(String.valueOf(game.getWonB()));
        scoreB.setOnFocusChangeListener(focusListener);

        scoreA1 = (EditText) findViewById(R.id.localSet1);
        scoreA1.setText(String.valueOf(game.getSetsA()[0]));
        scoreA1.setOnFocusChangeListener(focusListener);

        scoreA2 = (EditText) findViewById(R.id.localSet2);
        scoreA2.setText(String.valueOf(game.getSetsA()[1]));
        scoreA2.setOnFocusChangeListener(focusListener);

        scoreA3 = (EditText) findViewById(R.id.localSet3);
        scoreA3.setText(String.valueOf(game.getSetsA()[2]));
        scoreA3.setOnFocusChangeListener(focusListener);

        scoreA4 = (EditText) findViewById(R.id.localSet4);
        scoreA4.setText(String.valueOf(game.getSetsA()[3]));
        scoreA4.setOnFocusChangeListener(focusListener);

        scoreA5 = (EditText) findViewById(R.id.localSet5);
        scoreA5.setText(String.valueOf(game.getSetsA()[4]));
        scoreA5.setOnFocusChangeListener(focusListener);

        scoreB1 = (EditText) findViewById(R.id.visitorSet1);
        scoreB1.setText(String.valueOf(game.getSetsB()[0]));
        scoreB1.setOnFocusChangeListener(focusListener);

        scoreB2 = (EditText) findViewById(R.id.visitorSet2);
        scoreB2.setText(String.valueOf(game.getSetsB()[1]));
        scoreB2.setOnFocusChangeListener(focusListener);

        scoreB3 = (EditText) findViewById(R.id.visitorSet3);
        scoreB3.setText(String.valueOf(game.getSetsB()[2]));
        scoreB3.setOnFocusChangeListener(focusListener);

        scoreB4 = (EditText) findViewById(R.id.visitorSet4);
        scoreB4.setText(String.valueOf(game.getSetsB()[3]));
        scoreB4.setOnFocusChangeListener(focusListener);

        scoreB5 = (EditText) findViewById(R.id.visitorSet5);
        scoreB5.setText(String.valueOf(game.getSetsB()[4]));
        scoreB5.setOnFocusChangeListener(focusListener);

        poolLabel = (EditText) findViewById(R.id.poolLabel);
        poolLabel.setText(String.format("Grupo %s", game.getPool()=='-'? "único" : game.getPool()));

        phaseLabel = (Spinner) findViewById(R.id.phaseLabel);
        ArrayAdapter<CharSequence> phaseAdapter = ArrayAdapter.createFromResource(this, R.array.phases, android.R.layout.simple_spinner_item); // Create an ArrayAdapter using the string array and a default spinner layout
        phaseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Specify the layout to use when the list of choices appears
        phaseLabel.setAdapter(phaseAdapter); // Apply the adapter to the spinner
        phaseLabel.setSelection(phaseAdapter.getPosition(game.getPhase().toString()));

        category= (Spinner) findViewById(R.id.categoryLabel);
        ArrayAdapter<CharSequence> catAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item); // Create an ArrayAdapter using the string array and a default spinner layout
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Specify the layout to use when the list of choices appears
        category.setAdapter(catAdapter); // Apply the adapter to the spinner
        category.setSelection(catAdapter.getPosition(game.getCategory().toString()));

        division = (Spinner) findViewById(R.id.divisionLabel);
        ArrayAdapter<CharSequence> divAdapter = ArrayAdapter.createFromResource(this, R.array.divisions, android.R.layout.simple_spinner_item); // Create an ArrayAdapter using the string array and a default spinner layout
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Specify the layout to use when the list of choices appears
        division.setAdapter(divAdapter); // Apply the adapter to the spinner
        division.setSelection(divAdapter.getPosition(game.getDivision().toString()));

        timeClock = (EditText) findViewById(R.id.textClock);
        timeClock.setText(String.format(Locale.getDefault(),
                "%02d:%02d",
                game.getDate().get(Calendar.HOUR_OF_DAY),
                game.getDate().get(Calendar.MINUTE)));

        dateLabel = (EditText) findViewById(R.id.dateLabel);
        dateLabel.setText(game.getDateAsString());

        timeClock.setInputType(InputType.TYPE_NULL);
        timeClock.setFocusable(false);
        dateLabel.setInputType(InputType.TYPE_NULL);
        dateLabel.setFocusable(false);

        if(Administrator.ADMIN_MODE) {
            team1.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
            team2.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
            scoreA1.setInputType(InputType.TYPE_CLASS_NUMBER);
            scoreA2.setInputType(InputType.TYPE_CLASS_NUMBER);
            scoreA3.setInputType(InputType.TYPE_CLASS_NUMBER);
            scoreA4.setInputType(InputType.TYPE_CLASS_NUMBER);
            scoreA5.setInputType(InputType.TYPE_CLASS_NUMBER);
            scoreB1.setInputType(InputType.TYPE_CLASS_NUMBER);
            scoreB2.setInputType(InputType.TYPE_CLASS_NUMBER);
            scoreB3.setInputType(InputType.TYPE_CLASS_NUMBER);
            scoreB4.setInputType(InputType.TYPE_CLASS_NUMBER);
            scoreB5.setInputType(InputType.TYPE_CLASS_NUMBER);
            poolLabel.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            compLabel.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        }
        else {
            compLabel.setInputType(InputType.TYPE_NULL);
            team1.setInputType(InputType.TYPE_NULL);
            team2.setInputType(InputType.TYPE_NULL);
            scoreA1.setInputType(InputType.TYPE_NULL);
            scoreA2.setInputType(InputType.TYPE_NULL);
            scoreA3.setInputType(InputType.TYPE_NULL);
            scoreA4.setInputType(InputType.TYPE_NULL);
            scoreA5.setInputType(InputType.TYPE_NULL);
            scoreB1.setInputType(InputType.TYPE_NULL);
            scoreB2.setInputType(InputType.TYPE_NULL);
            scoreB3.setInputType(InputType.TYPE_NULL);
            scoreB4.setInputType(InputType.TYPE_NULL);
            scoreB5.setInputType(InputType.TYPE_NULL);
            poolLabel.setInputType(InputType.TYPE_NULL);
            category.setEnabled(false);
            division.setEnabled(false);
            phaseLabel.setEnabled(false);
        }
    }

    public void commitGame(View view) {
        if (!Administrator.ADMIN_MODE)
            finish();

        Game game = createGame();
        if(game==null) {
            Toast t = Toast.makeText(getBaseContext(), "The game is null, aborting", Toast.LENGTH_SHORT);
            Log.w("GAME", "Could not commit game/modifications, the game is null");
            t.show();
            return;
        }

        CloudantTask cloudant = new CloudantTask(getApplicationContext());
        String response = null;
        Object obj = null;

        try {
            if (adding){
                cloudant.execute(CloudantTask.Action.NEW, game);
            }else {
                cloudant.execute(CloudantTask.Action.EDIT, game);
            }

            obj = cloudant.get();
            response = (String) obj;

        } catch (InterruptedException e) {
            Log.e("GAME", "Operation interrupted: " + e);
        } catch (ExecutionException e) {
            Log.e("GAME", e.toString());
        } catch (ClassCastException e) { // la respuesta no es lo que se esperaba (error)
            Log.w("COMPETITIONS", obj.toString());
            Toast t = Toast.makeText(getApplicationContext(), "No se ha encontrado una conexión válida", Toast.LENGTH_LONG);
            t.show();
            return;
        }

        RadioButton noneRadio = (RadioButton) findViewById(R.id.noneRadio);

        if(noneRadio.isChecked()) {
            Toast t = Toast.makeText(this, response, Toast.LENGTH_SHORT);
            t.show();
            finish();
        }

        // send a notification
        RadioButton nuevoRadio = (RadioButton) findViewById(R.id.newgameRadio);
        RadioButton dateRadio = (RadioButton) findViewById(R.id.dateRadio);
        RadioButton scoreRadio = (RadioButton) findViewById(R.id.scoreRadio);
        String title;
        String subtext = String.format("%s - %s", game.getDateAsString(), game.getTime());
        String message = String.format("%s vs %s", game.getTeamA(), game.getTeamB());

        if(nuevoRadio.isChecked()) {
            title = "Nuevo partido";
        }
        else if(dateRadio.isChecked()) {
            title = "Cambio de fecha/hora";
        }
        else {
            title = "Resultado actualizado";
            message = String.format(Locale.getDefault(),
                    "%s %d - %d %s",
                    game.getTeamA(), game.getWonA(),
                    game.getWonB(), game.getTeamB()
            );
            subtext = "";
        }

        AdvNotification notif = new AdvNotification(title, message, AdvNotification.Topic.GENERAL);
        notif.setSubtext(subtext);
        notif.setExtra(game.getId());

        try {
            notif.setExpiration(game.getCanonicalDate());
        } catch (ParseException e) {
            //no deberia ocurrir, puesto que getCanonicalDate utiliza el formato correcto
            Toast t = Toast.makeText(getApplicationContext(), "La fecha de caducidad no es válida. Use el formato dd-MM-yyy", Toast.LENGTH_LONG);
            t.show();
            return;
        }

        Log.d("GAME", game.jsonToString());
        CloudantTask notifTask = new CloudantTask(getApplicationContext());
        notifTask.execute(CloudantTask.Action.ADD_NOTIFICATION, notif);
        finish();
    }


    private Game createGame() {
        String equipoa;
        String equipob;

        try {
            equipoa = team1.getText().toString();
            equipob = team2.getText().toString();
        } catch (NullPointerException e) {
            Toast t = Toast.makeText(this, "Falta el nombre de un equipo", Toast.LENGTH_LONG);
            t.show();
            return null;
        }

        if (equipoa.isEmpty() || equipob.isEmpty() || equipoa.equals("null") || equipob.equals("null")) {
            Toast t = Toast.makeText(this, "Falta el nombre de un equipo", Toast.LENGTH_LONG);
            t.show();
            return null;
        }

        if (compLabel.getText().toString().equals("Competición") || compLabel.getText().toString().isEmpty()) {
            compLabel.requestFocus();
            Toast t = Toast.makeText(this, "Falta el nombre de la competición", Toast.LENGTH_LONG);
            t.show();
            return null;
        }

        game.setCompetition(compLabel.getText().toString());
        game.setDate(dateLabel.getText().toString());
        game.setTime(timeClock.getText().toString());
        char pool = poolLabel.getText().toString().charAt(6);  //// TODO: 24/01/16 arreglar esto: cuando no tiene texto (grupo unico)
        game.setPool(pool=='ú'?'-':pool);
        game.setCategory(Category.valueOf(category.getSelectedItem().toString()));
        game.setDivision(Division.valueOf(division.getSelectedItem().toString()));
        game.setPhase(Phase.valueOf(phaseLabel.getSelectedItem().toString()));
        game.setTeamA(equipoa);
        game.setTeamB(equipob);

        int[] a = new int[5];
        int[] b = new int[5];
        a[0] = Integer.parseInt(scoreA1.getText().toString());
        a[1] = Integer.parseInt(scoreA2.getText().toString());
        a[2] = Integer.parseInt(scoreA3.getText().toString());
        a[3] = Integer.parseInt(scoreA4.getText().toString());
        a[4] = Integer.parseInt(scoreA5.getText().toString());

        b[0] = Integer.parseInt(scoreB1.getText().toString());
        b[1] = Integer.parseInt(scoreB2.getText().toString());
        b[2] = Integer.parseInt(scoreB3.getText().toString());
        b[3] = Integer.parseInt(scoreB4.getText().toString());
        b[4] = Integer.parseInt(scoreB5.getText().toString());
        game.setSetsA(a);
        game.setSetsB(b);

        return game;
    }

    public void showDatePickerDialog(View v) {
        if(!Administrator.ADMIN_MODE) return;

        DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                Log.d("DATE_PICKER", String.format("%02d/%02d/%04d", d, m, y));
                Calendar date = Calendar.getInstance();
                date.set(y, m, d);
                dateLabel.setText(String.format(Locale.getDefault(),
                        "%02d %s %04d",
                        date.get(Calendar.DAY_OF_MONTH),
                        date.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                        date.get(Calendar.YEAR)));
            }
        };

        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, callback, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    public void showTimePickerDialog(View v) {
        if(!Administrator.ADMIN_MODE) return;

        String[] t = ((EditText)v).getText().toString().split(":");
        int h = Integer.parseInt(t[0]);
        int m = Integer.parseInt(t[1]);

        TimePickerDialog.OnTimeSetListener callback = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                timeClock.setText(String.format(Locale.getDefault(),
                        "%02d:%02d", hour, min));
            }
        };

        TimePickerDialog d = new TimePickerDialog(this, callback, h, m, true);
        d.show();
    }

}
