package md.harta.util;

import md.harta.geometry.Bounds;

/**
 * Created by sergpank on 27.05.15.
 */
public class BoxIntersector
{
  public static boolean intersectXY(Bounds a, Bounds b)
  {
    if ( (a.getxMax() < b.getxMin()) || (a.getxMin() > b.getxMax()) )
    {
      return false;
    }
    if ( (a.getyMax() < b.getyMin()) || (a.getyMin() > b.getyMax()) )
    {
      return false;
    }
    return true;
  }

  public static boolean intersectLatLon(Bounds a, Bounds b)
  {
    if ( (a.getMaxLon() < b.getMinLon()) || (a.getMinLon() > b.getMaxLon()) )
    {
      return false;
    }
    if ( (a.getMaxLat() < b.getMinLat()) || (a.getMinLat() > b.getMaxLat()) )
    {
      return false;
    }
    return true;
  }

}
