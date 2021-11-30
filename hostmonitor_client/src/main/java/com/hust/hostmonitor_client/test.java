package com.hust.hostmonitor_client;

import java.text.DecimalFormat;

public class test {
    public static void main(String[] args){
        double a=0.0123457;
        DecimalFormat df=new DecimalFormat("#.######");
        String result=df.format(a);
        System.out.println(result);
        a=Double.parseDouble(result);
    }
}
