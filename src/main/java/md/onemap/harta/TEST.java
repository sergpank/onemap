package md.onemap.harta;

import java.util.HashMap;
import java.util.stream.Collectors;

public class TEST
{
  public static void main(String[] args)
  {
//    int x = 38023;
//    int y = 23048;
//    int z = 17;
//
//    GeneratorProperties props = new GeneratorProperties("properties/db-generator.properties");
//    TileGenerator gis = new TileGeneratorGIS(props);
//    TileGenerator db = new TileGeneratorGIS(props);
//
//    MercatorProjector projector = new MercatorProjector(z);
//    TileBoundsCalculator boundsCalculator = new TileBoundsCalculator(props.tileSize(), projector);
//
//    for (int i = 0; i < 10; i++)
//    {
//      start();
//      BoundsLatLon tileBounds = boundsCalculator.getTileBounds(x, y + i, 0);
//      db.generateTile(x , y + i, z, projector, tileBounds);
//      System.out.println("DB : " + TimePrettyPrint.prettyPrint(stop()));
//    }
//
//    System.out.println("\n\n\n");
//
//    for (int i = 0; i < 10; i++)
//    {
//      start();
//      BoundsLatLon tileBounds = boundsCalculator.getTileBounds(x, y + i, 0);
//      gis.generateTile(x, y + i, z, projector, tileBounds);
//      System.out.println("GIS: " + TimePrettyPrint.prettyPrint(stop()));
//    }

    HashMap<Integer, String> data = new HashMap<>();
    data.put(1, "one");
    data.put(2, "two");
    data.put(3, "three");

    String collect = data.entrySet().stream().map(m -> m.getKey() + "::" + m.getValue()).collect(Collectors.joining("; "));

    System.out.println(collect);
  }
}
