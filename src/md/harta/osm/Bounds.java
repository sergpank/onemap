package md.harta.osm;

import md.harta.projector.AbstractProjector;
import md.harta.geometry.XYPoint;

/**
 * Created by sergpank on 03.03.2015.
 */
public class Bounds {
  private double xMin;
  private double xMax;
  private double yMin;
  private double yMax;

  public Bounds(AbstractProjector projector, double maxLat, double minLon, double minLat, double maxLon) {
    XYPoint min = projector.getXY(maxLat, minLon);
    xMin = min.getX();
    yMin = min.getY();

    XYPoint max = projector.getXY(minLat, maxLon);
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

  @Override
  public String toString()
  {
    return "Bounds\n{" +
        "xMin=" + xMin +
        ", xMax=" + xMax +
        ", yMin=" + yMin +
        ", yMax=" + yMax +
        "}\n width = " + (xMax - xMin) +
        "\n height = " + (yMax - yMin);
  }
}
