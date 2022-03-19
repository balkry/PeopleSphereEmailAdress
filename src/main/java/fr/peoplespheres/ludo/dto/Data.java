package fr.peoplespheres.ludo.dto;

import java.util.ArrayList;
import java.util.List;

public class Data {
    List<Dat> data = new ArrayList<Dat>();

    public List<Dat> getData() {
        return data;
    }

    public void setData(List<Dat> dats) {
        this.data = dats;
    }
}
