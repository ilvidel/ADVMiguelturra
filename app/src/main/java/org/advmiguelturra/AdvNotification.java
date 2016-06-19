package org.advmiguelturra;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class AdvNotification {
    private String extra;
    private String title, message, subtext;
    private Calendar expiration;
    private String id;
    private String destination;

    public enum Topic {
        GENERAL,
        LIVE_SCORE;
    }

    private HashMap<Topic, String> topics = new HashMap<Topic, String>(){{
        put(Topic.GENERAL, "/topics/ADVMiguelturra");
        put(Topic.LIVE_SCORE, "");
    }};

    public AdvNotification(String title, String message, Topic topic) {
        this.title = title;
        this.message = message;
        setDestination(topic);
        subtext = "";
        extra = "";
        expiration = Calendar.getInstance();
        expiration.add(Calendar.HOUR, 72);
    }

    public AdvNotification(JSONObject notif) throws JSONException {
        if(notif.has("_id"))
            id = notif.getString("_id");
        else
            id = notif.getString("id");
        title = notif.getString("title");
        message = notif.getString("message");
        subtext = notif.getString("subtext");
        setDestination(Topic.GENERAL);
        String[] date = notif.getString("expiration").split("-");

        expiration = Calendar.getInstance();
        expiration.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date[0]));
        expiration.set(Calendar.MONTH, Integer.parseInt(date[1]) - 1);
        expiration.set(Calendar.YEAR, Integer.parseInt(date[2]));

        // extra information, to open a certain activity
        if(notif.has("extra")) {
            this.extra = notif.getString("extra");
        }
    }

    public Calendar getExpiration() {
        return expiration;
    }

    public String getExpirationAsString() {
        return String.format(Locale.getDefault(),
                "%02d-%02d-%04d",
                expiration.get(Calendar.DAY_OF_MONTH),
                expiration.get(Calendar.MONTH) + 1,
                expiration.get(Calendar.YEAR));
    }

    public void setExpiration(Calendar date) {
        this.expiration = date;
    }

    public void setExpiration(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        expiration.setTime(sdf.parse(date));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNumber() {
        if(id.startsWith("notif")) {
            return Integer.parseInt(id.substring(5));
        } else {
            return 0;
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message==null?"":message;
    }

    public String getSubtext() {
        return subtext;
    }

    public void setSubtext(String subtext) {
        this.subtext = subtext==null?"":subtext;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title==null?"":title;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra==null?"":extra;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(Topic topic) {
        this.destination = topics.get(topic);
    }

    @Override
    public String toString() {
        return getId() + " " + title + "\n" +
                message + "\n" +
                subtext + "\n" +
                expiration + "\n";
    }

    public JSONObject toJson() {
        JSONObject data = new JSONObject();
        JSONObject json = new JSONObject();

        try {
            data.put("message", getMessage());
            data.put("title", getTitle());
            data.put("subtext", getSubtext());

            json.put("to", getDestination());
            json.put("priority", "normal");
            json.put("time_to_live", getTTL());
            json.put("data", data);
            Log.d("ADV_NOTIFICATION", json.toString(4));
        } catch (JSONException e) {
            Log.e("ADV", e.toString());
        }

        return json;
    }

    public int getTTL() {
        Log.d("EXPIR", getExpirationAsString());
        long ttl = getExpiration().getTimeInMillis() - Calendar.getInstance().getTimeInMillis();

        if (ttl < 0) { //sending a notification for a event in the past
            ttl = 3 * 86400;
        } else {
            ttl /= 1000; //milliseconds to seconds
        }
        return (int) ttl;
    }
}
