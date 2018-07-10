package md.onemap.harta.tile;

import md.onemap.harta.db.gis.WayGisDao;
import md.onemap.harta.drawer.AwtDrawer;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.geometry.BoundsXY;
import md.onemap.harta.loader.AbstractLoader;
import md.onemap.harta.osm.Building;
import md.onemap.harta.osm.Highway;
import md.onemap.harta.osm.Way;
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
                                AbstractLoader loader, BoundsLatLon tileBounds)
  {
    BufferedImage bi = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
    AwtDrawer drawer = new AwtDrawer(createGraphics(bi));

    Collection<Way> tileData = new WayGisDao().load(level, tileBounds);

    Set<Way> highways = tileData
        .stream()
        .filter(w -> w.getType() != null && w.getType().equals(Highway.HIGHWAY))
        .collect(Collectors.toSet());

    Set<Way> buildings = tileData
        .stream()
        .filter(w -> w.getType() != null && w.getType().equals(Building.BUILDING))
        .collect(Collectors.toSet());

    BoundsXY boundsXY = tileBounds.toXY(projector);

//    new LandusePainter(projector,  boundsXY).draw(drawer, landuse, level);
//    new LeisurePainter(projector,  boundsXY).draw(drawer, leisure, level);
//    new WaterwayPainter(projector, boundsXY).draw(drawer, waterways, level);
//    new NaturePainter(projector, boundsXY).draw(drawer, nature, level);

    new BuildingPainter(projector, boundsXY).drawBuildings(drawer, buildings, level);
    new HighwayPainter(projector, boundsXY).drawHighways(drawer, highways, level);



    return bi;
  }
}
