package com.website.service.utils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DateTimeUtil {


    public static void main(String[] args){
        System.out.println(timeNow());
    }


    public static String timeNow(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
