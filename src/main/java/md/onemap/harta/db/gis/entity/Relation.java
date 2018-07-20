package md.onemap.harta.db.gis.entity;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Relation extends Unit
{
  private Map<String, String> tags;
  private Set<Member> members;

  public Relation(long id, Map<String, String> tags, Set<Member> nodeMembers)
  {
    super(id, UnitType.RELATION);
    this.tags = tags;
    this.members = nodeMembers;
  }

  public Set<Member> getMembers()
  {
    return members;
  }

  public Map<String, String> getTags()
  {
    return tags;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }
    Relation relation = (Relation) o;
    return id == relation.id;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(id);
  }
}
