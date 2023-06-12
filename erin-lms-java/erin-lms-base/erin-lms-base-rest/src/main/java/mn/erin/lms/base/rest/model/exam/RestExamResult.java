package mn.erin.lms.base.rest.model.exam;

import java.util.List;

/**
 * @author Galsan Bayart.
 */
public class RestExamResult
{
  List<RestExamResultEntity> restExamResultEntities;

  public RestExamResult()
  {
  }

  public List<RestExamResultEntity> getRestExamResultEntities()
  {
    return restExamResultEntities;
  }

  public void setRestExamResultEntities(List<RestExamResultEntity> restExamResultEntities)
  {
    this.restExamResultEntities = restExamResultEntities;
  }
}
