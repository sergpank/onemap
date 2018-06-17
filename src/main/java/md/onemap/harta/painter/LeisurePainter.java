package md.onemap.harta.painter;

import md.onemap.harta.drawer.AbstractDrawer;
import md.onemap.harta.geometry.BoundsXY;
import md.onemap.harta.geometry.CanvasPolygon;
import md.onemap.harta.osm.Leisure;
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


  public void drawParks(AbstractDrawer drawer, Collection<Leisure> leisures, int level)
  {
    drawer.setFillColor(Palette.PARK_COLOR);
    for (Leisure leisure : leisures) {
      if (leisure.isPark())
      {
        CanvasPolygon polygon = createPolygon(leisure);
        shiftPoints(bounds.getXmin(), polygon.getxPoints());
        shiftPoints(bounds.getYmin(), polygon.getyPoints());
        drawer.fillPolygon(polygon.getxPoints(), polygon.getyPoints());
      }
    }
  }
}
