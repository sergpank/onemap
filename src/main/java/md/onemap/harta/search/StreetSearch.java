package md.onemap.harta.search;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import md.onemap.harta.db.dao.NormalizedHighwayDao;
import md.onemap.harta.osm.NormalizedHighway;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class StreetSearch
{
  public Collection<NormalizedHighway> findStreets(String key)
  {
    String streetName = key.trim().toLowerCase();

    Collection<NormalizedHighway> result = new NormalizedHighwayDao().findHighwaysByKey(streetName);

    return result;
  }

  public static void main(String[] args)
  {
    NormalizedHighway highway1 = new NormalizedHighway(111, "Name1", "Имя", "Наименованiе", null);
    NormalizedHighway highway2 = new NormalizedHighway(222, "Name2", "Имя", "Наименованiе", null);

//    Gson gson = new Gson();
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    String json = gson.toJson(Arrays.asList(highway1, highway2));

    System.out.println("JSON = " + json);

    Collection<NormalizedHighway> fromJson = gson.fromJson(json, new TypeToken<List<NormalizedHighway>>(){}.getType());

    System.out.println("from JSON = " + fromJson);
  }
}
