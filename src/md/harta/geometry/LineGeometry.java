package md.harta.geometry;

import md.harta.projector.AbstractProjector;

/**
 * Created by sergpank on 05.03.2015.
 */
public class LineGeometry {

  public static final double EQUATOR_LENGTH_METERS = 2.0 * Math.PI * AbstractProjector.EARTH_RADIUS_M;

  public static final double RADIANS_IN_METER = 1.0 / (AbstractProjector.EARTH_RADIUS_M);

  public static final double DEGREES_IN_METER = 360.0 / EQUATOR_LENGTH_METERS;

  /**
   * @param leftPoint Left point
   * @param rightPoint Right point
   * @return Line as coefficients for normal form of line equation Ax + By + C = 0
   */
  public static Line getLine(XYPoint leftPoint, XYPoint rightPoint){
    double a = leftPoint.getY() - rightPoint.getY();
    double b = rightPoint.getX() - leftPoint.getX();
    double c = leftPoint.getX() * rightPoint.getY() - rightPoint.getX() * leftPoint.getY();
    return new Line(a, b, c);
  }

  public static Line getPerpendicular(Line line, XYPoint point){
    double a = line.getB() * -1;
    double b = line.getA();
    double c = line.getB() * point.getX() - line.getA() * point.getY();
    return new Line(a, b, c);
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
      // Значит это вертикальная линия и тангенс угла наклона(90) = бесконечности
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
    double delta = Math.acos(Math.sin(latA) * Math.sin(latB) +
                             Math.cos(latA) * Math.cos(latB) * Math.cos(lonB - lonA));
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
}
