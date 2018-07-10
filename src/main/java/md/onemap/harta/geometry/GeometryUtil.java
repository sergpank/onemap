package md.onemap.harta.geometry;

import md.onemap.harta.osm.OsmNode;
import md.onemap.harta.projector.AbstractProjector;

import java.util.List;

/**
 * Created by sergpank on 05.03.2015.
 */
public class GeometryUtil
{

  public static final double EQUATOR_LENGTH_METERS = 2.0 * Math.PI * AbstractProjector.EARTH_RADIUS_M;

  public static final double RADIANS_IN_METER = 1.0 / (AbstractProjector.EARTH_RADIUS_M);

  public static final double DEGREES_IN_METER = 360.0 / EQUATOR_LENGTH_METERS;

  public static final double EPS = 0.000001;

  /**
   * @param leftPoint Left point
   * @param rightPoint Right point
   * @return Line as coefficients for normal form of line equation Ax + By + C = 0
   */
  public static Line getLine(XYPoint leftPoint, XYPoint rightPoint){
    double a = rightPoint.getY() - leftPoint.getY();
    double b = leftPoint.getX() - rightPoint.getX();
    double c = rightPoint.getX() * leftPoint.getY() - leftPoint.getX() * rightPoint.getY();
    return new Line(a, b, c);
  }

  public static void main(String[] args)
  {
    System.out.println("diagonal right-to-left high");
    System.out.println(getLine(new XYPoint(10, 10), new XYPoint(0, 30)));
    System.out.println(getLine(new XYPoint(0, 30), new XYPoint(10, 10)));

    System.out.println("diagoanl left-to-right high");
    System.out.println(getLine(new XYPoint(10, 10), new XYPoint(20, 30)));
    System.out.println(getLine(new XYPoint(20, 30), new XYPoint(10, 10)));

    System.out.println("diagonal left-to-right right");
    System.out.println(getLine(new XYPoint(10, 10), new XYPoint(20, 20)));
    System.out.println(getLine(new XYPoint(20, 20), new XYPoint(10, 10)));

    System.out.println("diagonal right-to-left right");
    System.out.println(getLine(new XYPoint(10, 10), new XYPoint(20, 0)));
    System.out.println(getLine(new XYPoint(20, 0), new XYPoint(10, 10)));

    System.out.println("diagonal left-to-right low");
    System.out.println(getLine(new XYPoint(10, 10), new XYPoint(30, 20)));
    System.out.println(getLine(new XYPoint(30, 20), new XYPoint(10, 10)));

    System.out.println("diagonal right-to-left low");
    System.out.println(getLine(new XYPoint(10, 10), new XYPoint(30, 0)));
    System.out.println(getLine(new XYPoint(30, 0), new XYPoint(10, 10)));

    System.out.println("horizontal");
    System.out.println(getLine(new XYPoint(10, 10), new XYPoint(20, 10)));
    System.out.println(getLine(new XYPoint(20, 10), new XYPoint(10, 10)));

    System.out.println("vertical");
    System.out.println(getLine(new XYPoint(10, 10), new XYPoint(10, 20)));
    System.out.println(getLine(new XYPoint(10, 20), new XYPoint(10, 10)));
  }

  /**
   * @param center circle center
   * @param point a point that lays on the circle border
   * @return Circle class
   */
  public static Circle getCircle(XYPoint center, XYPoint point)
  {
    double radius = GeometryUtil.getDistanceBetweenPoints(center, point);
    return new Circle(center, radius);
  }

  public static Line getPerpendicular(Line line, XYPoint point){
    double a = line.getB() * -1;
    double b = line.getA();
    double c = line.getB() * point.getX() - line.getA() * point.getY();
    Line perpendicular = new Line(a, b, c);
    perpendicular.setLeftPoint(point);
    return perpendicular;
  }

  //TODO convert distance to meters

