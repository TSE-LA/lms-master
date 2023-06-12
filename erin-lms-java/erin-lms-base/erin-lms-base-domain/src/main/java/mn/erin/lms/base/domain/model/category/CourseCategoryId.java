package mn.erin.lms.base.domain.model.category;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseCategoryId extends EntityId
{
  private CourseCategoryId(String id)
  {
    super(id);
  }

  public static CourseCategoryId valueOf(String id)
  {
    return new CourseCategoryId(id);
  }
}
