package md.onemap.harta.painter;

import md.onemap.harta.drawer.AbstractDrawer;
import md.onemap.harta.geometry.*;
import md.onemap.harta.geometry.Label;
import md.onemap.harta.osm.Highway;
import md.onemap.harta.osm.OsmNode;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.tile.TilePalette;

import java.awt.*;
import java.awt.font.GlyphVector;
import java.util.List;

/**
 * Created by sergpank on 07.07.15.
 */
public class TextPainter extends AbstractPainter
{
  public TextPainter(AbstractProjector projector, BoundsXY bounds)
  {
    super(projector, bounds);
  }

  public void paintHighwayLabel(AbstractDrawer drawer, Label label)
  {
    Highway highway = label.getHighway();
    if (highway != null && highway.getName() != null)
    {
      drawer.setFillColor(TilePalette.FONT_COLOR);
      drawTiltString(drawer, label);
    }
  }

  private void drawTiltString(AbstractDrawer drawer, Label label)
  {
    Font font = new Font(TilePalette.HIGHWAY_FONT_NAME, Font.PLAIN, TilePalette.HIGHWAY_FONT_SIZE);

    XYPoint roadStartPoint = getRoadStartPoint(label);
    if (roadStartPoint == null)
    {
      return;
    }
    double roadLength = GeometryUtil.getHighwayLength(label.getHighway(), projector);

    if (roadLength > label.getWidth())
    {
      RoadLabelIntersector intersector = new RoadLabelIntersector(bounds, TilePalette.HIGHWAY_FONT_NAME, TilePalette.HIGHWAY_FONT_SIZE);
      List<Intersection> intersections = intersector.getIntersections(label.getHighway(), label, projector);
      for (int i = 0; i < intersections.size(); i++)
      {
        String character = label.getText().charAt(i % label.getText().length()) + "";
        GlyphVector glyphVector = font.createGlyphVector(drawer.getFontRenderContext(), character);
        Intersection intersection = intersections.get(i);

        drawer.translate((int)intersection.getPoint().getX(), (int)intersection.getPoint().getY());
        drawer.rotate(intersection.getAngle());

        Shape outline = glyphVector.getGlyphOutline(0);
        drawer.fill(outline);

        drawer.rotate(-(intersection.getAngle()));
        drawer.translate(-(int)intersection.getPoint().getX(), -(int)intersection.getPoint().getY());
      }
    }
  }

  /**
   * Road start point is upper left of lower left point
   * Because road name is drawn from left to right
   */
  private XYPoint getRoadStartPoint(Label label)
  {
    List<OsmNode> nodes = label.getHighway().getNodes();
    if (nodes.isEmpty())
    {
      return null;
    }
    OsmNode firstNode = nodes.get(0);
    OsmNode lastNode = nodes.get(nodes.size() - 1);

    if (firstNode.getLon() < lastNode.getLon())
    {
      return shiftPoint(projector.getXY(firstNode.getLat(), firstNode.getLon()));
    }
    else
    {
      return shiftPoint(projector.getXY(lastNode.getLat(), lastNode.getLon()));
    }
  }
}
