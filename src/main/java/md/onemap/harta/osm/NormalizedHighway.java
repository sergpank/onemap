package md.onemap.harta.osm;

/**
 * Light Highway entity
 * Contains only ID, and simplified names (without specific romanian and russian letters)
 */
public class NormalizedHighway
{
  private long id;
  private String name;
  private String nameRu;
  private String nameOld;
  private String geoJSON;

  public NormalizedHighway(long id, String name, String nameRu, String nameOld, String geoJSON)
  {
    this.id = id;
    this.name = name;
    this.nameRu = nameRu;
    this.nameOld = nameOld;
    this.geoJSON = geoJSON;
  }

  public long getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  public String getNameRu()
  {
    return nameRu;
  }

  public String getNameOld()
  {
    return nameOld;
  }

  public String getGeoJSON() {
    return geoJSON;
  }

  @Override
  public String toString()
  {
    return "NormalizedHighway{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", nameRu='" + nameRu + '\'' +
        ", nameOld='" + nameOld + '\'' +
        ", geoJSON='" + geoJSON + '\'' +
        '}';
  }
}
