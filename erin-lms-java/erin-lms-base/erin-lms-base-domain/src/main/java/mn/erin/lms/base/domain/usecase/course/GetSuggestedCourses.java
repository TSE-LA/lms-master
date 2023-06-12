package mn.erin.lms.base.domain.usecase.course;

import java.util.Objects;

import org.apache.commons.lang3.Validate;
import java.util.List;

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
import mn.erin.lms.base.aim.LmsUserService;
import mn.erin.lms.base.domain.usecase.UseCaseDelegator;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.domain.usecase.course.dto.GetCoursesInput;

/**
 * @author Erdenetulga
 */
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class, Learner.class })
public class GetSuggestedCourses extends CourseUseCase<GetCoursesInput, List<CourseDto>>
{
  private final UseCaseDelegator<GetCoursesInput, List<CourseDto>> useCaseDelegator;

  public GetSuggestedCourses(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry,
      UseCaseDelegator<GetCoursesInput, List<CourseDto>> useCaseDelegator)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.useCaseDelegator = Objects.requireNonNull(useCaseDelegator);
  }

  @Override
  public List<CourseDto> execute(GetCoursesInput input) throws UseCaseException
  {
    Validate.notNull(input);
    LmsUserService lmsUserRepository = lmsServiceRegistry.getLmsUserService();
    LmsUser lmsUser = lmsUserRepository.getCurrentUser();
    return useCaseDelegator.execute(lmsUser, input);
  }
}
