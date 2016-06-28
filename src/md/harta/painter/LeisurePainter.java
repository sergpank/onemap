package md.harta.painter;

import md.harta.drawer.AbstractDrawer;
import md.harta.drawer.TileDrawer;
import md.harta.geometry.Bounds;
import md.harta.geometry.CanvasPolygon;
import md.harta.osm.Building;
import md.harta.osm.Leisure;
import md.harta.projector.AbstractProjector;
import md.harta.tile.TilePalette;

import java.util.Collection;

/**
 * Created by serg on 6/29/16.
 */
public class LeisurePainter extends AbstractPainter
{
  public LeisurePainter(AbstractProjector projector, Bounds bounds)
  {
    super(projector, bounds);
  }


  public void drawParks(AbstractDrawer drawer, Collection<Leisure> leisures, int level)
  {
    drawer.setFillColor(TilePalette.PARK_COLOR);
    for (Leisure leisure : leisures) {
      if (leisure.isPark())
      {
        CanvasPolygon polygon = createPolygon(leisure);
        shiftPoints(bounds.getxMin(), polygon.getxPoints());
        shiftPoints(bounds.getyMin(), polygon.getyPoints());
        drawer.fillPolygon(polygon.getxPoints(), polygon.getyPoints());
      }
    }
  }
}
