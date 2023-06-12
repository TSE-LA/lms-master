package mn.erin.lms.base.domain.model.category;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Temuulen Naranbold
 */
public class QuestionCategoryId extends EntityId
{
  public QuestionCategoryId(String id)
  {
    super(Validate.notBlank(id));
  }
  public static QuestionCategoryId valueOf(String id)
  {
    return new QuestionCategoryId(id);
  }
}
