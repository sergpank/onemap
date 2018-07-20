package md.onemap.harta.geometry;

import md.onemap.harta.db.gis.entity.Node;
import md.onemap.harta.projector.AbstractProjector;

import java.util.List;

/**
 * Created by sergpank on 03.03.2015.
 */
public class BoundsLatLon {
  private double minLat;
  private double maxLat;
  private double minLon;
  private double maxLon;

  public BoundsLatLon(double minLat, double minLon, double maxLat, double maxLon)
  {
    this.minLat = minLat;
    this.minLon = minLon;
    this.maxLat = maxLat;
    this.maxLon = maxLon;
  }

  public BoundsLatLon(List<Node> nodes)
  {
    double minLat = Double.MAX_VALUE, maxLat = Double.MIN_VALUE, minLon = Double.MAX_VALUE, maxLon = Double.MIN_VALUE;
    for (Node node : nodes)
    {
      double lat = node.getLat();
      double lon = node.getLon();
      if (minLat > lat)
      {
        minLat = lat;
      }
      if (maxLat < lat)
      {
        maxLat = lat;
      }
      if (minLon > lon)
      {
        minLon = lon;
      }
      if (maxLon < lon)
      {
        maxLon = lon;
      }
    }
    this.minLat = minLat;
    this.minLon = minLon;
    this.maxLat = maxLat;
    this.maxLon = maxLon;
  }

  public BoundsXY toXY(AbstractProjector projector) {
    XYPoint min = projector.getXY(minLat, minLon);
    XYPoint max = projector.getXY(maxLat, maxLon);

    // min-y and max-y --- are inverted
    // ... this happens because latitude center is in the middle of coordinates
    // ... but Y goes from top to bottom of coordinate system
    return new BoundsXY(min.getX(), max.getY(), max.getX(), min.getY());
  }

  public double getMinLat()
  {
    return minLat;
  }

  public double getMaxLat()
  {
    return maxLat;
  }

  public double getMinLon()
  {
    return minLon;
  }

  public double getMaxLon()
  {
    return maxLon;
  }

  @Override
  public String toString()
  {
    return "BoundsLatLon{" +
        ", minLat=" + minLat +
        ", minLon=" + minLon +
        ", maxLat=" + maxLat +
        ", maxLon=" + maxLon +
        '}';
  }
}