  /**
   * Get points that stay on perpendicular line and on specific distance from given line
   *
   * @param line
   * @param point
   * @param distance in meters
   * @return 4 points of rectangle of a given width
   */
  public static XYPoint[] getPerpendicularPoints(Line line, LatLonPoint point,
                                                 double distance, AbstractProjector projector){
    double scale = projector.getScale(point);
    double delta = distance / 2 * scale;
    XYPoint xyPoint = projector.getXY(point.getLat(), point.getLon());
    if (line.getA() == 0){
      // Значит это горизонтальная
      return new XYPoint[]{new XYPoint(xyPoint.getX(), xyPoint.getY() - delta),
                           new XYPoint(xyPoint.getX(), xyPoint.getY() + delta)};
    }
    else if (line.getB() == 0)
    {
      // Значит это вертикальная линия
      return new XYPoint[]{new XYPoint(xyPoint.getX() - delta, xyPoint.getY()),
          new XYPoint(xyPoint.getX() + delta, xyPoint.getY())};
    }
    Line perpendicular = getPerpendicular(line, xyPoint);

    double dy = Math.sin(perpendicular.getSlope()) * delta;
    double y1 = xyPoint.getY() - dy;
    double y2 = xyPoint.getY() + dy;
    return new XYPoint[]{ new XYPoint(perpendicular.getX(y1), y1) ,
                          new XYPoint(perpendicular.getX(y2), y2)
                         };
  }

  public static double getHighwayLength(AbstractProjector projector, List<OsmNode> nodes)
  {
    double length = 0;
    for (int i = 1; i < nodes.size(); i++)
    {
      OsmNode nodeA = nodes.get(i - 1);
      OsmNode nodeB = nodes.get(i);

      XYPoint pointA = projector.getXY(nodeA.getLat(), nodeA.getLon());
      XYPoint pointB = projector.getXY(nodeB.getLat(), nodeB.getLon());

      length += getDistanceBetweenPoints(pointA, pointB);
    }
    return length;
  }

  public static double getDistanceBetweenPoints(XYPoint pointA, XYPoint pointB)
  {
    double dx = Math.abs(pointA.getX() - pointB.getX());
    double dy = Math.abs(pointA.getY() - pointB.getY());

    return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
  }

  /**
   * Calculate distance between 2 points in meters
   * @param pointA
   * @param pointB
   * @return Distance between points in meters
   */
  public static double getDistanceOrtodroma(LatLonPoint pointA, LatLonPoint pointB){
    double latA = Math.toRadians(pointA.getLat());
    double latB = Math.toRadians(pointB.getLat());
    double lonA = Math.toRadians(pointA.getLon());
    double lonB = Math.toRadians(pointB.getLon());
    double delta = Math.acos(
              (Math.sin(latA) * Math.sin(latB))
            + (Math.cos(latA) * Math.cos(latB) * Math.cos(lonB - lonA)));
    double distance = AbstractProjector.EARTH_RADIUS_M * delta;
    return distance;
  }

  public static double getDistanceВелосипед(LatLonPoint pointA, LatLonPoint pointB) {
    /*
    1 - Сконвертировать точки из сверических координат в декартовы и покрыть это тестами
    2 - Из декартовых координат составить векторы
    3 - Из векторов скалярным произведением вычислить угол между ними
    4 - Зная угол вычисляем расстояние между точками т.к 1 градус = диаметр Земли / 360
        Кроме того угол в радианах между точками помноженый на радиус земли даёт длину дуги между точками
     */
    return 0;
  }

  public static XYPoint getLineIntersection(Line line1, Line line2, XYPoint startPoint, XYPoint endPoint, boolean checkBounds)
  {
    // A1x + B1y + C1 = 0
    // A2x + B2y + C2 = 0

    // x = (-B1y - C1) / A1

    // (-A2*B1y - A2*C1) / A1 + B2y + C2 = 0
    // B2y + C2 - ()
    double a1 = line1.getA();
    double a2 = line2.getA();
    double b1 = line1.getB();
    double b2 = line2.getB();
    double c1 = line1.getC();
    double c2 = line2.getC();
    double y = (a2 / a1 * c1 - c2) / (b2 - a2 / a1 * b1);
    double x = (-b1 * y - c1) / a1;

    XYPoint point = new XYPoint(x, y);
    if (Double.isNaN(x) || Double.isNaN(y))
    {
      return null;
    }
    else if (!checkBounds || pointBetween(point, startPoint, endPoint))
    {
      return point;
    }
    else
    {
      // Отрезки не пересекаются
      return null;
    }
  }

  private static boolean pointBetween (XYPoint point, XYPoint point1, XYPoint point2)
  {
    return between(point.getX(), point1.getX(), point2.getX()) && between(point.getY(), point1.getY(), point2.getY());
  }

  /**
   * Check if v is betseen v1 and v2
   * @param v
   * @param v1
   * @param v2
   * @return true if v is between v1 and v2, false otherwise
   */
  private static boolean between(double v, double v1, double v2)
  {
    return v >= Math.min(v1, v2) && v <= Math.max(v1, v2) || Math.abs(v1 - v) < EPS || Math.abs(v2 - v) < EPS;
  }

