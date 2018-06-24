package md.onemap.harta.painter;

public enum LanduseGreen
{
//  allotments,
//  basin,
//  brownfield,
  cemetery,
  churchyard,
//  commercial,
//  construction,
//  farmland,
//  farmyard,
  forest,
//  garages,
  grass,
//  greenfield,
//  greenhouse_horticulture,
//  industrial,
  landfill,
  meadow,
//  military,
  orchard,
//  quarry,
  recreation_ground,
//  reservoir,
//  residential,
//  retail,
  village_green,
  vineyard;

  public static boolean isGreen(String type)
  {
    try
    {
      LanduseGreen.valueOf(type);
      return true;
    }
    catch (Throwable t)
    {
      return false;
    }
  }
}
