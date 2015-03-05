package md.harta.osm;

import md.harta.projector.AbstractProjector;
import md.harta.projector.Point;

/**
 * Created by sergpank on 03.03.2015.
 */
public class Bounds {
  private double xMin;
  private double xMax;
  private double yMin;
  private double yMax;

  public Bounds(AbstractProjector projector, double maxLat, double minLon, double minLat, double maxLon) {
    Point min = projector.getXY(maxLat, minLon);
    xMin = min.getX();
    yMin = min.getY();

    Point max = projector.getXY(minLat, maxLon);
    xMax = max.getX();
    yMax = max.getY();
  }

  public double getxMin() {
    return xMin;
  }

  public double getxMax() {
    return xMax;
  }

  public double getyMin() {
    return yMin;
  }

  public double getyMax() {
    return yMax;
  }
}
