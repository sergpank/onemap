package md.harta.tile;

import md.harta.db.DbHelper;
import md.harta.db.dao.NodeDao;
import md.harta.geometry.BoundsLatLon;
import md.harta.loader.AbstractLoader;
import md.harta.loader.PostgresLoader;
import md.harta.osm.Building;
import md.harta.osm.Highway;
import md.harta.projector.AbstractProjector;
import md.harta.projector.MercatorProjector;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Created by sergpank on 23.05.15.
 */
public class TileGenerator
{
  public static final int TILE_SIZE = 256;

  private static Logger LOG = LoggerFactory.getLogger(TileGenerator.class);

  private GeneratorProperties props = new GeneratorProperties("tile-generator-properties/generate-from-db.properties");

  public static void main(String[] args)
  {
    DOMConfigurator.configure("log4j.xml");
    //    PropertyConfigurator.configure("log4j.properties");

    new TileGenerator().generate();
  }

  public void generate()
  {
    File tilesFolder = new File(props.outputDir(), props.dbName());
    tilesFolder.mkdirs();

//    AbstractLoader loader = new PostgisLoader(database);
    AbstractLoader loader = new PostgresLoader(props.dbName());
//    AbstractLoader loader = new OsmLoader();
//    loader.load("osm/botanica.osm", null);

    NodeDao nodeDao = new NodeDao(DbHelper.getConnection(props.dbName()));
    BoundsLatLon bounds = nodeDao.getBounds();
//    BoundsLatLon bounds = loader.getBounds();
    System.out.println(bounds);

    LocalDateTime generationStart = LocalDateTime.now();

    generateLevel(tilesFolder, loader, bounds);

    LOG.info("{} seconds", Duration.between(generationStart, LocalDateTime.now()).getSeconds());
  }

  private void generateLevel(File tilesFolder, AbstractLoader loader, BoundsLatLon bounds)
  {
    for (int level = props.startLevel(); level <= props.endLevel(); level++)
    {
      new File(tilesFolder, Integer.toString(level)).mkdirs();

      AbstractProjector projector = new MercatorProjector(level);

      TileCutter tileCutter = new TileCutter(projector, TILE_SIZE, level, bounds);
      tileCutter.cut();

      generateLevelTiles(loader, level, projector, tileCutter);
    }
  }

  private void generateLevelTiles(AbstractLoader loader, int level, AbstractProjector projector, TileCutter tileCutter)
  {
    long tileCnt = 0;
    long start = System.currentTimeMillis();
    TileFileWriter tileWriter = new TileFileWriter(TILE_SIZE, props.outputDir(), props.dbName());

    for (int y = tileCutter.getMinTileYindex(); y <= tileCutter.getMaxTileYindex(); y++)
    {
      for (int x = tileCutter.getMinTileXindex(); x <= tileCutter.getMaxTileXindex(); x++)
      {
        BoundsLatLon tileBounds = tileCutter.getTileBounds(x, y, 0);

        Collection<Highway> highways = loader.getHighways(level, tileBounds);
        Collection<Building> buildings = loader.getBuildings(level, tileBounds);

        tileWriter.drawTile(level, x, y, tileBounds.toXY(projector), projector, highways, buildings);

        tileCnt++;
      }
    }

    long end = System.currentTimeMillis();
    LOG.info("level {} -> {} ms; {} tiles; {} ms per tile", level, (end - start), tileCnt, (end - start) / tileCnt);
  }
}
