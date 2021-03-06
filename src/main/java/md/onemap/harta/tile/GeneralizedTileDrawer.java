package md.onemap.harta.tile;

import md.onemap.harta.db.gis.RelationGisDao;
import md.onemap.harta.db.gis.WayGisDao;
import md.onemap.harta.db.gis.entity.Member;
import md.onemap.harta.db.gis.entity.Relation;
import md.onemap.harta.db.gis.entity.Unit;
import md.onemap.harta.db.gis.entity.Way;
import md.onemap.harta.drawer.AwtDrawer;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.geometry.BoundsXY;
import md.onemap.harta.geometry.Label;
import md.onemap.harta.loader.AbstractLoader;
import md.onemap.harta.osm.Building;
import md.onemap.harta.osm.Highway;
import md.onemap.harta.osm.Landuse;
import md.onemap.harta.osm.Leisure;
import md.onemap.harta.osm.Natural;
import md.onemap.harta.osm.Waterway;
import md.onemap.harta.painter.BuildingPainter;
import md.onemap.harta.painter.HighwayPainter;
import md.onemap.harta.painter.LandusePainter;
import md.onemap.harta.painter.LeisurePainter;
import md.onemap.harta.painter.NaturePainter;
import md.onemap.harta.painter.TextPainter;
import md.onemap.harta.painter.WaterwayPainter;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.properties.Props;
import md.onemap.harta.util.Stopwatch;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GeneralizedTileDrawer extends AbstractTileDrawer
{
  public GeneralizedTileDrawer(int tileSize)
  {
    super(tileSize);
  }

  @Override
  public BufferedImage drawTile(int level, int x, int y, AbstractProjector projector,
                                AbstractLoader loader, BoundsLatLon tileBounds)
  {
    BufferedImage bi = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = createGraphics(bi);
    AwtDrawer drawer = new AwtDrawer(graphics);

    Collection<Way> tileWays = new WayGisDao().load(level, tileBounds);
    Collection<Relation> tileRelations = new RelationGisDao(null).load(level, tileBounds);
    Set<Way> relationWays = getRelationWays(tileRelations);
    tileWays.addAll(relationWays);

    Set<Way> highways = getWays(tileWays, Highway.HIGHWAY);
    Set<Way> buildings = getWays(tileWays, Building.BUILDING);
    Set<Way> landuse = getWays(tileWays, Landuse.LANDUSE);
    Set<Way> leisure = getWays(tileWays, Leisure.LEISURE);
    Set<Way> waterways = getWays(tileWays, Waterway.WATERWAY);
    Set<Way> nature = getWays(tileWays, Natural.NATURAL);

    BoundsXY boundsXY = tileBounds.toXY(projector);

    new LeisurePainter(projector, boundsXY).drawLeisure(drawer, leisure, level);
    new LandusePainter(projector, boundsXY).drawLanduse(drawer, landuse, level);
    new WaterwayPainter(projector, boundsXY).drawWaterways(drawer, waterways, level);
    new NaturePainter(projector, boundsXY).drawNatural(drawer, nature, level);

    List<Label> highwayLabels = new HighwayPainter(projector, boundsXY).drawHighways(drawer, highways, level);
    List<Label> houseLabels = new BuildingPainter(projector, boundsXY).drawBuildings(drawer, buildings, level);

    if (level > Palette.BUILDING_LABEL_LEVEL) {
      TextPainter textPainter = new TextPainter(projector, boundsXY);
      for (Label label : houseLabels)
      {
        textPainter.paintHouseLabel(drawer, label);
      }
    }

    if (level > Palette.STREET_LABEL_LEVEL)
    {
      TextPainter textPainter = new TextPainter(projector, boundsXY);
      for (Label label : highwayLabels)
      {
        textPainter.paintHighwayLabel(drawer, label);
      }
    }

    if (Props.debugTileBorder()) {
      graphics.setPaint(Color.RED);
      graphics.setStroke(new BasicStroke(1));

      int max = Props.tileSize() - 1;

      graphics.drawLine(0, 0, max, 0);
      graphics.drawLine(0, 0, 0, max);
      graphics.drawLine(max, max, 0, max);
      graphics.drawLine(max, max, max, 0);
    }

    if (Props.debugTileNumber())
    {
      int pos = Props.tileSize() / 2;
      drawer.fillText(String.format("z :: %d\nx :: %d\ny :: %d", level, x, y), pos / 2, pos);
    }

    if (Props.debugTileTime())
    {
      graphics.setStroke(new BasicStroke(2));
      int pos = Props.tileSize() / 2;
      drawer.fillText(Stopwatch.stop() + " ms", pos / 2, pos);
    }

    return bi;
  }

  private Set<Way> getWays(Collection<Way> tileWays, String type)
  {
    return tileWays
        .stream()
        .filter(w -> w.getType() != null && w.getType().equals(type))
        .collect(Collectors.toSet());
  }

  private Set<Way> getRelationWays(Collection<Relation> tileRelations)
  {
    Set<Way> relationWays = new HashSet<>();
    for (Relation relation : tileRelations)
    {
      relationWays.addAll(extractWaysFromRelation(relation));
    }
    return relationWays;
  }

  private Collection<Way> extractWaysFromRelation(Relation relation)
  {
    Set<Way> ways = new HashSet<>();
    String relationLanduse = relation.getTags().get("landuse");
    String relationNatural = relation.getTags().get("natural");
    for (Member m : relation.getMembers())
    {
      switch (m.getType())
      {
        case WAY:
        {
          Way way = new WayGisDao().load(m.getRef());
          if (way != null)
          {
            ways.add(way);
            way.getTags().putIfAbsent("landuse", relationLanduse);
            way.getTags().putIfAbsent("natural", relationNatural);
            if (way.getType() == null)
            {
              way.setType(Unit.defineType(relation.getTags()));
            }
          }
          break;
        }
        case RELATION:
        {
          ways.addAll(extractWaysFromRelation(new RelationGisDao(null).load(m.getRef())));
          break;
        }
        default: break;
      }
    }

    return ways;
  }
}
