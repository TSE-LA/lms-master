package mn.erin.lms.base.domain.usecase.assessment;

import java.util.List;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.assessment.AssessmentId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.assessment.dto.UpdateAssessmentStatusInput;

/**
 *
 * @author Munkh
 */
@Authorized(users = { Instructor.class })
public class UpdateAssessmentStatus extends AssessmentUseCase<UpdateAssessmentStatusInput, Void>
{
  private final CourseRepository courseRepository;

  public UpdateAssessmentStatus(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.courseRepository = lmsRepositoryRegistry.getCourseRepository();
  }


  @Override
  protected Void executeImpl(UpdateAssessmentStatusInput input) throws UseCaseException
  {
    Validate.notNull(input);
    Validate.notBlank(input.getAssessmentId());

    try
    {
      // On deactivation check all courses
      if(!input.isActivate())
      {
        List<Course> courses = courseRepository.listAll(AssessmentId.valueOf(input.getAssessmentId()));

        if(!courses.isEmpty() && (input.getCurrentCourseId() == null || courses.size() != 1 ||
              !courses.get(0).getCourseId().getId().equals(input.getCurrentCourseId())))
        {
          return null;
        }
      }

      assessmentRepository.updateStatus(AssessmentId.valueOf(input.getAssessmentId()), input.isActivate());
      return null;
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

}
