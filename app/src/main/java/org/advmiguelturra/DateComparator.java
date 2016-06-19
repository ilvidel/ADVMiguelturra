package org.advmiguelturra;

import java.util.Calendar;
import java.util.Comparator;

/**
 * Comparator class for dates.
 *
 * This is necesary because we treat games played from midnight to 8am to be in the same day.
 * This is mainly for the beach volleyball tournament; where the "day" starts about 18:00 and
 * ends about 2:00 am
 */
public class DateComparator implements Comparator<Game> {
    /**
     * Compares the two specified objects to determine their relative ordering. The ordering
     * implied by the return value of this method for all possible pairs of
     * {@code (left, right)} should form an <i>equivalence relation</i>.
     * This means that
     * <ul>
     * <li>{@code compare(a,a)} returns zero for all {@code a}</li>
     * <li>the sign of {@code compare(a,b)} must be the opposite of the sign of {@code
     * compare(b,a)} for all pairs of (a,b)</li>
     * <li>From {@code compare(a,b) > 0} and {@code compare(b,c) > 0} it must
     * follow {@code compare(a,c) > 0} for all possible combinations of {@code
     * (a,b,c)}</li>
     * </ul>
     *
     * @param left an {@code Object}.
     * @param right a second {@code Object} to compare with {@code left}.
     * @return an integer < 0 if {@code left} is less than {@code right}, 0 if they are
     * equal, and > 0 if {@code left} is greater than {@code right}.
     * @throws ClassCastException if objects are not of the correct type.
     */
    @Override
    public int compare(Game left, Game right) {
        int comp = left.getDate().compareTo(right.getDate());

        if(comp==0) { //same date, same time
            comp = left.getCategory().compareTo(right.getCategory());
            if(comp==0) {
                comp = left.getDivision().compareTo(right.getDivision());
            }
        }

        Calendar a = left.getDate();
        Calendar b = right.getDate();

        if(a.get(Calendar.DAY_OF_MONTH) == b.get(Calendar.DAY_OF_MONTH) && a.get(Calendar.MONTH)==b.get(Calendar.MONTH) && a.get(Calendar.YEAR)==b.get(Calendar.YEAR)) {
            //mismo día (jornada). Hacer que el que se juega despues de medianoche sea "mayor"

            if(a.get(Calendar.HOUR_OF_DAY) <8 && b.get(Calendar.HOUR_OF_DAY)>8)
                return 1;
            else if(a.get(Calendar.HOUR_OF_DAY)>8 && b.get(Calendar.HOUR_OF_DAY)<8)
                return -1;
        }

        return comp;
    }

}
