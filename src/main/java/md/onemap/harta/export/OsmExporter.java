package md.onemap.harta.export;

import md.onemap.harta.db.DatabaseCreator;
import md.onemap.harta.properties.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class OsmExporter
{
  private static final Logger LOG = LoggerFactory.getLogger(OsmExporter.class);

  protected String osmFile;
  protected String dbName;

  public OsmExporter()
  {
    this.osmFile = Props.osmFile();
    this.dbName = Props.dbName();
  }

  public void export()
  {
    DatabaseCreator.createDb(Props.dbName());
    exportEntities();
    normalizeHighwayNames();
  }

  protected abstract void exportEntities();

  protected abstract void normalizeHighwayNames();
}
