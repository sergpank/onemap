package md.onemap.harta.db.gis.entity;

public class Unit
{
  protected long id;
  private UnitType unitType;

  public Unit(long id, UnitType type)
  {
    this.id = id;
    this.unitType = type;
  }

  public long getId()
  {
    return id;
  }

  public UnitType getUnitType()
  {
    return unitType;
  }
}
