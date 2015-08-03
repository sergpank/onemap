package md.harta.projector;

import md.harta.geometry.LatLonPoint;
import md.harta.geometry.XYPoint;
import md.harta.util.ScaleCalculator;

/**
 * x = R · λ;
 * y = R · ln(tg(π/4 + φ/2), где R — радиус сферы, λ — долгота в радианах, φ — широта в радианах.
 *
 * Created by sergpank on 22.02.2015.
 */
public class MercatorProjector extends AbstractProjector {

  public static final double LEVEL_1_RADIUS = 40.74366543152521;
  public static final double MAX_LAT = 85.05112877980659;

  private double radius;
  private double maxLat;

  public MercatorProjector(int level)
  {
    this.radius = ScaleCalculator.getRadiusForLevel(level);
    this.maxLat = MAX_LAT;

    width = radius * Math.toRadians(MAX_LON) * 2;
    height = radius * Math.log(Math.tan(Math.PI / 4 + Math.toRadians(maxLat)/2)) * 2;
  }

  public MercatorProjector(double radius, double maxLat) {
    this.radius = radius;
    this.maxLat = maxLat;
    width = radius * Math.toRadians(MAX_LON) * 2;
    height = radius * Math.log(Math.tan(Math.PI / 4 + Math.toRadians(maxLat)/2)) * 2;
  }

  @Override
  public XYPoint getXY(double lat, double lon) {
    double x = width / 2 + radius * Math.toRadians(lon);
    double y;
    if (lat <= maxLat) {
      y = height / 2 + radius * Math.log(Math.tan(Math.PI / 4 + Math.toRadians(lat)/2)) * -1;
    } else {
      y = height;
    }
    return new XYPoint(x, y);
  }

  @Override
  public LatLonPoint getLatLon(double x, double y) {
    double lat = Math.toDegrees(Math.atan(Math.sinh((y - height /2) / radius))) * -1;
    double lon = Math.toDegrees((x - (getWidth() / 2))/radius);

    return new LatLonPoint(lat, lon);
  }

  @Override
  public double getScale(LatLonPoint point) {
    // Scale = sec(latitude)
    // sec = 1 / cos
    return 1 / Math.cos(Math.toRadians(point.getLat()));
  }

  public static void main(String[] args) {
//    256 = radius * Math.toRadians(MAX_LON) * 2;
//    256 = radius * Math.log(Math.tan(Math.PI / 4 + Math.toRadians(maxLat)/2)) * 2;
    double radius = 256.0 / (Math.toRadians(MAX_LON) * 2);
    double maxLat = Math.toDegrees((Math.atan(Math.exp(256.0 / (radius * 2))) - (Math.PI / 4)) * 2);
    System.out.println(radius);
    System.out.println(maxLat);
    for (int i = 0; i <= 20; i++)
    {
      double r = LEVEL_1_RADIUS * Math.pow(2, i);
      MercatorProjector projector = new MercatorProjector(r, MAX_LAT);
      System.out.printf("%d :: Width = %f; Height = %f; Radius = %f\n", i, projector.getWidth(), projector.getHeight(), r);
    }
  }
}
