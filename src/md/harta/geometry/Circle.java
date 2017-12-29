package md.harta.geometry;

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

  public static void main(String[] args)
  {
//    Line line = new Line(10.0, 10.0, -2386.19288125423);
//    Circle circle = new Circle(new XYPoint(184.3096440627115, 54.309644062711506), 10.0 / 3);
    double C = Math.pow(184.3096440627115, 2) * 2 - (Math.pow(10.0 / 3, 2));
    double B = (2386.19288125423 / 10 - 54.309644062711506) * 2 * 2;
    double D = B * B - 8 * C;
    System.out.println(B * B);
    System.out.println(4 * 2 * C);
    System.out.println(D);
  }
}
