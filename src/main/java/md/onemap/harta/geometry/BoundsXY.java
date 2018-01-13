package md.onemap.harta.geometry;

/**
 * Created by sergpank on 03.03.2015.
 */
public class BoundsXY
{
  private double xMin;
  private double xMax;
  private double yMin;
  private double yMax;

  public BoundsXY(double xMin, double yMin, double xMax, double yMax)
  {
    this.xMin = xMin;
    this.yMin = yMin;
    this.xMax = xMax;
    this.yMax = yMax;
  }

  public BoundsXY(XYPoint min, XYPoint max)
  {
    this.xMin = min.getX();
    this.yMin = min.getY();
    this.xMax = max.getX();
    this.yMax = max.getY();
  }

  /**
   * @param height - should be negative
   */
  public void liftUp(double height)
  {
    yMin += height;
    yMax += height;
  }

  public double getXmin()
  {
    return xMin;
  }

  public double getXmax()
  {
    return xMax;
  }

  public double getYmin()
  {
    return yMin;
  }

  public double getYmax()
  {
    return yMax;
  }

  public double getWidth()
  {
    return xMax - xMin;
  }

  public double getHeight()
  {
    return yMax - yMin;
  }

  @Override
  public String toString()
  {
    return "BoundsXY{" +
        "xMin=" + xMin +
        ", xMax=" + xMax +
        ", yMin=" + yMin +
        ", yMax=" + yMax +
        '}';
  }
}
