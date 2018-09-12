package com.example.clyste.jf_dl_final_babyhealth;

/**
 * Created by Clyste on 5/10/2018.
 */
public class GridCell {

    private final String name;
    private final int imageResource;


    public GridCell(String name, int image) {
        this.name = name;
        this.imageResource = image;

    }
    public String getName() {
        return name;
    }

    public int getImage() {
        return imageResource;
    }
}