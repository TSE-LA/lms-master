package mn.erin.lms.base.domain.model.course;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Temuulen Naranbold
 */
public class LearnerCourseHistoryId extends EntityId
{
  public LearnerCourseHistoryId(String id)
  {
    super(id);
  }

  public static LearnerCourseHistoryId valueOf(String id)
  {
    return new LearnerCourseHistoryId(id);
  }
}
