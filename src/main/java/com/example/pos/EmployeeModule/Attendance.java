package com.example.pos.EmployeeModule;

public class Attendance {


    private String date;
    private String day;
    private String timeIn;
    private String timeOut;
    private String workedTime;

    public Attendance(String date,String day, String timeIn, String timeOut, String workedTime) {
        this.date = date;
        this.day = day;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.workedTime = workedTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(String timeIn) {
        this.timeIn = timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

    public String getWorkedTime() {
        return workedTime;
    }

    public void setWorkedTime(String workedTime) {
        this.workedTime = workedTime;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
