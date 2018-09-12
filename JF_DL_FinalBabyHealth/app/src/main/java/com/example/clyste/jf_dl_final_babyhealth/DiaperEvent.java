package com.example.clyste.jf_dl_final_babyhealth;

import java.util.ArrayList;

/**
 * Created by Clyste on 4/26/2018.
 */

public class DiaperEvent extends BabyEvents {

    public boolean pee;
    public boolean poop;
    public ArrayList<String> colors;

    public DiaperEvent(){
        colors = new ArrayList();
    }

    public boolean isPee() {
        return pee;
    }

    public void setPee(boolean pee) {
        this.pee = pee;
    }

    public boolean isPoop() {
        return poop;
    }

    public void setPoop(boolean poop) {
        this.poop = poop;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    public void setColors(ArrayList<String> colors) {
        this.colors = colors;
    }

    public void addColor(String color){
        this.colors.add(color);
    }

    public void removeColor(String color){
        try{
            this.colors.remove(color);
        }
        catch(Exception e){
            return;
        }
    }


}
