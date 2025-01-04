package com.example.demo.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static Date TimetoDate(String s) throws ParseException {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

    /**     目前没用上
     * 将输入的日期字符串从一种格式转换为目标格式，并返回 Date 对象。
     *
     * @param inputDate 输入的日期字符串（例如 "Sun Jan 05 00:00:00 CST 2025"）。
     * @return 格式化后的日期对象（Date 类型，格式为 "yyyy-MM-dd HH:mm:ss"）。
     * @throws ParseException 如果输入日期字符串无法解析，则抛出此异常。
     */
    public static Date convertToTargetFormat(String inputDate) throws ParseException {
        System.out.println("before:"+inputDate);
        // 定义输入日期格式（与输入字符串 "Sun Jan 05 00:00:00 CST 2025" 的格式匹配）
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

        // 定义目标日期格式，用于标准化日期格式
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 将输入的日期字符串解析为 Date 对象
        // 如果输入字符串格式不正确，会抛出 ParseException
        Date parsedDate = inputFormat.parse(inputDate);

        // 将解析后的 Date 对象格式化为目标字符串格式
        // 然后重新解析为 Date 对象（确保返回符合目标格式的 Date）
        String formattedDateStr = outputFormat.format(parsedDate);
        System.out.println("after:"+formattedDateStr);
//        return outputFormat.parse(formattedDateStr);
        return TimetoDate(formattedDateStr);
    }
}
