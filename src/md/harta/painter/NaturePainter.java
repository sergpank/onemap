package md.harta.painter;

import md.harta.drawer.AbstractDrawer;
import md.harta.geometry.Bounds;
import md.harta.geometry.CanvasPolygon;
import md.harta.osm.Natural;
import md.harta.projector.AbstractProjector;
import md.harta.tile.TilePalette;

import java.util.Collection;

/**
 * Created by serg on 6/29/16.
 */
public class NaturePainter extends AbstractPainter
{
  public NaturePainter(AbstractProjector projector, Bounds bounds)
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
        shiftPoints(bounds.getxMin(), polygon.getxPoints());
        shiftPoints(bounds.getyMin(), polygon.getyPoints());
        drawer.fillPolygon(polygon.getxPoints(), polygon.getyPoints());
      }
    }
  }
}
