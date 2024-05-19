package com.example.pos.UtilityClasses;

import javafx.scene.control.Alert;

import java.io.File;

public class Function {
    static String basePath = new File("").getAbsolutePath();
    static String dbFilePath = basePath + File.separator + "config" + File.separator + "POS.accdb";
    public static String databasePath = "jdbc:ucanaccess://" + dbFilePath ;
    public static String fxmlFileName;
    public static String title;

//    D:\Projects\POS Software\POS_FE - Increment 3.6.1\out\artifacts\POS_S1_jar\src\main\resources\com\example\pos\Images
    public static String deleteButtonImagePath = basePath + "\\src\\main\\resources\\com\\example\\pos\\Images\\TrashBlack.png";
    public static String editButtonImagePath = basePath + "\\src\\main\\resources\\com\\example\\pos\\Images\\EditBlack.png";
    public static String viewButtonImagePath = basePath + "\\src\\main\\resources\\com\\example\\pos\\Images\\ViewBlack.png";
    public static String localBackUpPath = basePath + "\\Backup\\POS_Backup.accdb";

    public static boolean isOBProdAdd = true;

    public static void alert(Alert.AlertType alertType, String alertTitle, String alertText){
        Alert alert = new Alert(alertType);
        alert.setTitle(alertTitle);
        alert.setContentText(alertText);
        alert.showAndWait();
    }


    public static void alert(Alert.AlertType alertType, String alertTitle, String alertText,String headerText){
        Alert alert = new Alert(alertType);
        alert.setTitle(alertTitle);
        alert.setContentText(alertText);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }

    public static boolean checkForDecimal(String number){
        boolean decimal = true;
        for(int i=0; i<number.length();i++){
            if(number.charAt(i) >= '0' && number.charAt(i) <= '9' || number.charAt(i) == '.');
            else {
                decimal = false;
            }
        }
        return decimal;
    }

    public static boolean checkForDecimalPlusMinus(String number){
        boolean decimalPlusMinus = true;
        for(int i=0; i<number.length();i++){
            if(number.charAt(i) >= '0' && number.charAt(i) <= '9' || number.charAt(i) == '.' || number.charAt(i)=='+'|| number.charAt(i)=='-');
            else {
                decimalPlusMinus = false;
            }
        }
        return decimalPlusMinus;
    }

    public static boolean checkForInt(String number){
        boolean integer = true;
        for(int i=0; i<number.length();i++){
            if(number.charAt(i) >= '0' && number.charAt(i) <= '9');
            else {
                integer = false;
            }
        }
        return integer;
    }

    public static boolean checkPhoneNo(String number){
        boolean numberFormat = true;
        int numberDigits=0;
        //Checks if the number is in right format.
        for(int i=0; i<number.length();i++){
            numberDigits++;
            numberFormat=checkForInt(number);
        }
        if(numberDigits==11 && numberFormat);
        else if (number.length() == 0);
        else{
            numberFormat = false;
        }
        return numberFormat;
    }

    public static boolean checkAlphabets(String name){
        boolean nameFlag = true;
        for(int i=0;i<name.length();i++){
            if(name.charAt(i)>='a' && name.charAt(i)<='z' || name.charAt(i)>='A' && name.charAt(i)<='Z' || name.charAt(i)== 32);
            else {
                nameFlag = false;
            }
        }
        return  nameFlag;
    }
}
