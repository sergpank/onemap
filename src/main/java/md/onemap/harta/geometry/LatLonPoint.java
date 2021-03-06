package md.onemap.harta.geometry;

/**
 * Created by sergpank on 20.02.2015.
 */
public class LatLonPoint {
  private double lat;
  private double lon;

  /**
   * @param lat широта
   * @param lon долгота
   */
  public LatLonPoint(double lat, double lon) {
    this.lat = lat;
    this.lon = lon;
  }

  /**
   * @return Latitude (Широту)
   */
  public double getLat() {
    return lat;
  }

  /**
   * @return Longitude (Долготу)
   */
  public double getLon() {
    return lon;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    LatLonPoint point = (LatLonPoint) o;

    if (Double.compare(point.lat, lat) != 0) return false;
    if (Double.compare(point.lon, lon) != 0) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(lat);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(lon);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "Point{" +
        "lon=" + lon +
        ", lat=" + lat +
        '}';
  }
}
