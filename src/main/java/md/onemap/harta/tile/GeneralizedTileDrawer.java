package md.onemap.harta.tile;

import md.onemap.harta.db.gis.WayGisDao;
import md.onemap.harta.db.gis.entity.Way;
import md.onemap.harta.drawer.AwtDrawer;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.geometry.BoundsXY;
import md.onemap.harta.loader.AbstractLoader;
import md.onemap.harta.osm.*;
import md.onemap.harta.painter.*;
import md.onemap.harta.projector.AbstractProjector;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class GeneralizedTileDrawer extends AbstractTileDrawer
{
  public GeneralizedTileDrawer(int tileSize)
  {
    super(tileSize);
  }

  @Override
  public BufferedImage drawTile(int level, int x, int y, AbstractProjector projector,
                                AbstractLoader loadekr, BoundsLatLon tileBounds)
  {
    BufferedImage bi = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
    AwtDrawer drawer = new AwtDrawer(createGraphics(bi));

    Collection<Way> tileData = new WayGisDao().load(level, tileBounds);

    Set<Way> highways = getWays(tileData, Highway.HIGHWAY);
    Set<Way> buildings = getWays(tileData, Building.BUILDING);
    Set<Way> landuse = getWays(tileData, Landuse.LANDUSE);
    Set<Way> leisure = getWays(tileData, Leisure.LEISURE);
    Set<Way> waterways = getWays(tileData, Waterway.WATERWAY);
    Set<Way> nature = getWays(tileData, Natural.NATURAL);

    BoundsXY boundsXY = tileBounds.toXY(projector);

    new LandusePainter(projector,  boundsXY).drawLanduse(drawer, landuse, level);
    new LeisurePainter(projector,  boundsXY).drawLeisure(drawer, leisure, level);
    new WaterwayPainter(projector, boundsXY).drawWaterways(drawer, waterways, level);
    new NaturePainter(projector, boundsXY).drawNatural(drawer, nature, level);

    new BuildingPainter(projector, boundsXY).drawBuildings(drawer, buildings, level);
    new HighwayPainter(projector, boundsXY).drawHighways(drawer, highways, level);

    return bi;
  }

  private Set<Way> getWays(Collection<Way> tileData, String highway)
  {
    return tileData
        .stream()
        .filter(w -> w.getType() != null && w.getType().equals(highway))
        .collect(Collectors.toSet());
  }
}
