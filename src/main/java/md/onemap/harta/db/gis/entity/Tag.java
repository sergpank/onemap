package md.onemap.harta.db.gis.entity;

import java.util.Map;

public class Tag
{
  private Long parentId;
  private Map<String, String> tags;

  public Tag(Long parentId, Map<String, String> tags)
  {
    this.parentId = parentId;
    this.tags = tags;
  }

  public Long getParentId()
  {
    return parentId;
  }

  public Map<String, String> getTags()
  {
    return tags;
  }
}
