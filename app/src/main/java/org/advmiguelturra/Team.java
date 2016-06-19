package org.advmiguelturra;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by nacho on 30/06/14.
 */
public class Team implements Comparable, Serializable {

    private String name;
    private int played, won, lost, setsWon, setsLost, pointsWon, pointsLost, total;

    public Team(String name) {
        this.name = name;
    }

    public void ganar(int scored, int lost, int lostSets){
        played++;
        won++;

        setsWon +=2;
        setsLost += lostSets;

        pointsWon += scored;
        pointsLost += lost;

        total += 2;
    }

    public void perder(int scored, int lost, int wonSets) {
        played++;
        this.lost++;

        setsWon +=  wonSets;
        setsLost += 2;

        pointsWon += scored;
        pointsLost += lost;

        total += 1;
    }

    public String getName() {
        return name;
    }

    public void setName(java.lang.String name) {
        this.name = name;
    }

    public int getPlayed() {
        return played;
    }

    public void setPlayed(int played) {
        this.played = played;
    }

    public int getWon() {
        return won;
    }

    public void setWon(int won) {
        this.won = won;
    }

    public int getLost() {
        return lost;
    }

    public void setLost(int lost) {
        this.lost = lost;
    }

    public int getSetsWon() {
        return setsWon;
    }

    public void setSetsWon(int setsWon) {
        this.setsWon = setsWon;
    }

    public int getSetsLost() {
        return setsLost;
    }

    public void setSetsLost(int setsLost) {
        this.setsLost = setsLost;
    }

    public int getPointsWon() {
        return pointsWon;
    }

    public void setPointsWon(int pointsWon) {
        this.pointsWon = pointsWon;
    }

    public int getPointsLost() {
        return pointsLost;
    }

    public void setPointsLost(int pointsLost) {
        this.pointsLost = pointsLost;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public double setsQuotient() {
        return (double) setsWon /(double) setsLost;
    }

    public double pointsQuotient() {
        return (double) pointsWon /(double) pointsLost;
    }

    @Override
    public int compareTo(Object o) {
        Team other = (Team) o;
        if(! (o instanceof Team)) return 0;

        if(this.total == other.total) {
            //then compare sets
            if(this.setsQuotient() == other.setsQuotient()) {
                //then compare points
                if(this.pointsQuotient()==other.pointsQuotient()) {
                    //TODO
                    Log.e("EQUIPO", "No hay criterio válido para ordenar!!");
                    return 0;
                }

                return this.pointsQuotient() < other.pointsQuotient()?1:-1;
            } else {
                return this.setsQuotient()<other.setsQuotient()?1:-1;
            }
        } else {
            return this.total<other.total?1:-1;
        }
    }

}
