package md.onemap.harta.tile;

import md.onemap.harta.drawer.AbstractDrawer;
import md.onemap.harta.drawer.AwtDrawer;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.geometry.BoundsXY;
import md.onemap.harta.loader.AbstractLoader;
import md.onemap.harta.osm.*;
import md.onemap.harta.painter.*;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.properties.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;

/**
 * Created by sergpank on 04/12/2017.
 */
public class CategorizedTileDrawer extends AbstractTileDrawer
{
  private static final Logger LOG = LoggerFactory.getLogger(CategorizedTileDrawer.class);


  public CategorizedTileDrawer(int tileSize)
  {
    super(tileSize);
  }

  @Override
  public BufferedImage drawTile(int level, int x, int y, AbstractProjector projector,
                                AbstractLoader loader, BoundsLatLon tileBounds)
  {
    BufferedImage bi = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = createGraphics(bi);

    AbstractDrawer drawer = new AwtDrawer(graphics);

    BoundsXY boundsXY = tileBounds.toXY(projector);

    Collection<Waterway> waterways = loader.getWaterways(level, tileBounds);
    Collection<Natural> nature = loader.getNature(level, tileBounds);
    Collection<Highway> highways = loader.getHighways(level, tileBounds);
    Collection<Building> buildings = loader.getBuildings(level, tileBounds);
    Collection<Leisure> leisure = loader.getLeisure(level, tileBounds);
    Collection<Landuse> landuse = loader.getLanduse(level, tileBounds);

    new LandusePainter(projector,  boundsXY).draw(drawer, landuse, level);
    new LeisurePainter(projector,  boundsXY).draw(drawer, leisure, level);
    new WaterwayPainter(projector, boundsXY).draw(drawer, waterways, level);
    new NaturePainter(projector, boundsXY).draw(drawer, nature, level);
    new BuildingPainter(projector, boundsXY).draw(drawer, buildings, level);
    new HighwayPainter(projector,  boundsXY).draw(drawer, highways, level);

    if (Props.debugTileNumber())
    {
      drawTileNumber(x, y, level, graphics);
    }

    if (Props.debugTileBorder())
    {
      drawTileBorder(graphics);
    }

    return bi;
  }
}
