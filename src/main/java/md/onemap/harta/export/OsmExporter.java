package md.onemap.harta.export;

import md.onemap.harta.db.DatabaseCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static md.onemap.harta.properties.OsmExporterProperties.getDbName;

public abstract class OsmExporter
{
  private static final Logger LOG = LoggerFactory.getLogger(OsmExporter.class);

  public void export()
  {
    DatabaseCreator.createDb(getDbName());
    exportEntities();
    normalizeHighwayNames();
  }

  protected abstract void exportEntities();

  protected abstract void normalizeHighwayNames();
}
