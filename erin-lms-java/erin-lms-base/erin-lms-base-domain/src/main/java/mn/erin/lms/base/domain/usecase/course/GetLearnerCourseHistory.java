package mn.erin.lms.base.domain.usecase.course;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.course.LearnerCourseHistory;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;

/**
 * @author Temuulen Naranbold
 */
public class GetLearnerCourseHistory extends CourseUseCase<String, List<LearnerCourseHistory>>
{
  public GetLearnerCourseHistory(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  public List<LearnerCourseHistory> execute(String input) throws UseCaseException
  {
    String username = StringUtils.isBlank(input) ? lmsServiceRegistry.getAuthenticationService().getCurrentUsername() : input;
    return lmsRepositoryRegistry.getLearnerCourseHistoryRepository().getAllByUserId(username);
  }
}
