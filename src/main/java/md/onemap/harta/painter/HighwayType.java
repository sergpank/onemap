package md.onemap.harta.painter;

import md.onemap.harta.geometry.GeometryUtil;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.tile.Palette;

import java.awt.*;

public enum HighwayType
{
  // Main Tags of road network (from most to less important)
  motorway(12, 100, Palette.HIGHWAY_COLOR, Palette.HIGHWAY_BORDER_COLOR),   // - a restricted access major divided motorway, normally with 2 or more running lanes
  trunk(12, 90, Palette.HIGHWAY_COLOR, Palette.HIGHWAY_BORDER_COLOR),      // - the most important roads that aren't motorways (not necessary to be divided by lanes)
  primary(8, 80, Palette.HIGHWAY_COLOR, Palette.HIGHWAY_BORDER_COLOR),     // - next most important road (links between large towns)
  secondary(8, 70, Palette.HIGHWAY_COLOR, Palette.HIGHWAY_BORDER_COLOR),   // - next most important road in a country system (link between towns)
  tertiary(8, 60, Palette.HIGHWAY_COLOR, Palette.HIGHWAY_BORDER_COLOR),    // - less important roads (link between small town and village)
  unclassified(4, 50, Palette.HIGHWAY_COLOR, Palette.HIGHWAY_BORDER_COLOR),// - local motorways
  residential(4, 50, Palette.HIGHWAY_COLOR, Palette.HIGHWAY_BORDER_COLOR), // - roads inside living areas
  service(4, 50, Palette.HIGHWAY_COLOR, Palette.HIGHWAY_BORDER_COLOR),     // - roads in industrial areas or parking or some restricted zone

  // Links of main road types
  motorway_link(6, 100, Palette.HIGHWAY_COLOR, Palette.HIGHWAY_BORDER_COLOR),
  trunk_link(6, 90, Palette.HIGHWAY_COLOR, Palette.HIGHWAY_BORDER_COLOR),
  primary_link(4, 80, Palette.HIGHWAY_COLOR, Palette.HIGHWAY_BORDER_COLOR),
  secondary_link(4, 70, Palette.HIGHWAY_COLOR, Palette.HIGHWAY_BORDER_COLOR),
  tertiary_link(4, 60, Palette.HIGHWAY_COLOR, Palette.HIGHWAY_BORDER_COLOR),

  // Special road types
  living_street(2, 40, Palette.PEDESTRIAN_HIGHWAY_COLOR, Palette.PEDESTRIAN_HIGHWAY_BORDER_COLOR),// - residential rads where pedestrians have legal priority over the cars
  pedestrian(2, 40, Palette.PEDESTRIAN_HIGHWAY_COLOR, Palette.PEDESTRIAN_HIGHWAY_BORDER_COLOR),   // - residential roads that are allowed mainly/exclusively for pedestrians
  track(2, 40, Palette.PEDESTRIAN_HIGHWAY_COLOR, Palette.PEDESTRIAN_HIGHWAY_BORDER_COLOR),        // - unpaved surface for agricultural of forestry use
  raceway(2, 40, Palette.PEDESTRIAN_HIGHWAY_COLOR, Palette.PEDESTRIAN_HIGHWAY_BORDER_COLOR),      // - a racing road
  road(2, 40, Palette.PEDESTRIAN_HIGHWAY_COLOR, Palette.PEDESTRIAN_HIGHWAY_BORDER_COLOR),         // - lines that _may be_ are roads. This is a temporary tag

  // Roads not for 4-wheeled transport
  footway(2, 30, Palette.PEDESTRIAN_HIGHWAY_COLOR, Palette.PEDESTRIAN_HIGHWAY_BORDER_COLOR),  // - road for pedestrians, if bicycle=ye - allowed for riding
  cycleway(2, 30, Palette.PEDESTRIAN_HIGHWAY_COLOR, Palette.PEDESTRIAN_HIGHWAY_BORDER_COLOR), // - road for bicycles, if foot=yes - allowed for walking
  bridleway(2, 30, Palette.PEDESTRIAN_HIGHWAY_COLOR, Palette.PEDESTRIAN_HIGHWAY_BORDER_COLOR),// - road for horse riding
  steps(2, 30, Palette.PEDESTRIAN_HIGHWAY_COLOR, Palette.PEDESTRIAN_HIGHWAY_BORDER_COLOR),    // - stairs on footways
  path(2, 30, Palette.PEDESTRIAN_HIGHWAY_COLOR, Palette.PEDESTRIAN_HIGHWAY_BORDER_COLOR),     // - a non specific path mainly for walkers

  // Roads under construction
  proposed(2, 20, Palette.PROPOSED_HIGHWAY_COLOR, Palette.PROPOSED_HIGHWAY_BORDER_COLOR),    // - approved for building (do not draw)
  construction(2, 10, Palette.PROPOSED_HIGHWAY_COLOR, Palette.PROPOSED_HIGHWAY_BORDER_COLOR);// - under construction or reparation (draw, but don't use for routing)

  private final int width;
  private final int priority;
  private final Color surfaceColor;
  private final Color borderColor;

  HighwayType(int width, int priority, Color surfaceColor, Color borderColor)
  {
    this.width = width;
    this.priority = priority;
    this.surfaceColor = surfaceColor;
    this.borderColor = borderColor;
  }

  public int getWidth(AbstractProjector projector, boolean withBorder)
  {
    return getRoadWidthPixels(projector, width, withBorder);
  }

  public int getPriority()
  {
    return priority;
  }

  public Color getSurfaceColor()
  {
    return surfaceColor;
  }

  public Color getBorderColor()
  {
    return borderColor;
  }

  private int getRoadWidthPixels(AbstractProjector projector, double roadWidthMeters, boolean withBorder)
  {
    double widthInPixels = GeometryUtil.metersToPixels(projector, roadWidthMeters);

    if (withBorder)
    {
      double extension = widthInPixels / 4;
      return  (int)Math.ceil(widthInPixels + (extension < 2 ? 2 : Math.ceil(extension)));
    }
    else
    {
      return (int)Math.ceil(widthInPixels);
    }
  }
}