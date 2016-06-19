package org.advmiguelturra;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

public class NotificationsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initExpiryText();
    }

    private void initExpiryText() {
        TextView tv = (TextView) findViewById(R.id.notif_expire);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 2);

        String expiry = String.format(Locale.getDefault(), "%02d-%02d-%04d",
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.YEAR));

        tv.setText(expiry);
    }

    public void createNotification(View v) {
        String title = ((TextView) findViewById(R.id.notif_title)).getText().toString();
        String message = ((TextView) findViewById(R.id.notif_msg)).getText().toString();
        String subtext = ((TextView) findViewById(R.id.notif_subtext)).getText().toString();
        String expiry = ((TextView) findViewById(R.id.notif_expire)).getText().toString();
        String extra = ((TextView) findViewById(R.id.notif_extra)).getText().toString();

        AdvNotification notification = new AdvNotification(title, message, AdvNotification.Topic.GENERAL);
        notification.setSubtext(subtext);
        notification.setExtra(extra);

        try {
            notification.setExpiration(expiry);
        } catch (ParseException e) {
            Toast t = Toast.makeText(getApplicationContext(), "La fecha no es válida. Usa el formato dd-MM-yyy", Toast.LENGTH_LONG);
            t.show();
            return;
        }

        CloudantTask cloudant = new CloudantTask(getApplicationContext());
        cloudant.execute(CloudantTask.Action.ADD_NOTIFICATION, notification);
        finish();
    }
}
