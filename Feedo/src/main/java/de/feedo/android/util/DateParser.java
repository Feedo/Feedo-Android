package de.feedo.android.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jan-Henrik on 15.10.13.
 */
public class DateParser {
    public static Date parse(String input) throws java.text.ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

        if (input.endsWith("Z")) {
            input = input.substring(0, input.length() - 1) + "GMT-00:00";
        } else {
            int inset = 6;

            String s0 = input.substring(0, input.length() - inset);
            String s1 = input.substring(input.length() - inset, input.length());

            input = s0 + "GMT" + s1;
        }

        return df.parse(input);

    }
}
