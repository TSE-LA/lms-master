package mn.erin.lms.base.domain.usecase.course;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Learner;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.UseCaseDelegator;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class, Learner.class })
public class SearchCourse extends CourseUseCase<Map<String, Object>, List<CourseDto>>
{
  private final UseCaseDelegator<Map<String, Object>, List<CourseDto>> useCaseDelegator;

  public SearchCourse(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry,
      UseCaseDelegator<Map<String, Object>, List<CourseDto>> useCaseDelegator)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.useCaseDelegator = Objects.requireNonNull(useCaseDelegator);
  }

  @Override
  public List<CourseDto> execute(Map<String, Object> queryParameters) throws UseCaseException
  {
    Validate.notEmpty(queryParameters);
    LmsUser currentUser = lmsServiceRegistry.getLmsUserService().getCurrentUser();
    return useCaseDelegator.execute(currentUser, queryParameters);
  }
}
