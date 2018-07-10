package md.onemap.harta.export;

import md.onemap.harta.properties.Props;

public abstract class OsmExporter
{
  protected String osmFile;
  protected String dbName;

  public OsmExporter()
  {
    this.osmFile = Props.osmFile();
    this.dbName = Props.dbName();
  }

  public void export()
  {
    createdDb(Props.dbName());
    exportEntities();
//    normalizeHighwayNames();
  }

  protected abstract void createdDb(String dbName);

  protected abstract void exportEntities();

  protected abstract void normalizeHighwayNames();
}
