package md.onemap.harta.tile;

import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.geometry.BoundsXY;
import md.onemap.harta.loader.AbstractLoader;
import md.onemap.harta.osm.Building;
import md.onemap.harta.osm.Highway;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.projector.MercatorProjector;
import md.onemap.harta.properties.Props;
import md.onemap.harta.util.TimePrettyPrint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sergpank on 23.05.15.
 */
public abstract class TileGenerator
{
  private static Logger LOG = LoggerFactory.getLogger(TileGenerator.class);
  private Map<Integer, TileBoundsCalculator> tileBoundsCache = new HashMap<>();

  protected AbstractLoader loader;

  private final String outputDir;
  private final int startLevel;
  private final int endLevel;
  private final int tileSize;

  public TileGenerator(AbstractLoader loader)
  {
    this.outputDir = Props.outputDir();
    this.startLevel = Props.startLevel();
    this.endLevel = Props.endLevel();
    this.tileSize = Props.tileSize();
    this.loader = loader;
  }

  public abstract void generate();

  protected void generateLevels()
  {
    LOG.info("Output directory: {}", new File(outputDir).getAbsolutePath());

    for (int level = startLevel; level <= endLevel; level++)
    {
      AbstractProjector projector = new MercatorProjector(level);

      TileCutter tileCutter = new TileCutter(projector, tileSize, level, loader.getBounds());
      tileCutter.cut();

      generateLevelTiles(level, projector, tileCutter);
    }
  }

  private void generateLevelTiles(int level, AbstractProjector projector, TileCutter tileCutter)
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
        BufferedImage tile = generateTile(x, y, level, projector, tileCutter.getTileBounds(x, y));
        writeTile(tile, level, x, y, outputDir);

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

    TileDrawer tileDrawer = new TileDrawer(tileSize);
    BoundsXY boundsXY = tileBounds.toXY(projector);
    BufferedImage tile = tileDrawer.drawTile(level, x, y, boundsXY, projector, highways, buildings);

    return tile;
  }

  public BufferedImage generateTileCached(int x, int y, int level)
  {
    BufferedImage tile = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
    File tileFile = null;

    if (Props.cacheEnabled())
    {
      String tileName = String.format("%s/%s/tile_%d_%d_%d.png", Props.cacheDir(), level, level, y, x);
      tileFile = new File(tileName);
      LOG.info("Look for cache @: {}", tileFile.getAbsolutePath());
    }

    if (tileFile != null && tileFile.exists())
    {
      LOG.info("Getting tile from cache: {}", tileFile.getAbsolutePath());
      try
      {
        tile = ImageIO.read(tileFile);
      }
      catch (IOException e)
      {
        LOG.error("Unable to read file: {}", tileFile.getAbsolutePath());
      }
    }
    else
    {
      TileBoundsCalculator boundsCalculator = getTileBoundsCalculator(level);
      BoundsLatLon tileBounds = boundsCalculator.getTileBounds(x, y);

      tile = generateTile(x, y, level, boundsCalculator.getProjector(), tileBounds);
      if (Props.cacheEnabled())
      {
        writeTile(tile, level, x, y, Props.cacheDir());
      }
    }

    return tile;
  }

  private void writeTile(BufferedImage bi, int level, int x, int y, String outputDir)
  {
    try
    {
      String tileName = String.format("%s/%s/tile_%d_%d_%d.png", outputDir, level, level, y, x);
      LOG.info("Generating tile: {}", tileName);

      File tileFile = new File(tileName);
      tileFile.mkdirs();

      ImageIO.write(bi, "PNG", tileFile);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  private synchronized TileBoundsCalculator getTileBoundsCalculator(int level)
  {
    TileBoundsCalculator calculator = tileBoundsCache.get(level);
    if (calculator == null)
    {
      LOG.info("New Bounds Calculator for level {}", level);
      AbstractProjector projector = new MercatorProjector(level);
      calculator = new TileBoundsCalculator(Props.tileSize(), projector);
      tileBoundsCache.put(level, calculator);
    }
    return calculator;
  }
}
