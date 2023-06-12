package mn.erin.lms.legacy.domain.lms.usecase.readership.create_readership;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.audit.CourseAudit;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.usecase.audit.CreateCourseAudit;
import mn.erin.lms.legacy.domain.lms.usecase.audit.dto.CreateCourseAuditInput;

/**
 * @author Munkh
 */
public class CreateReaderships implements UseCase<CreateReadershipsInput, List<String>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateReaderships.class);
  private final CourseRepository courseRepository;
  private final CourseAuditRepository courseAuditRepository;
  private final UserRepository userRepository;

  public CreateReaderships(CourseRepository courseRepository, CourseAuditRepository courseAuditRepository, UserRepository userRepository)
  {
    this.courseRepository = courseRepository;
    this.courseAuditRepository = courseAuditRepository;
    this.userRepository = userRepository;
  }

  @Override
  public List<String> execute(CreateReadershipsInput input) throws UseCaseException
  {
    CreateCourseAudit createCourseAudit = new CreateCourseAudit(courseAuditRepository, courseRepository, userRepository);

    List<String> result = new ArrayList<>();

    List<String> courseAudit = courseAuditRepository.listAll(new LearnerId(input.getLearnerId())).stream().map(CourseAudit::getCourseId).map(CourseId::getId)
        .collect(Collectors.toList());

    for (String courseId : input.getCourses())
    {
      if (!courseAudit.contains(courseId))
      {
        CreateCourseAuditInput auditInput = new CreateCourseAuditInput(courseId, input.getLearnerId());
        try
        {
          String id = createCourseAudit.execute(auditInput);
          result.add(id);
        }
        catch (UseCaseException e)
        {
          LOGGER.error(e.getMessage(), e);
        }
      }
    }

    return result;
  }
}
