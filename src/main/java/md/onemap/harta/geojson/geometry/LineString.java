package md.onemap.harta.geojson.geometry;

public class LineString extends Geometry
{
  public static final String TYPE = "LineString";
  public double [][] geometry;

  public LineString()
  {
    super(TYPE);
  }
}
