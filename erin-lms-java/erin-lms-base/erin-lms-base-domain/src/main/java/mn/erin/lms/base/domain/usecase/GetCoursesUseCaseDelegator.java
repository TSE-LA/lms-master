package mn.erin.lms.base.domain.usecase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.domain.usecase.course.dto.GetCoursesInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetCoursesUseCaseDelegator implements UseCaseDelegator<GetCoursesInput, List<CourseDto>>
{
  private Map<String, UseCase<GetCoursesInput, List<CourseDto>>> registry;

  public GetCoursesUseCaseDelegator()
  {
    this.registry = new HashMap<>();
  }

  @Override
  public <T extends LmsUser> void register(Class<T> user, UseCase<GetCoursesInput, List<CourseDto>> useCase)
  {
    this.registry.put(user.getName(), useCase);
  }

  @Override
  public List<CourseDto> execute(LmsUser user, GetCoursesInput input) throws UseCaseException
  {
    UseCase<GetCoursesInput, List<CourseDto>> usecase = registry.get(user.getClass().getName());
    input.setLearnerId(user.getId().getId());
    return usecase.execute(input);
  }
}
