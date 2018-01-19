package md.onemap.harta.util;

public class Stopwatch
{
  private static long start;
  private static long duration;

  public static void start()
  {
    start = System.currentTimeMillis();
  }

  public static long stop()
  {
    duration = System.currentTimeMillis() - start;
    return duration;
  }

  public static long getDuration()
  {
    return duration;
  }

  public static String pretty()
  {
    return TimePrettyPrint.prettyPrint(duration);
  }
}
