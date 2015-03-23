package md.harta.projector;

import md.harta.geometry.LatLonPoint;
import md.harta.geometry.XYPoint;

/**
 * Created by sergpank on 20.02.2015.
 */
public class CylindricalProjector extends AbstractProjector {
  private double radius;
  private double maxLat;

  public CylindricalProjector(double scale, double maxLat) {
    if (maxLat >= MAX_LAT || maxLat <= 0){
      // tan(90) = INFINITY
      throw new IllegalArgumentException("Max latitude should be less that 90 degrees");
    }
    this.scale = scale;
    this.maxLat = maxLat;
    this.width = MAX_LON * 2 * scale;
    radius = width / (2 * Math.PI); // L = 2 * pi * R
    height = radius * Math.tan(Math.toRadians(maxLat)) * 2;
  }

  @Override
  public XYPoint getXY(double lat, double lon) {
    double x = width / 2 + lon * scale;
    double y;
    if (Math.abs(lat) >= maxLat){
      y = lat > 0 ? 0 : height;
    } else{
       y = height / 2 + (
           (lat > 0)
               ? (-radius * Math.tan(Math.toRadians(lat)))
               : (radius * Math.abs(Math.tan(Math.toRadians(lat))))
       );
    }
    return new XYPoint(x, y);
  }

  @Override
  public LatLonPoint getLatLon(double x, double y) {
    double lon = (x - width / 2) / scale;
    double lat = Math.toDegrees(Math.atan(((height / 2) - y) / radius));
    return new LatLonPoint(lat, lon);
  }

  @Override
  public double getScale(LatLonPoint point) {
    return 0;
  }

  public static void main(String[] args) {
    for (int i = 90; i >= -90; i--){
      System.out.println(i + " :: " + Math.tan(Math.toRadians(i)));
    }
  }
}
