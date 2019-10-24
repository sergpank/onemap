package md.onemap.harta.geojson.geometry;

public class Point extends Geometry
{
  public static final String TYPE = "Point";
  public double[] coordinates;

  Point()
  {
    super(TYPE);
  }
}
