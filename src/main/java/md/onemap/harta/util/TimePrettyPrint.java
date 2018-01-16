package md.onemap.harta.util;

public class TimePrettyPrint
{
  public static final int MILLIS_IN_SECOND = 1000;
  public static final int SECONDS_IN_MINUTE = 60;
  public static final int MINUTES_IN_HOUR = 60;
  public static final int HOURS_IN_DAY = 24;

  public static String prettyPrint(long time)
  {
    StringBuilder sb = new StringBuilder();
    if (time > 0)
    {
      long milliseconds = time % MILLIS_IN_SECOND;
      time -= milliseconds;
      time /= MILLIS_IN_SECOND;

      sb.append(milliseconds).append(" ms");
    }

    if (time > 0)
    {
      long seconds = time % SECONDS_IN_MINUTE;
      time -= seconds;
      time /= SECONDS_IN_MINUTE;

      sb.insert(0, " sec, ").insert(0, seconds);
    }

    if (time > 0)
    {
      long minutes = time % MINUTES_IN_HOUR;
      time -= minutes;
      time /= MINUTES_IN_HOUR;

      sb.insert(0, " min, ").insert(0, minutes);
    }

    if (time > 0)
    {
      long hours = time % HOURS_IN_DAY;
      time -= hours;
      time /= HOURS_IN_DAY;

      sb.insert(0, " hours, ").insert(0, hours);
    }

    if (time > 0)
    {
      sb.insert(0, " days, ").insert(0, time);
    }

    sb.insert(0, "[").append("]");
    return sb.toString();
  }
}
