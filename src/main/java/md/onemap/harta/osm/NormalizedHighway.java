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

  public NormalizedHighway(long id, String name, String nameRu, String nameOld)
  {
    this.id = id;
    this.name = name;
    this.nameRu = nameRu;
    this.nameOld = nameOld;
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
}
