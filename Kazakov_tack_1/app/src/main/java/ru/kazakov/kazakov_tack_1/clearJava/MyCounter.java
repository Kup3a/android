package ru.kazakov.kazakov_tack_1.clearJava;

import java.util.Arrays;
import java.util.List;

/**
 * Created by user on 16.03.2016.
 */
public class MyCounter {

    private static List<String> oneNineM = Arrays.asList("один", "два", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять");
    private static List<String> oneNineF = Arrays.asList("одна", "две", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять");     // одна, две..девять
    private static List<String> tenNineteen = Arrays.asList("десять", "одиннадцать", "двенадцать", "тринадцать", "четырнадцать", "пятнадцать", "шестнадцать", "семнадцать", "восемнадцать", "девятнадцать"); // десять, одиннадцать..девятнадцать
    private static List<String> twentyNinety = Arrays.asList("двадцать", "тридцать", "сорок", "пятьдесят", "шестьдесят", "семьдесят", "восемьдесят", "девяносто");  // двадцать..девяносто
    private static List<String> hundreds = Arrays.asList("сто", "двести", "триста", "четыреста", "пятьсот", "шестьсот", "семьсот", "восемьсот", "девятьсот");      // сто..девятьсот
    private static List<String> thousandMF = Arrays.asList("тысяч", "тысяча", "тысячи", "тысячи", "тысячи", "тысяч", "тысяч", "тысяч", "тысяч", "тысяч");    // тысяч, тысяча, тысячи, тысячи..тысяч
    private static String million = "миллион";

    public static String convert(int n) {
        int c, c1;
        StringBuilder result = new StringBuilder();
        String res = new String();

        for (int i = 0; i < 2; i++) {

            c1 = n % 10;
            if (i == 1 && n != 0) {
                if (n%100 >= 10 && n%100 <= 20) {
                    result.insert(0, " " +  thousandMF.get(9) + " ");
                    res = result.toString();
                    result.delete(0, result.length());
                } else {
                    result.insert(0, " " + thousandMF.get(c1) + " ");
                    res = result.toString();
                    result.delete(0, result.length());
                }
            }
            if (c1 - 1 >= 0 && n != 0) {
                if (i == 0)
                    result.insert(0, oneNineM.get(c1 - 1));
                else
                    result.insert(0, oneNineF.get(c1 - 1));
            }
            n = n / 10;

            c = n % 10;
            if (c - 2 >= 0 && n != 0) {
                result.insert(0, twentyNinety.get(c - 2) + " ");
            }
            if (c == 1 && n != 0) {
                result.delete(0, result.length());
                result.insert(0, tenNineteen.get(c1));
            }
            n = n / 10;

            c1 = n % 10;
            if (c1 - 1 >= 0 && n != 0) {
                result.insert(0, hundreds.get(c1 - 1) + " ");
            }
            n = n / 10;
        }

        result.append(res);

        if (result.toString().isEmpty()) {
            return "ноль";
        } else if (n == 1) {
            return "один миллион";
        } else {
            return result.toString().replaceAll("  ", " ");
        }
    }

}


