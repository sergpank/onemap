package md.onemap.harta.geojson.geometry;

public class Polygon extends Geometry
{
  public static final String TYPE = "Polygon";
  public double [][][] geometry;

  Polygon()
  {
    super(TYPE);
  }
}
