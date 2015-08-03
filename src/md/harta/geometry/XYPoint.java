package md.harta.geometry;

/**
 * Created by sergpank on 20.02.2015.
 */
public class XYPoint {
  private double x;
  private double y;

  /**
   * @param x
   * @param y
   */
  public XYPoint(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /**
   * @param height - should be negative
   */
  public void liftUp(double height)
  {
    y += height;
  }

  /**
   * @return x
   */
  public double getX() {
    return x;
  }

  /**
   * @return у
   */
  public double getY() {
    return y;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    XYPoint point = (XYPoint) o;

    if (Double.compare(point.x, x) != 0) return false;
    if (Double.compare(point.y, y) != 0) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(x);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "Point{" +
        "x=" + x +
        ", y=" + y +
        '}';
  }

  public boolean equals(XYPoint point, double eps)
  {
    if (this == point) return true;
    if (point == null || getClass() != point.getClass()) return false;

    if (Math.abs(Double.compare(point.x, x)) > eps) return false;
    if (Math.abs(Double.compare(point.y, y)) > eps) return false;

    return true;
  }
}
