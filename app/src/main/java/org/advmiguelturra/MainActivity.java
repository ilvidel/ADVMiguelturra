package org.advmiguelturra;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;


public class MainActivity extends Activity {

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


    public void showCalendar(View v) {
        Intent i = new Intent(this, CompetitionChooserActivity.class);
        startActivity(i);
    }

    public void showQualifications(View v) {
        if(!Administrator.ADMIN_MODE)
            openWebPage("http://www.advmiguelturra.org/vplaya/torneo/clasificacion.html");
        else {
            openWebPage("http://www.advmiguelturra.org/cgi-bin/clasificacion.py");
//            Intent ranking = new Intent(getApplicationContext(), RankingActivity.class);
//            ranking.putExtra("category", "AFICIONADOS");
//            startActivity(ranking);
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
