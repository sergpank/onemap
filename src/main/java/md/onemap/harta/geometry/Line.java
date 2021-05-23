package md.onemap.harta.geometry;

import java.util.Objects;

/**
 * Created by sergpank on 05.03.2015.
 */
public class Line {
  private XYPoint leftPoint;
  private XYPoint rightPoint;

  // normal form of line equation Ax + By + C = 0
  final private double a;
  final private double b;
  final private double c;

  final private Double slope;

  /**
   * This is not safe declaration of line.
   * Use it carefully, only if you know what you are doing.
   */
  public Line(double a, double b, double c) {
    this.a = a;
    this.b = b;
    this.c = c;

    this.slope = calcSlope();
  }
  
  public Line(XYPoint pointA, XYPoint pointB)
  {
    if (pointA.getX() < pointB.getX())
    {
      leftPoint = pointA;
      rightPoint = pointB;
    }
    else if (pointA.getX() == pointB.getX())
    {
      if (pointA.getY() > pointB.getY())
      {
        leftPoint = pointA;
        rightPoint = pointB;
      }
      else
      {
        leftPoint = pointB;
        rightPoint = pointA;
      }
    }
    else
    {
      leftPoint = pointB;
      rightPoint = pointA;
    }
    // (x - x1) / (x2 - x1) = (y - y1) / (y2 - y1)
    // --->
    // (y2 - y1) * x - (x2 - x1) * y + (x2 * y1 - x1 * y2) = 0
    // Ax + By + C = 0
    // --->
    // A = (y2 - y1)
    // B = - (x2 - x1) = (x1 - x2)
    // C = (x2*y1 - x1*y2)

    a = rightPoint.getY() - leftPoint.getY();
    b = leftPoint.getX() - rightPoint.getX();
    c = rightPoint.getX() * leftPoint.getY() - leftPoint.getX() * rightPoint.getY();

    this.slope = calcSlope();
  }

  public double getX(double y){
    double x = (b * y + c) / (-a);
    return x;
  }

  public double getY(double x){
    double y = (a * x + c) / (-b);
    return y;
  }

  public double getA() {
    return a;
  }

  public double getB() {
    return b;
  }

  public double getC() {
    return c;
  }

  // Line slope in radians
  public double getSlope() 
  {
    return slope;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Line)) return false;

    Line line = (Line) o;

    if (Double.compare(line.a, a) != 0) return false;
    if (Double.compare(line.b, b) != 0) return false;
    if (Double.compare(line.c, c) != 0) return false;
    if (Double.compare(line.slope, slope) != 0) return false;

    return true;
  }

  public boolean isIdentical(Line l)
  {
    if (Objects.equals(this.slope, l.slope)) {
      if ((this.a == l.a || this.a == -l.a)
          && (this.b == l.b || this.b == -l.b)
          && (this.c == l.c || this.c == -l.c)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(a);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(b);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(c);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(slope);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "Line{" +
        "left=" + leftPoint +
        ", right=" + rightPoint +
        ", a=" + a +
        ", b=" + b +
        ", c=" + c +
        ", slope=" + slope +
        '}';
  }

  // This method is necessary for GeometryUtil -> vertical and horizontal intersection cases
  public void setLeftPoint(XYPoint point) {
    this.leftPoint = point;
  }

  public XYPoint getLeftPoint()
  {
    return leftPoint;
  }

  public XYPoint getRightPoint()
  {
    return rightPoint;
  }

  public double calcLength()
  {
    return GeometryUtil.getDistanceBetweenPoints(leftPoint, rightPoint);
  }

  // Calculate line slope in radians
  private double calcSlope()
  {
    return (b == 0) ? (Math.PI / 2) : (a == 0) ? 0 : Math.atan(a / (-b));
  }
}
