package org.advmiguelturra;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;
import java.util.Timer;

public class QuestionActivity extends ActionBarActivity {

    private String question;
    private String options[];
    private int correct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        Intent i = getIntent();
        question = i.getStringExtra("question");
        options = i.getStringArrayExtra("options"); // options[0] is always the correct one

        scrambleOptions();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_question, menu);
//        return true;
//    }
//
//    @Override
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

    /**
     * Assign each answer to a button, scrambling them so that the correct answer is in a random button every time
     */
    void scrambleOptions(){
        byte n = (byte) (Calendar.getInstance().get(Calendar.MILLISECOND) % 4);

        Button answer1 = (Button) findViewById(R.id.trivia_answer1);
        Button answer2 = (Button) findViewById(R.id.trivia_answer2);
        Button answer3 = (Button) findViewById(R.id.trivia_answer3);
        Button answer4 = (Button) findViewById(R.id.trivia_answer4);

        Button aux[] = {answer1, answer2, answer3, answer4};
        correct = aux[n].getId();

        for(int i=0; i<aux.length; i++) {
            aux[(n+i)%aux.length].setText(options[i]);
        }
    }

    public void onAnswer(View v) {
        if(v.getId() == correct) {
            v.setBackgroundColor(getResources().getColor(R.color.correct));
        } else {
            v.setBackgroundColor(getResources().getColor(R.color.wrong));
        }
    }

}
