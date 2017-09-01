package adria.dia6;

import java.util.ArrayList;

/**
 * Created by inlab on 03/07/2017.
 */

public class Persona {
    public String name;
    public int puntuation;
    public ArrayList<String> votadors;


    public Persona(){}

    public Persona(String name, int puntuation, ArrayList<String> votadors) {
        this.name = name;
        this.puntuation = puntuation;
        this.votadors = votadors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean searchVotadors(String votador) {
        int i = 0;
        while (i < votadors.size()-1) {
            if ((votadors).get(i).equals(votador)) {
             return true;
            }
            ++i;
        }
        return false;
    }
}

//problemes al fer que la app la facin servir olts ordinadors sobretot en les imatges que noc arregeun i en el puto array de votadors jdoer




