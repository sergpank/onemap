package md.onemap.harta.painter;

import md.onemap.harta.drawer.TileDrawer;
import md.onemap.harta.geometry.BoundsXY;
import md.onemap.harta.geometry.CanvasPolygon;
import md.onemap.harta.osm.Landuse;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.tile.TilePalette;
import org.apache.log4j.Logger;

import java.util.Collection;

/**
 * Created by serg on 08-Aug-16.
 */
public class LandusePainter extends AbstractPainter{

  private static Logger log = Logger.getLogger("landuse_painter");

  public LandusePainter(AbstractProjector projector, BoundsXY bounds) {
    super(projector, bounds);
  }

  public void drawLanduse(TileDrawer drawer, Collection<Landuse> landuse, int level)
  {

    for (Landuse land : landuse) {
      switch (land.getType()) {
        case "grass":
          drawer.setFillColor(TilePalette.PARK_COLOR);
          break;
        case "greenhouse_horticulture":
          drawer.setFillColor(TilePalette.GREEN_HOUSE);
          break;
        default:
          log.error(String.format("Landuse type : \"%s\" is not supported for drawing", land.getType()));
          continue;
      }
      CanvasPolygon polygon = createPolygon(land);
      shiftPolygon(polygon);
      drawer.fillPolygon(polygon.getxPoints(), polygon.getyPoints());
    }

//    CanvasPolygon polygon = createPolygon(building);
//    shiftPoints(bounds.getXmin(), polygon.getxPoints());
//    shiftPoints(bounds.getYmin(), polygon.getyPoints());
//
//    drawer.fillPolygon(polygon.getxPoints(), polygon.getyPoints());
  }
}
