package org.advmiguelturra;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by nacho on 14/09/15.
 */
public class Competition implements Comparable, Serializable {

    public enum Type {
        RFEVB,
        FVCM,
        JCCM,
        LEAGUE,
        BEACH,
        TROPHY;

        public static Type getValue(String type) {
            switch (type) {
                case "RFEVB":
                    return RFEVB;
                case "FVCM":
                    return FVCM;
                case "BEACH":
                    return BEACH;
                case "TROPHY":
                    return TROPHY;
                case "JCCM":
                    return JCCM;
                default:
                    return LEAGUE;
            }
        }
    }

    /**
     * String to identify this Competition in the Cloudant DB
     */
    private String id;

    /**
     * The name of this {@code Competition}
     */
    private String name;

    /**
     * The starting date of competition.
     */
    private Calendar startDate;

    /**
     * The last day of competition.
     */
    private Calendar endDate;

    /**
     * The type of this Competition
     */
    private Type type;

    public Competition(String name) {
        this.name = name;
        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
    }

    public Competition(JSONObject object) throws JSONException {
        setId(object.getString("_id"));
        setName(object.getString("name"));
        setType(Type.getValue(object.getString("type")));

        try {
            setStartDate(object.getString("start"));
            setEndDate(object.getString("end"));
        } catch (ParseException e) {
            Log.e("COMPETITION", e.toString());
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private void setStartDate(String date) throws ParseException {
        if(startDate==null) startDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        startDate.setTime(sdf.parse(date));
    }

    private void setEndDate(String date) throws ParseException {
        if(endDate==null) endDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        endDate.setTime(sdf.parse(date));

    }

    public Calendar getEndDate() {
        return endDate;
    }

    public String getEndDateAsString() {
        return String.format(Locale.getDefault(), "%02d %s %04d",
                endDate.get(Calendar.DAY_OF_MONTH),
                endDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                endDate.get(Calendar.YEAR));
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name == null)
            name = "";
        this.name = name;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public String getStartDateAsString() {
        return String.format(Locale.getDefault(), "%02d %s %04d",
                startDate.get(Calendar.DAY_OF_MONTH),
                startDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                startDate.get(Calendar.YEAR));
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getDuration() {
        String date = "";
        DateTime start = new DateTime(startDate);
        DateTime end = new DateTime(endDate);

        if(Days.daysBetween(start, end).getDays() == 0) { //one-day tournament
            date = String.format(Locale.getDefault(), "%02d %s '%02d",
                    endDate.get(Calendar.DAY_OF_MONTH),
                    endDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                    endDate.get(Calendar.YEAR)    );
        }
        else if(Days.daysBetween(start, end).getDays() < 30) { //several days competition
            if(startDate.get(Calendar.MONTH) == endDate.get(Calendar.MONTH)) { //same month
                date = String.format(Locale.getDefault(), "%02d-%02d %s %02d",
                        startDate.get(Calendar.DAY_OF_MONTH),
                        endDate.get(Calendar.DAY_OF_MONTH),
                        endDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                        endDate.get(Calendar.YEAR));
            } else { //across months
                date = String.format(Locale.getDefault(), "%02d %s-%02d %s %02d",
                        startDate.get(Calendar.DAY_OF_MONTH),
                        startDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                        endDate.get(Calendar.DAY_OF_MONTH),
                        endDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                        endDate.get(Calendar.YEAR));
            }
        } else if (Months.monthsBetween(start, end).getMonths() > 1) { //several months (league)
            date = String.format(Locale.getDefault(), "%s %02d - %s %02d",
                    startDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                    startDate.get(Calendar.YEAR),
                    endDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                    endDate.get(Calendar.YEAR)
            );
        }

        return date;
    }

    @Override
    public int compareTo(Object o) {
        Competition other;
        if(o instanceof Competition) other = (Competition) o;
        else return -1;

        if(this.endDate.after(other.endDate)) return -1;
        else if(this.endDate.before(other.endDate)) return 1;
        return 0;
    }


}
