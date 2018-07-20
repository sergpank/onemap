package md.onemap.harta.export;

import md.onemap.harta.db.DatabaseCreator;
import md.onemap.harta.db.gis.NodeGisDao;
import md.onemap.harta.db.gis.RelationGisDao;
import md.onemap.harta.db.gis.WayGisDao;
import md.onemap.harta.loader.OsmLoader;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsmToPostgisExporter extends OsmExporter
{
  private static final Logger LOG = LoggerFactory.getLogger(OsmToPostgisExporter.class);

  public static void main(String[] args)
  {
    DOMConfigurator.configure("log4j.xml");
    new OsmToPostgisExporter().export();
  }

  @Override
  protected void createdDb(String dbName)
  {
    DatabaseCreator.createGisDb(dbName);
  }

  @Override
  protected void exportEntities()
  {
    OsmLoader osmLoader = new OsmLoader();
    LOG.info("\n\nReading {} ...", osmFile);
    osmLoader.load(osmFile);

    LOG.info("\n\nSaving nodes ...");
    new NodeGisDao().saveAll(osmLoader.getNodes().values());

    LOG.info("\n\nSaving ways ...");
    new WayGisDao().saveAll(osmLoader.getWays().values());

    LOG.info("\n\nSaving relations ...");
    new RelationGisDao(osmLoader).saveAll(osmLoader.getRelations().values());
    }

  @Override
  protected void normalizeHighwayNames()
  {
    LOG.info("Normalizing highway names ... NOT IMPLEMENTED");
    //Collection<Highway> highways = new HighwayGisDao().loadAll();
    //new HighwayNameNormalizer().normalize(highways);
  }
}
