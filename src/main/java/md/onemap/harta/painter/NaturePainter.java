package md.onemap.harta.painter;

import md.onemap.harta.drawer.AbstractDrawer;
import md.onemap.harta.geometry.BoundsXY;
import md.onemap.harta.geometry.CanvasPolygon;
import md.onemap.harta.osm.Natural;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.tile.TilePalette;

import java.util.Collection;

/**
 * Created by serg on 6/29/16.
 */
public class NaturePainter extends AbstractPainter
{
  public NaturePainter(AbstractProjector projector, BoundsXY bounds)
  {
    super(projector, bounds);
  }

  public void drawWater(AbstractDrawer drawer, Collection<Natural> nature, int level)
  {
    drawer.setFillColor(TilePalette.WATER_COLOR);
    for (Natural natural : nature)
    {
      if (natural.isWater())
      {
        CanvasPolygon polygon = createPolygon(natural);
        shiftPoints(bounds.getXmin(), polygon.getxPoints());
        shiftPoints(bounds.getYmin(), polygon.getyPoints());
        drawer.fillPolygon(polygon.getxPoints(), polygon.getyPoints());
      }
    }
  }
}
