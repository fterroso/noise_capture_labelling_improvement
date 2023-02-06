package org.noise_planet.noisecapture.util;

import java.util.ArrayList;
import java.util.List;

/**
 * El objeto noise tiene las cosas que nos interesa. El tag, start, end, value y la lista.
 */
public class Noise {

    private String tag, value;
    private Long start, end;
    private List<Double> listDB;

    public Noise(String tag, Long start, Long end) {
        this.tag = tag;
        this.start = start;
        this.end = end;
        this.value = "";
        this.listDB = new ArrayList<>(); // Lista de decibelios.
    }

    public Noise(String tag, Long start) {
        this.tag = tag;
        this.start = start;
        this.end = 0L;
        this.value = "";
        this.listDB = new ArrayList<>();
    }

    public Noise() { }

    public void addValue(Double value) {
        listDB.add(Math.exp(value/10));
    }

    /**
     *Calcular la media de decibelios
     */
    public void calculateAverage() {
        double total = 0;
        for (int i = 0; i < listDB.size(); i++) {
            total += listDB.get(i);
        }
        this.value = String.valueOf(10*(Math.log(total/listDB.size()))); // Operación de la media de decibelios.
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public List<Double> getListDB() {
        return listDB;
    }

    public void setListDB(List<Double> listDB) {
        this.listDB = listDB;
    }

}
