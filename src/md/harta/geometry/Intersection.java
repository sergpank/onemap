package md.harta.geometry;

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
  public String toString()
  {
    return "Intersection{" +
        "point=" + point +
        ", angle=" + angle +
        '}';
  }
}
