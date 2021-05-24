package edu.uci.ics.fabflixmobile;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class Movie {
    private final String name;
    private final short year;
    final String director;
    final String sstars;
    final String sgenres;
    String id;

    public Movie(String name, short year, String director, JSONArray stars,
                 JSONArray genres, String id) throws JSONException {
        String sstars1;
        this.name = name;
        this.year = year;
        this.director = director;
        String sep = "";
        sstars1 = "";
        for (int i=0; i < stars.length(); i++){
            sstars1 += sep + stars.getJSONObject(i).getString("name");
            sep = ", ";
//            this.stars.add(stars.getJSONObject(i).getString("name"));
        }
        sep = "";
        String sgenres1 = "";
        this.sstars = sstars1;
        for (int i = 0; i < genres.length(); i++){
            sgenres1 += sep + genres.getJSONObject(i).getString("name");
            sep = ", ";
//            this.genres.add(genres.getJSONObject(i).getString("name"));
        }
        this.sgenres = sgenres1;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public short getYear() {
        return year;
    }

}