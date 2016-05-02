package apps.modisku.com.modisku.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Danz on 10/31/2015.
 */
public class DateUtils {
    public static Calendar stringToCalendar(String dateString)
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try
        {
            date = formatter.parse(dateString);
            cal.setTime(date);
        }
        catch(ParseException e)
        {
            cal = null;
        }
        return cal;
    }


    public static long getElapseday(Calendar c1, Calendar c2)
    {
        long milliSeconds1 = c1.getTimeInMillis();
        long milliSeconds2 = c2.getTimeInMillis();
        long periodSeconds = (milliSeconds2 - milliSeconds1) / 1000;
        long elapsedDays = periodSeconds / 60 / 60 / 24;
        //String elapsedDaysText = String.format("%d days ago", elapsedDays);
        return elapsedDays;
    }
}
