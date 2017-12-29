package md;

import md.harta.geometry.GeometryUtil;
import md.harta.geometry.LatLonPoint;

public class MAIN
{
  public static void main(String[] args)
  {
    for (int i = 0; i <= 10; i++)
    {
      double next = 1 / Math.pow(10, i);
      LatLonPoint pointA = new LatLonPoint(0, 0);
      LatLonPoint pointB = new LatLonPoint(0, next);
      double distance = GeometryUtil.getDistanceOrtodroma(pointA, pointB);

      System.out.printf("%2d :: next = %.10f; distance = %.10f\n", i, next, distance);
    }

    System.out.println();

    for (int i = 20; i > 0; i--) {
      double next = i * 1e-6;
      LatLonPoint pointA = new LatLonPoint(0, 0);
      LatLonPoint pointB = new LatLonPoint(0, next);
      double distance = GeometryUtil.getDistanceOrtodroma(pointA, pointB);

      System.out.printf("%2d :: next = %.10f; distance = %.10f\n", i, next, distance);
    }
  }
}
