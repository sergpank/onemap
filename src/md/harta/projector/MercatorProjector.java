package md.harta.projector;

import md.harta.geometry.LatLonPoint;
import md.harta.geometry.XYPoint;

/**
 * x = R · λ;
 * y = R · ln(tg(π/4 + φ/2), где R — радиус сферы, λ — долгота в радианах, φ — широта в радианах.
 *
 * Created by sergpank on 22.02.2015.
 */
public class MercatorProjector extends AbstractProjector {

  private double radius;
  private double maxLat;

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
    MercatorProjector projector = new MercatorProjector(100, 85);
    for (int i = 0; i <= 85; i++){
      System.out.println(String.format("%d - %f", i, projector.getScale(new LatLonPoint(i, 0))));
    }
  }
}
