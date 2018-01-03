package md.harta.projector;

import md.harta.geometry.LatLonPoint;
import md.harta.geometry.XYPoint;
import md.harta.osm.OsmNode;

/**
 * Created by sergpank on 20.02.2015.
 */
public abstract class AbstractProjector {
  public static final int EARTH_RADIUS_M = 6_371_000;
  public static final int MAX_LAT = 90;
  public static final int MAX_LON = 180;

  protected double shift;
  protected double width;
  protected double height;
  protected double scale;

  public abstract XYPoint getXY(double lat, double lon);

  public XYPoint getXY(LatLonPoint latLonPoint) {
    return getXY(latLonPoint.getLat(), latLonPoint.getLon());
  }

  public abstract LatLonPoint getLatLon(double x, double y);

  public LatLonPoint getLatLon(XYPoint xyPoint) {
    return getLatLon(xyPoint.getX(), xyPoint.getY());
  }

  public abstract double getScale(LatLonPoint point);

  public double getWidth() {
    return width;
  }

  public double getHeight() {
    return height;
  }

  public XYPoint getXY(OsmNode node) {
    return getXY(node.getLat(), node.getLon());
  }
}
