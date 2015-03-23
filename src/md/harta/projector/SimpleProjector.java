package md.harta.projector;

import md.harta.geometry.LatLonPoint;
import md.harta.geometry.XYPoint;

/**
 * Created by sergpank on 21.02.2015.
 */
public class SimpleProjector extends AbstractProjector {

  public SimpleProjector(double scale) {
    this.scale = scale;
    height = MAX_LAT * 2 * scale;
    width = MAX_LON * 2 * scale;
  }

  @Override
  public XYPoint getXY(double lat, double lon) {
    double x = width / 2 + lon * scale;
    double y = (lat >= 0) ? (height / 2 - lat * scale) : (height / 2 + Math.abs(lat * scale));
    return new XYPoint(x + shift, y);
  }

  @Override
  public LatLonPoint getLatLon(double x, double y) {
    double lat = y > height / 2 ? (-(y - height / 2) / scale) : (height / 2 - y) / scale;
    double lon = (x - width / 2) / scale;
    return new LatLonPoint(lat, lon);
  }

  @Override
  public double getScale(LatLonPoint point) {
    return 0;
  }
}
