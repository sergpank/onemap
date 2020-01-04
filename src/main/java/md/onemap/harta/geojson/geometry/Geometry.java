package md.onemap.harta.geojson.geometry;

public abstract class Geometry
{
  protected String type;

  Geometry(String type)
  {
    this.type = type;
  }

  public String getType()
  {
    return type;
  }
}
