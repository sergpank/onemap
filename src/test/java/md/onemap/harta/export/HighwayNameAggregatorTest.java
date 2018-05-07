package md.onemap.harta.export;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HighwayNameAggregatorTest
{
  @Test
  public void test()
  {
    HighwayNameAggregator aggregator = new HighwayNameAggregator();
    aggregator.aggregate("a-main", "a-main", "main");
    aggregator.aggregate("a-main", "a-rus", "russian");
    aggregator.aggregate("a-main", "a-old", "old");
    aggregator.aggregate("a-main", "a-secondary", "secondary");

    aggregator.aggregate("b-main", "b-main-name", "main");

    assertEquals(4, aggregator.getResult().get("a-main").size());
    assertEquals(1, aggregator.getResult().get("b-main").size());
  }
}