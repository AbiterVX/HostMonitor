package com.hust.hostmonitor_client.utils;

import java.math.BigDecimal;

public class FormatUtils {
    public static double doubleTo2bits_double(double original){
        BigDecimal b=new BigDecimal(original);
        return b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
