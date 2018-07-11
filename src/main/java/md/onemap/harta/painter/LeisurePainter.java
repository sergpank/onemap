package md.onemap.harta.painter;

import md.onemap.harta.drawer.AbstractDrawer;
import md.onemap.harta.geometry.BoundsXY;
import md.onemap.harta.geometry.CanvasPolygon;
import md.onemap.harta.osm.Leisure;
import md.onemap.harta.osm.Water;
import md.onemap.harta.osm.Way;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.tile.Palette;

import java.util.Collection;

/**
 * Created by serg on 6/29/16.
 */
public class LeisurePainter extends AbstractPainter
{
  public LeisurePainter(AbstractProjector projector, BoundsXY bounds)
  {
    super(projector, bounds);
  }

  public void draw(AbstractDrawer drawer, Collection<Leisure> leisures, int level)
  {
    for (Leisure leisure : leisures) {
      if (!Water.isWater(leisure.getType()))
      {
        drawer.setFillColor(Palette.PARK_COLOR);
      }
      else
      {
        drawer.setFillColor(Palette.WATER_COLOR);
      }
      CanvasPolygon polygon = createPolygon(leisure.getNodes());
      shiftPoints(bounds.getXmin(), polygon.getxPoints());
      shiftPoints(bounds.getYmin(), polygon.getyPoints());
      drawer.fillPolygon(polygon.getxPoints(), polygon.getyPoints());
    }
  }

  public void drawLeisure(AbstractDrawer drawer, Collection<Way> leisures, int level)
  {
    for (Way leisure : leisures) {
      if (!Water.isWater(leisure.getTags().get(Leisure.LEISURE)))
      {
        drawer.setFillColor(Palette.PARK_COLOR);
      }
      else
      {
        drawer.setFillColor(Palette.WATER_COLOR);
      }
      CanvasPolygon polygon = createPolygon(leisure.getNodes());
      shiftPoints(bounds.getXmin(), polygon.getxPoints());
      shiftPoints(bounds.getYmin(), polygon.getyPoints());
      drawer.fillPolygon(polygon.getxPoints(), polygon.getyPoints());
    }
  }
}
