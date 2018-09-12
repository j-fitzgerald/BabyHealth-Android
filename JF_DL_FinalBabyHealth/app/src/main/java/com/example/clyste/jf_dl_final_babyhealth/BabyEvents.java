package com.example.clyste.jf_dl_final_babyhealth;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Clyste on 4/24/2018.
 */

public class BabyEvents {

    private long startTime;
    private long endTime;
    private String recordedBy;
    private Date date;
    private String formattedDate;
    private Calendar calDate;
    private static SimpleDateFormat dateFormatTime = new SimpleDateFormat("HH:mm", Locale.US);
    private static SimpleDateFormat dateFormatDate = new SimpleDateFormat("MM-dd-yyyy", Locale.US);

    public BabyEvents(){

    }

    public String getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(String recordedBy) {
        this.recordedBy = recordedBy;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime() {
        this.startTime = new Date().getTime();
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime() {
        this.endTime = new Date().getTime();
    }

    public long getDuration(){
        return endTime - startTime;
    }

    public Date getDate() {
        return date;
    }

    public void setDate() {
        //this.date = calDate.getTime();
        this.date = new Date();
        this.formattedDate = dateFormatDate.format(this.date);
    }

    public String getFormattedDate(){
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate){
        this.formattedDate = formattedDate;
    }

    public Calendar getCalDate() {
        return calDate;
    }

    public void setCalDate(Calendar calDate) {
        this.calDate = calDate;
    }

}
