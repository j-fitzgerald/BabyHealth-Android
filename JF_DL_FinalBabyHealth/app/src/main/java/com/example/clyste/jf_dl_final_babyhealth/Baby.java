package com.example.clyste.jf_dl_final_babyhealth;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Clyste on 4/19/2018.
 */

public class Baby {

    private String firstName;
    private String lastName;
    private String gender;
    private Date birthday;


    public Baby(){
        // required empty constructor
    }

    public Baby(String firstName, String lastName, String gender, Date birthday){
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthday = birthday;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFormattedBirthday() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        return dateFormat.format(birthday);
    }

    public Date getBirthday(){
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String toString(){
        return(this.firstName + "\t" + this.lastName);
    }
}
