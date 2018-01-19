package md.onemap.harta.tile;

import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.geometry.BoundsXY;
import md.onemap.harta.loader.AbstractLoader;
import md.onemap.harta.osm.Building;
import md.onemap.harta.osm.Highway;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.projector.MercatorProjector;
import md.onemap.harta.util.Stopwatch;
import md.onemap.harta.util.TimePrettyPrint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by sergpank on 23.05.15.
 */
public abstract class TileGenerator
{
  private static Logger LOG = LoggerFactory.getLogger(TileGenerator.class);

  protected GeneratorProperties props;
  protected AbstractLoader loader;

  public TileGenerator(GeneratorProperties properties, AbstractLoader loader)
  {
    this.props = properties;
    this.loader = loader;
  }

  public abstract void generate();

  protected void generateLevels()
  {
    LOG.info("Output directory: {}", new File(props.outputDir()).getAbsolutePath());

    for (int level = props.startLevel(); level <= props.endLevel(); level++)
    {
      AbstractProjector projector = new MercatorProjector(level);

      TileCutter tileCutter = new TileCutter(projector, props.tileSize(), level, loader.getBounds());
      tileCutter.cut();

      generateLevelTiles(level, projector, tileCutter);
    }
  }

  protected void generateLevelTiles(int level, AbstractProjector projector, TileCutter tileCutter)
  {
    long tileCnt = 0;
    long start = System.currentTimeMillis();
    double totalTiles = (tileCutter.getMaxTileXindex() - tileCutter.getMinTileXindex() + 1)
        * (tileCutter.getMaxTileYindex() - tileCutter.getMinTileYindex() + 1);

    short logStep = 10;
    long logTileStep = (long) totalTiles / logStep; // 10%

    for (int y = tileCutter.getMinTileYindex(); y <= tileCutter.getMaxTileYindex(); y++)
    {
      for (int x = tileCutter.getMinTileXindex(); x <= tileCutter.getMaxTileXindex(); x++)
      {
        BufferedImage tile = generateTile(x, y, level, projector, tileCutter.getTileBounds(x, y, 0));
        writeTile(tile, level, x, y);

        tileCnt++;
        if (logTileStep > 0 && tileCnt % logTileStep == 0)
        {
          LOG.info(String.format("level %2d -> %3.2f %%; %d of %.0f tiles", level, tileCnt / totalTiles * 100, tileCnt, totalTiles));
        }
      }
    }

    long end = System.currentTimeMillis();
    LOG.info("level {} -> {}; {} tiles; {} ms per tile", level, TimePrettyPrint.prettyPrint(end - start), tileCnt, (end - start) / tileCnt);
  }

  public BufferedImage generateTile(int x, int y, int level, AbstractProjector projector, BoundsLatLon tileBounds)
  {
    Collection<Highway> highways = loader.getHighways(level, tileBounds);
    Collection<Building> buildings = loader.getBuildings(level, tileBounds);

    TileDrawer tileDrawer = new TileDrawer(props.tileSize());
    BoundsXY boundsXY = tileBounds.toXY(projector);
    BufferedImage tile = tileDrawer.drawTile(level, x, y, boundsXY, projector, highways, buildings);

    return tile;
  }

  private void writeTile(BufferedImage bi, int level, int x, int y)
  {
    try
    {
      String tileName = String.format("%s/%s/tile_%d_%d_%d.png", props.outputDir(), level, level, y, x);

      File tileFile = new File(tileName);
      tileFile.mkdirs();

      ImageIO.write(bi, "PNG", tileFile);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
