package md.harta.geometry;

import md.harta.projector.AbstractProjector;

/**
 * Created by sergpank on 03.03.2015.
 */
public class Bounds {
  private double xMin;
  private double xMax;
  private double yMin;
  private double yMax;

  private double minLat;
  private double maxLat;
  private double minLon;
  private double maxLon;

  public Bounds(AbstractProjector projector, double minLat, double minLon, double maxLat, double maxLon) {
    this.minLat = minLat;
    this.minLon = minLon;
    this.maxLat = maxLat;
    this.maxLon = maxLon;

    if (projector != null)
    {
      XYPoint min = projector.getXY(maxLat, minLon);
      xMin = min.getX();
      yMin = min.getY();

      XYPoint max = projector.getXY(minLat, maxLon);
      xMax = max.getX();
      yMax = max.getY();
    }
  }

  public Bounds(double xMin, double yMin, double xMax, double yMax, double minLat, double minLon, double maxLat, double maxLon)
  {
    this.xMin = xMin;
    this.yMin = yMin;
    this.xMax = xMax;
    this.yMax = yMax;

    this.minLat = minLat;
    this.minLon = minLon;
    this.maxLat = maxLat;
    this.maxLon = maxLon;
  }

  public Bounds(double xMin, double yMin, double xMax, double yMax)
  {
    this.xMin = xMin;
    this.yMin = yMin;
    this.xMax = xMax;
    this.yMax = yMax;
  }

  public Bounds(AbstractProjector projector, Bounds bounds)
  {
    this(projector, bounds.getMinLat(), bounds.getMinLon(), bounds.getMaxLat(), bounds.getMaxLon());
  }

  /**
   * @param height - should be negative
   */
  public void liftUp(double height)
  {
    yMin += height;
    yMax += height;
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
    return "Bounds{" +
        "xMin=" + xMin +
        ", xMax=" + xMax +
        ", yMin=" + yMin +
        ", yMax=" + yMax +
        ", minLat=" + minLat +
        ", minLon=" + minLon +
        ", maxLat=" + maxLat +
        ", maxLon=" + maxLon +
        '}';
  }
}
