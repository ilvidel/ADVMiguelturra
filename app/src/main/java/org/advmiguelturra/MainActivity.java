package org.advmiguelturra;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;


public class MainActivity extends Activity {

    private final String PROJECT_NUMBER = "251066530747";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hide or show some buttons, for admin and testing purposes
        LinearLayout adminbuttons = (LinearLayout) findViewById(R.id.adminbuttons);

        if(Administrator.ADMIN_MODE) {
            adminbuttons.setVisibility(View.VISIBLE);
        } else {
            adminbuttons.setVisibility(View.GONE);
        }

        // obtain version name and display it on the main screen
        TextView ver = (TextView) findViewById(R.id.versionText);
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            ver.setText(String.format("version %s", pInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            ver.setText("");
        }

        //subscribe to the apps topic(s)
        FirebaseMessaging.getInstance().subscribeToTopic("ADVMiguelturra");
        Log.i("ADV", "Subscribed to ADVMiguelturra topic");
    }

    /**
     * Set a repeating alarm to check for new notifications on the DB and show them, if necessary
     */
//    private void setAlarm() {
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//
//        Intent intent = new Intent(getApplicationContext(), NotificationPublisher.class);
//        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
//        calendar.add(Calendar.SECOND, 3);
//        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), alarmIntent); //fire an alarm to check for notifications when the app starts
//
//        alarmManager.cancel(alarmIntent);
//
//        // set another alarm to check for notifications, everyday between 15:00 and 16:00
//        long delay = AlarmManager.INTERVAL_DAY/4;
//        Log.d("ADV MAIN",
//                String.format("Alarm set at %s to repeat every %d hours",
//                        calendar.getTime().toString(),
//                        delay/3600000)); //conversion to hours
//        alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), delay, alarmIntent);
//    }

    public void showCalendar(View v) {
        Intent i = new Intent(this, CompetitionChooserActivity.class);
        startActivity(i);
    }

//    public void populateDatabase(View v) {
//        CloudantTask cloudant;
//
//        for(int i=0; i<5; i++) {
//            Game game = Game.random();
//            cloudant = new CloudantTask(getApplicationContext());
//            cloudant.execute(CloudantTask.Action.NEW, game);
//            String response = null;
//            Object obj = null;
//
//            try {
//                obj = cloudant.get();
//                response = (String) obj;
//            } catch (InterruptedException e) {
//                Log.e("CLOUDANT", "Operation interrupted: " + e);
//            } catch (ExecutionException e) {
//                Log.e("CLOUDANT", "Excecution exception: " + e);
//            } catch (ClassCastException e) { // la respuesta no es lo que se esperaba (error)
//                Log.w("COMPETITIONS", obj.toString());
//                Toast t = Toast.makeText(getApplicationContext(), "No se ha encontrado una conexión válida", Toast.LENGTH_LONG);
//                t.show();
//            }
//
//            Log.d("POPULATEDB", response);
//            if(response==null || response.toLowerCase().contains("error")) {
//                break;
//            }
//        }
//    }

    public void showQualifications(View v) {
        if(!Administrator.ADMIN_MODE)
            openWebPage("http://www.advmiguelturra.org/vplaya/torneo-clasificacion.html");
        else {
            Intent ranking = new Intent(getApplicationContext(), RankingActivity.class);
            ranking.putExtra("category", "ALEVIN");
            startActivity(ranking);
        }
    }

    public void addGame(View v) {
        Intent gameAct = new Intent(this, GameActivity.class);
        Bundle b = new Bundle();
        b.putBoolean("adding", true);
        gameAct.putExtras(b);
        startActivity(gameAct);
    }

    private void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void showTeams(View v) {
        Intent teams = new Intent(this, TeamsActivity.class);
        startActivity(teams);
    }

    public void createNotification(View v) {
        Intent intent = new Intent(getApplicationContext(), NotificationsActivity.class);
        startActivity(intent);
    }

    public void viewPictures(View v) {
        openWebPage("https://www.flickr.com/photos/advmiguelturra/albums");
    }
}
