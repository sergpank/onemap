package md.onemap.harta.export;

import org.junit.Test;

import static org.junit.Assert.*;

public class HighwayNameNormalizerTest
{

  @Test
  public void normalizeName()
  {
    String roL = "aăâbcdefghiîjklmnppqrsșştțţuvwxyz";
    String roU = "AĂÂBCDEFGHIÎJKLMNPPQRSȘŞTȚŢUVWXYZ";
    String ruL = "абвгдеёжзийклмнопрстуфхцчшщьъыэюя";
    String ruU = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЬЪЫЭЮЯ";

    HighwayNameNormalizer normalizer = new HighwayNameNormalizer();

    assertEquals("aaabcdefghiijklmnppqrssstttuvwxyz", normalizer.normalizeName(roL));
    assertEquals("aaabcdefghiijklmnppqrssstttuvwxyz", normalizer.normalizeName(roU));
    assertEquals("абвгдеежзийклмнопрстуфхцчшщьъыэюя", normalizer.normalizeName(ruL));
    assertEquals("абвгдеежзийклмнопрстуфхцчшщьъыэюя", normalizer.normalizeName(ruU));
  }
}