  /**
   * In our case this intersection is necessary for "contact point" calculation of label letter and road.
   * Even thou that there may be 2 points of line-circle intersection - we need only the most right one,
   * because the beginning of circle is on the beginning of the road,
   * and the road beginning is the most left point of the road.
   */
  public static XYPoint[] getLineCircleIntersection(Line line, Circle circle)
  {
    if (line.getA() == 0)
    {
      return calcHorizontalIntersection(line, circle);
    }
    if (line.getB() == 0)
    {
      return calcVerticalIntersection(line, circle);
    }
    else
    {
      return calcGeneralIntersection(line, circle);
    }
  }

  private static XYPoint[] calcHorizontalIntersection(Line line, Circle circle)
  {
    double y = line.getLeftPoint().getY();
    double yc = circle.getCenter().getY();
    double xc = circle.getCenter().getX();

    if (Math.abs(y - yc) > circle.getRadius())
    {
      // not intersection in such case
      return null;
    }
    else if (Math.abs(y - yc) == circle.getRadius())
    {
      // there is only 1 intersection
      return new XYPoint[]{new XYPoint(xc, y)};
    }
    //there are 2 intersections
    double dy = yc - y;
    double dist = Math.sqrt(circle.getRadius() * circle.getRadius() - dy * dy);
    return new XYPoint[]{new XYPoint(xc - dist, y),
                         new XYPoint(xc + dist, y)};
  }

  private static XYPoint[] calcVerticalIntersection(Line line, Circle circle)
  {
    double x = line.getLeftPoint().getX();
    double yc = circle.getCenter().getY();
    double xc = circle.getCenter().getX();

    if (Math.abs(x - xc) > circle.getRadius())
    {
      // not intersection in such case
      return null;
    }
    else if (Math.abs(x - xc) == circle.getRadius())
    {
      // there is only 1 intersection
      return new XYPoint[]{new XYPoint(x, yc)};
    }
    //there are 2 intersections
    double dx = xc - x;
    double dist = Math.sqrt(circle.getRadius() * circle.getRadius() - dx * dx);
    return new XYPoint[]{new XYPoint(x, yc - dist),
                         new XYPoint(x, yc + dist)};

  }

  private static XYPoint[] calcGeneralIntersection(Line line, Circle circle)
  {
    // Solution of system of 2 equations
    // (x – a)^2 + (y – b)^2 = R^2
    // Ax + By + C = 0
    //

    /*
    y = (-A/B)x - C/B // y = kx + b

    (x - a)^2 + ((-A/B)x - C/B - b)^2 = R^2
    x^2 - 2ax + a^2 + (-A/B)^2x^2 - 2(-A/B)(-C/B - b)x + (-C/B - b)^2 = R^2
    (1 + A^2/B^2)x^2 + (-2a + (-2(A/B)(-C/B) - b)x + (a^2 + (-C/B - b)^2 - R^2) = 0

    #A = 1 + A^2/B^2
    #B = -2a - 2(-A/B)(-C/B) - b
    #C = a^2 + (-C/B - b)^2 - R^2

    So we have to solve the following equation:
    Ax^2 + Bx + C = 0;
     */

    double xc = circle.getCenter().getX(); // a
    double yc = circle.getCenter().getY(); // b

    double a = line.getA(); // A
    double b = line.getB(); // B
    double c = line.getC(); // C

    double A = 1 + Math.pow(-a / b, 2);
    double B =  (2 * (c / b + yc) * (a / b))  - (2 * xc);
    double C = Math.pow(xc, 2) + Math.pow((c / b) + yc, 2) - Math.pow(circle.getRadius(), 2);

    double D = Math.pow(B, 2) - 4 * A * C;

    if (D < 0)
    {
      // No intersection
      return null;
    }

    double x1 = ((-B) - Math.sqrt(D)) / (2 * A);
    double x2 = ((-B) + Math.sqrt(D)) / (2 * A);

    double y1 = (-(b / a)) * x1 - (c / b);
    double y2 = (-(b / a)) * x2 - (c / b);

    return new XYPoint[]{new XYPoint(x1, y1), new XYPoint(x2, y2)};
  }

  public static double metersToPixels(AbstractProjector projector, double meters)
  {
    double roadWidth = GeometryUtil.DEGREES_IN_METER * meters;
    XYPoint center = projector.getXY(47, 29);
    XYPoint xyLon = projector.getXY(47, 29 + roadWidth);

    return Math.ceil(xyLon.getX() - center.getX());
  }
}
