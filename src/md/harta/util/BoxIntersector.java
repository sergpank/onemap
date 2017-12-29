package md.harta.util;

import md.harta.geometry.BoundsLatLon;
import md.harta.geometry.BoundsXY;

/**
 * Created by sergpank on 27.05.15.
 */
public class BoxIntersector
{
  public static boolean intersectXY(BoundsXY a, BoundsXY b)
  {
    if ( (a.getXmax() < b.getXmin()) || (a.getXmin() > b.getXmax()) )
    {
      return false;
    }
    if ( (a.getYmax() < b.getYmin()) || (a.getYmin() > b.getYmax()) )
    {
      return false;
    }
    return true;
  }

  public static boolean intersectLatLon(BoundsLatLon a, BoundsLatLon b)
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
