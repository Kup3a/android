package ru.kazakov.kazakov_tack_1.clearJava;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.view.ContextThemeWrapper;

import ru.kazakov.kazakov_tack_1.R;

/**
 * Created by user on 16.03.2016.
 */
public class MyCounter {
    private static List<String> oneNineM;
    private static List<String> oneNineF;
    private static List<String> tenNineteen;
    private static List<String> twentyNinety;
    private static List<String> hundreds;
    private static List<String> thousandMF;
    private static String million;
    private static String zero;

    public MyCounter(Context context) {
        oneNineM = Arrays.asList(context.getResources().getStringArray(R.array.oneNineM));
        oneNineF = Arrays.asList(context.getResources().getStringArray(R.array.oneNineF));
        tenNineteen = Arrays.asList(context.getResources().getStringArray(R.array.tenNineteen));
        twentyNinety = Arrays.asList(context.getResources().getStringArray(R.array.twentyNinety));
        hundreds = Arrays.asList(context.getResources().getStringArray(R.array.hundreds));
        thousandMF = Arrays.asList(context.getResources().getStringArray(R.array.thousandMF));
        million = context.getResources().getString(R.string.million);
        zero = context.getResources().getString(R.string.zero);
    }

    public String convert(int n) {

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
            return million;
        } else {
            return result.toString().replaceAll("  ", " ");
        }
    }

}


