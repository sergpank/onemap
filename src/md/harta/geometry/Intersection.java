package md.harta.geometry;

import java.util.Objects;

/**
 * Created by sergpank on 04.08.15.
 */
public class Intersection
{
  private XYPoint point;
  private double angle;

  public Intersection(XYPoint point, double angle)
  {
    this.point = point;
    this.angle = angle;
  }

  public XYPoint getPoint()
  {
    return point;
  }

  public void setPoint(XYPoint point)
  {
    this.point = point;
  }

  public double getAngle()
  {
    return angle;
  }

  public void setAngle(double angle)
  {
    this.angle = angle;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (!(o instanceof Intersection)) return false;
    Intersection that = (Intersection) o;
    return Objects.equals(angle, that.angle) &&
        Objects.equals(point, that.point);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(point, angle);
  }

  @Override
  public String toString()
  {
    return "Intersection{" +
        "point=" + point +
        ", angle=" + angle +
        '}';
  }
}
