package md.harta.geometry;

/**
 * Created by sergpank on 05.03.2015.
 */
public class Line {
  private XYPoint leftPoint;
  private XYPoint rightPoint;

  private double a;
  private double b;
  private double c;

  private Double slope;

  public Line(double a, double b, double c) {
    this.a = a;
    this.b = b;
    this.c = c;
  }
  
  public Line(XYPoint pointA, XYPoint pointB)
  {
    a = pointA.getY() - pointB.getY();
    b = pointB.getX() - pointA.getX();
    c = pointA.getX() * pointB.getY() - pointB.getX() * pointA.getY();

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
    }
    else
    {
      leftPoint = pointB;
      rightPoint = pointA;
    }
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

  public double getSlope() 
  {
    if (slope == null)
    {
      if (b ==0)
      {
        if (leftPoint.getY() > rightPoint.getX())
          slope = -Math.PI / 2;
        else
          slope = Math.PI / 2;
      }
      else
      {
        double x1 = 10;
        double x2 = 20;
        double y1 = (a * x1 + c) / (b * -1);
        double y2 = (a * x2 + c) / (b * -1);
        slope = b == 0 ? Math.PI / 2 : Math.atan((y2 - y1) / (x2 - x1));
      }
    }
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
    if (a == l.a)
    {
      if (b == l.b)
      {
        if (slope == l.slope)
        {
          return true;
        }
      }
    }
    else if (a == -l.a)
    {
      if (b == l.b)
      {
        if (slope == -l.slope || ((c >= 0 && l.c <=0) || (c <=0 && l.c >=0)))
        {
          return true;
        }
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
        "a=" + a +
        ", b=" + b +
        ", c=" + c +
        ", slope=" + slope +
        '}';
  }

  public XYPoint getLeftPoint()
  {
    return leftPoint;
  }

  public XYPoint getRightPoint()
  {
    return rightPoint;
  }
}
