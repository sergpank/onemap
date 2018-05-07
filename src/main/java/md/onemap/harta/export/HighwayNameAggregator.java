package md.onemap.harta.export;

import java.util.HashMap;
import java.util.Map;

public class HighwayNameAggregator
{
  // Highway-name : <tag : highway-alternative-name>
  // This map contains all highway names map to its main(official) name.
  // Each alternative name is mapped to its tag.
  // Alternative name may be translation-to-other language | old-name.
  private Map<String, Map<String, String>> aggregationMap;

  public HighwayNameAggregator()
  {
    aggregationMap = new HashMap<>();
  }

  public void aggregate(String name, String otherName, String tag)
  {
    Map<String, String> tagMap = aggregationMap.getOrDefault(name, new HashMap<>());
    tagMap.put(tag, otherName);
    if (tagMap.size() == 1)
    {
      aggregationMap.put(name, tagMap);
    }
  }

  Map<String, Map<String, String>> getResult()
  {
    return aggregationMap;
  }
}
