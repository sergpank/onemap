package md.harta.geometry;

/**
 * Created by sergpank on 01.03.2015.
 */
public class CanvasPolygon{
  long id;
  double[] xPoints;
  double[] yPoints;
  int pointsNumber;

  public CanvasPolygon(long id, double[] xPoints, double[] yPoints) {
    if (xPoints.length != yPoints.length){
      throw new RuntimeException(
          String.format("xPoints = %d; yPoints = %d", xPoints.length, yPoints.length));
    }
    this.pointsNumber = xPoints.length;
    this.id = id;
    this.xPoints = xPoints;
    this.yPoints = yPoints;
  }

  public long getId() {
    return id;
  }

  public double[] getxPoints() {
    return xPoints;
  }

  public double[] getyPoints() {
    return yPoints;
  }

  public int getPointsNumber() {
    return pointsNumber;
  }
}
