package md.onemap.harta.util;

import md.onemap.harta.projector.MercatorProjector;

/**
 * Created by sergpank on 24.05.15.
 */
public class ScaleCalculator
{

  public static final int MIN_SCALE_LEVEL = 1; // Максимальное отдаление (всего 1 тайл 256 х 256)
  public static final int MAX_SCALE_LEVEL = 20; // Максимальное приближение

  public static double getRadiusForLevel(int level)
  {
    return MercatorProjector.LEVEL_1_RADIUS * Math.pow(2, level);
  }

  public static void main(String[] args)
  {
    for (int i = MIN_SCALE_LEVEL; i <= MAX_SCALE_LEVEL; i++)
    {
      System.out.printf("%d :: %f\n", i, getRadiusForLevel(i));
    }
  }
}
