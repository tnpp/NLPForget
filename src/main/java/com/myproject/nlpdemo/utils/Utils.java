package com.myproject.nlpdemo.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Utils {
    public static List<String> readResourceFile(String path) {
        List<String> list = new ArrayList<>();
        try (InputStream is = Utils.class.getClassLoader().getResourceAsStream(path);
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));) {
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.trim().length() <= 0) {
                    continue;
                }
                list.add(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static double round2(double d) {
        return round(d, 2);
    }

    public static double round(double d, int digits) {
        BigDecimal bg = new BigDecimal(d).setScale(digits, RoundingMode.HALF_UP);
        return bg.doubleValue();
    }

    public static boolean isDigit(String strNum) {
        return strNum.matches("-?[0-9]+\\.?[0-9]*");
    }

    /**
     * 和Set中的字符串项目，该字符串是否是细粒度 <br>
     * 判断依据：str包含(substring)集合中的字符串
     * 
     * @param set 字符串集合
     * @param str 要判断是否是细粒度的字符串
     * @return
     */
    public static boolean isFineGrained(Set<String> set, String str) {
        for (String e : set) {
            if (str.contains(e)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {

        System.out.println(isDigit("220.45"));
        System.out.println(isDigit("-220"));
        System.out.println(isDigit("220.0m"));
    }

}
