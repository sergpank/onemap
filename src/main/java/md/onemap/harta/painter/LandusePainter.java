package md.onemap.harta.painter;

import md.onemap.harta.db.gis.entity.Way;
import md.onemap.harta.drawer.AbstractDrawer;
import md.onemap.harta.geometry.BoundsXY;
import md.onemap.harta.geometry.CanvasPolygon;
import md.onemap.harta.osm.Landuse;
import md.onemap.harta.osm.Water;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.tile.Palette;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

/**
 * Created by serg on 08-Aug-16.
 */
public class LandusePainter extends AbstractPainter{

  private static Logger log = LogManager.getLogger();

  public LandusePainter(AbstractProjector projector, BoundsXY bounds) {
    super(projector, bounds);
  }

  public void draw(AbstractDrawer drawer, Collection<Landuse> landuse, int level)
  {
    for (Landuse land : landuse) {
      if (LanduseGreen.isGreen(land.getType()))
      {
        drawer.setFillColor(Palette.PARK_COLOR);
        CanvasPolygon polygon = createPolygon(land.getNodes());
        shiftPolygon(polygon);
        drawer.fillPolygon(polygon.getxPoints(), polygon.getyPoints());
      }
      else if (Water.isWater(land.getType()))
      {
        drawer.setFillColor(Palette.WATER_COLOR);
        CanvasPolygon polygon = createPolygon(land.getNodes());
        shiftPolygon(polygon);
        drawer.fillPolygon(polygon.getxPoints(), polygon.getyPoints());
      }
    }
  }

  public void drawLanduse(AbstractDrawer drawer, Collection<Way> landuse, int level)
  {
    for (Way land : landuse) {
      String type = land.getTags().get(Landuse.LANDUSE);
      if (LanduseGreen.isGreen(type))
      {
        drawer.setFillColor(Palette.PARK_COLOR);
        CanvasPolygon polygon = createPolygon(land.getNodes());
        shiftPolygon(polygon);
        drawer.fillPolygon(polygon.getxPoints(), polygon.getyPoints());
      }
      else if (Water.isWater(type))
      {
        drawer.setFillColor(Palette.WATER_COLOR);
        CanvasPolygon polygon = createPolygon(land.getNodes());
        shiftPolygon(polygon);
        drawer.fillPolygon(polygon.getxPoints(), polygon.getyPoints());
      }
    }
  }
}
