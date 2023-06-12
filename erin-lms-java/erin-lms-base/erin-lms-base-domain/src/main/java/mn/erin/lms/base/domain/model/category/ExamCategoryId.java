package mn.erin.lms.base.domain.model.category;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Temuulen Naranbold
 */
public class ExamCategoryId extends EntityId
{
  private ExamCategoryId(String id)
  {
    super(Validate.notBlank(id));
  }

  public static ExamCategoryId valueOf(String id)
  {
    return new ExamCategoryId(id);
  }
}
