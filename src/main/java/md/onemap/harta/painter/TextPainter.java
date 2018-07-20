package md.onemap.harta.painter;

import md.onemap.harta.db.gis.entity.Node;
import md.onemap.harta.drawer.AbstractDrawer;
import md.onemap.harta.geometry.*;
import md.onemap.harta.geometry.Label;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.tile.Palette;

import java.awt.*;
import java.awt.font.GlyphVector;
import java.util.List;

/**
 * Created by sergpank on 07.07.15.
 */
public class TextPainter extends AbstractPainter
{
  private final Font font;

  public TextPainter(AbstractProjector projector, BoundsXY bounds)
  {
    super(projector, bounds);
    font = new Font(Palette.HIGHWAY_FONT_NAME, Font.PLAIN, Palette.HIGHWAY_FONT_SIZE);
  }

  public void paintHighwayLabel(AbstractDrawer drawer, Label label)
  {
    String highwayName = label.getText();
    if (highwayName != null && highwayName != null)
    {
      drawer.setFillColor(Palette.FONT_COLOR);
      drawTiltString(drawer, label);
    }
  }

  private void drawTiltString(AbstractDrawer drawer, Label label)
  {
    XYPoint roadStartPoint = getRoadStartPoint(label);
    if (roadStartPoint == null)
    {
      return;
    }
    double roadLength = GeometryUtil.getHighwayLength(projector, label.getNodes());

    if (roadLength > label.getWidth())
    {
      RoadLabelIntersector intersector = new RoadLabelIntersector(bounds, Palette.HIGHWAY_FONT_NAME, Palette.HIGHWAY_FONT_SIZE);
      List<Intersection> intersections = intersector.getIntersections(label, projector, label.getNodes());
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
    List<Node> nodes = label.getNodes();
    if (nodes.isEmpty())
    {
      return null;
    }
    Node firstNode = nodes.get(0);
    Node lastNode = nodes.get(nodes.size() - 1);

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
