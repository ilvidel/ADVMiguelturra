package org.advmiguelturra;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by nacho on 30/06/14.
 */
public class Equipo implements Comparable, Serializable {

    private String nombre;
    private int jugados, ganados, perdidos, setsF, setsC, puntosF, puntosC, total;

    public Equipo(String nombre) {
        this.nombre = nombre;
    }

    public void ganar(int afavor, int encontra, int setsPerdidos){
        jugados++;
        ganados++;

        setsF+=2;
        setsC += setsPerdidos;

        puntosF+=afavor;
        puntosC+=encontra;

        total += 2;
    }

    public void perder(int afavor, int encontra, int setsGanados) {
        jugados++;
        perdidos++;

        setsF +=  setsGanados;
        setsC += 2;

        puntosF += afavor;
        puntosC += encontra;

        total += 1;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(java.lang.String nombre) {
        this.nombre = nombre;
    }

    public int getJugados() {
        return jugados;
    }

    public void setJugados(int jugados) {
        this.jugados = jugados;
    }

    public int getGanados() {
        return ganados;
    }

    public void setGanados(int ganados) {
        this.ganados = ganados;
    }

    public int getPerdidos() {
        return perdidos;
    }

    public void setPerdidos(int perdidos) {
        this.perdidos = perdidos;
    }

    public int getSetsF() {
        return setsF;
    }

    public void setSetsF(int setsF) {
        this.setsF = setsF;
    }

    public int getSetsC() {
        return setsC;
    }

    public void setSetsC(int setsC) {
        this.setsC = setsC;
    }

    public int getPuntosF() {
        return puntosF;
    }

    public void setPuntosF(int puntosF) {
        this.puntosF = puntosF;
    }

    public int getPuntosC() {
        return puntosC;
    }

    public void setPuntosC(int puntosC) {
        this.puntosC = puntosC;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public double cocienteSets() {
        return (double)setsF/(double)setsC;
    }

    public double cocientePuntos() {
        return (double)puntosF/(double) puntosC;
    }

    @Override
    public int compareTo(Object o) {
        Equipo other = (Equipo) o;
        if(! (o instanceof Equipo)) return 0;

        if(this.total == other.total) {
            //then compare sets
            if(this.cocienteSets() == other.cocienteSets()) {
                //then compare points
                if(this.cocientePuntos()==other.cocientePuntos()) {
                    //TODO
                    Log.e("EQUIPO", "No hay criterio válido para ordenar!!");
                    return 0;
                }

                return this.cocientePuntos() < other.cocientePuntos()?1:-1;
            } else {
                return this.cocienteSets()<other.cocienteSets()?1:-1;
            }
        } else {
            return this.total<other.total?1:-1;
        }
    }

}
