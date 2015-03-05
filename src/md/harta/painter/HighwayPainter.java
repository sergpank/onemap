package md.harta.painter;

import javafx.scene.canvas.GraphicsContext;
import md.harta.geometry.CanvasPolygon;
import md.harta.osm.Bounds;
import md.harta.osm.Highway;
import md.harta.projector.MercatorProjector;

import java.util.Map;

/**
 * Created by sergpank on 03.03.2015.
 */
public class HighwayPainter extends AbstractPainter{
  private Map<Long, Highway> highways;

  public HighwayPainter(Map<Long, Highway> highways, MercatorProjector projector, Bounds bounds) {
    super(projector, bounds);
    this.highways = highways;
  }

  public void drawHighways(GraphicsContext gc) {
    gc.setLineWidth(3);
    for (Highway highway : highways.values()) {
      CanvasPolygon polygon = createPolygon(highway);
      shiftPoints(polygon.getxPoints(), bounds.getxMin());
      shiftPoints(polygon.getyPoints(), bounds.getyMin());
      gc.strokePolyline(polygon.getxPoints(), polygon.getyPoints(), polygon.getPointsNumber());
    }
  }
}
