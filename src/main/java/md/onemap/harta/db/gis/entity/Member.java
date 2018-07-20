package md.onemap.harta.db.gis.entity;

import java.util.Objects;

public class Member
{
  private UnitType type;
  private Long ref;
  private String role;

  public Member(String type, Long ref, String role)
  {
    this.type = UnitType.valueOf(type.toUpperCase());
    this.ref = ref;
    this.role = role;
  }

  public UnitType getType()
  {
    return type;
  }

  public Long getRef()
  {
    return ref;
  }

  public String getRole()
  {
    return role;
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
    Member member = (Member) o;
    return type == member.type &&
        Objects.equals(ref, member.ref) &&
        Objects.equals(role, member.role);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(type, ref, role);
  }
}
