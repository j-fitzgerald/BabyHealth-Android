package com.example.clyste.jf_dl_final_babyhealth;



/**
 * Created by Clyste on 4/24/2018.
 */

public class    FeedingBabyEvents extends BabyEvents {
    public int volume;
    public boolean spitUp;
    public String units;

    public FeedingBabyEvents(){

    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public boolean isSpitUp() {
        return spitUp;
    }

    public void setSpitUp(boolean spitUp) {
        this.spitUp = spitUp;
    }

    public void setUnits(String units){
        this.units = units;
    }

    public String getUnits(){
        return units;
    }
}
