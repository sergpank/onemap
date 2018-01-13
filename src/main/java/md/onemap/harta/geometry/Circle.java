package md.onemap.harta.geometry;

/**
 * Created by sergpank on 22.07.15.
 */
public class Circle
{
  private final XYPoint center;
  private final double radius;

  public Circle(XYPoint center, double radius)
  {
    this.center = center;
    this.radius = radius;
  }

  public XYPoint getCenter()
  {
    return center;
  }

  public double getRadius()
  {
    return radius;
  }

  @Override
  public String toString()
  {
    return "Circle{" +
        "center=" + center +
        ", radius=" + radius +
        '}';
  }
}
