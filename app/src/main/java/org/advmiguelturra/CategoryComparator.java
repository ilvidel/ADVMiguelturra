package org.advmiguelturra;

import java.util.Comparator;

/**
 * Comparator class for Categories.
 */
public class CategoryComparator implements Comparator<Game> {

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
        int comp = left.getCategory().compareTo(right.getCategory());

        if(comp==0) { //same category
            comp = left.getDivision().compareTo(right.getDivision());
            if(comp==0) { //same division
                DateComparator dc = new DateComparator();
//                comp = left.getDate().compareTo(right.getDate());
                comp = dc.compare(left, right);
            }
        }

        return comp;
    }
}
