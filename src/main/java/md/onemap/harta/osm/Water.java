package md.onemap.harta.osm;

public enum Water
{
  // leisure
  swimming_pool,
  ice_rink,
  water_park,

  // landuse
  basin,
  reservoir;

  public static boolean isWater(String type)
  {
    try
    {
      Water.valueOf(type);
      return true;
    }
    catch (Throwable t)
    {
      return false;
    }
  }
}
