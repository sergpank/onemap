package md.harta.tile;

import md.harta.drawer.AbstractDrawer;
import md.harta.drawer.TileDrawer;
import md.harta.geometry.BoundsXY;
import md.harta.osm.Building;
import md.harta.osm.Highway;
import md.harta.painter.BuildingPainter;
import md.harta.painter.HighwayPainter;
import md.harta.projector.AbstractProjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by sergpank on 04/12/2017.
 */
public class TileFileWriter
{
  private static final Logger LOG = LoggerFactory.getLogger(TileFileWriter.class);
  private int tileSize;
  private String outputDir;
  private String source;

  public TileFileWriter(int tileSize, String outputDir, String source)
  {
    this.tileSize = tileSize;
    this.outputDir = outputDir;
    this.source = new File(source).getName();
    LOG.info("Output directory: " + new File(outputDir, source).getAbsolutePath());
  }

  public void drawTile(int level, int x, int y, BoundsXY tileBounds, AbstractProjector projector,
                       Collection<Highway> highways, Collection<Building> buildings) {
    BufferedImage bi = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = bi.createGraphics();

    graphics.setPaint(TilePalette.BACKGROUND_COLOR);
    graphics.fillRect(0, 0, tileSize, tileSize);

    AbstractDrawer drawer = new TileDrawer(graphics);
    drawer.setAAEnabled(true);

    new HighwayPainter(projector, tileBounds).drawHighways(drawer, highways, level);
    new BuildingPainter(projector, tileBounds).drawBuildings(drawer, buildings, level);

    drawTileNumber(x, y, level, graphics);
    drawTileBorder(graphics);

    writeTile(bi, level, x, y);
  }

  private void writeTile(BufferedImage bi, int level, int x, int y)
  {
    try
    {
      String tileName = String.format("%s/%s/%s/tile_%d_%d_%d.png", outputDir, source, level, level, y, x);

      File tileFile = new File(tileName);
      tileFile.mkdirs();

      ImageIO.write(bi, "PNG", tileFile);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  private void drawTileNumber(int x, int y, int level, Graphics2D graphics)
  {
    String levelLabel = level + "";
    String xyLabel = String.format("(%d; %d)", x, y);

    Font font = new Font("Calibri", Font.BOLD, 14);
    FontMetrics fontMetrics = graphics.getFontMetrics(font);
//    graphics.setFont(font);
//    graphics.setColor(Color.WHITE);

    int levelWidth = fontMetrics.stringWidth(levelLabel);
    int xyWidth = fontMetrics.stringWidth(xyLabel);
    int h = fontMetrics.getHeight();

//    graphics.fillRect(128 - levelWidth / 2 - 4, 128 - h / 2 - 4, levelWidth + 8, h + 8);
//    graphics.fillRect(128 - xyWidth / 2 - 4, (128 + h / 2 + 4) + 4, xyWidth + 8, h + 8);

    graphics.setColor(Color.RED);
    graphics.drawString(levelLabel, 128 - levelWidth / 2, 128 + h / 2);
    graphics.drawString(xyLabel, 128 - xyWidth / 2, 128 + h / 2 * 3 + 8);
  }

  private void drawTileBorder(Graphics2D graphics)
  {
    graphics.drawLine(0,            0,            tileSize - 1, 0);
    graphics.drawLine(tileSize - 1, 0,            tileSize - 1, tileSize - 1);
    graphics.drawLine(tileSize - 1, tileSize - 1, 0,            tileSize - 1);
    graphics.drawLine(0,            tileSize - 1, 0,            0);
  }
}
