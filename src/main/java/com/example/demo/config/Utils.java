package com.example.demo.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class Utils {
    public static Date TimetoDate(String s) throws ParseException {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.parse(s);
    }
    public static int TimeDiff(String dateString1,String dateString2) throws ParseException {
        LocalDate date1 = LocalDate.parse(dateString1);
        LocalDate date2 = LocalDate.parse(dateString2);
        System.out.println(date1);
        System.out.println(date2);
        Duration duration = Duration.between(date1.atStartOfDay(), date2.atStartOfDay());
        int days = (int) duration.toDays(); // 相隔天数
        return days+1;//包含开头结尾
    }
    public static LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
