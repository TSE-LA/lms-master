package mn.erin.lms.legacy.domain.lms.usecase.readership.create_readership;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseDetail;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;

/**
 * @author Munkh
 */
public class UpdateCourseEnrollments implements UseCase<UpdateCourseEnrollmentsInput, Void>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateCourseEnrollments.class);
  private final CourseRepository courseRepository;

  public UpdateCourseEnrollments(CourseRepository courseRepository)
  {
    this.courseRepository = courseRepository;
  }

  @Override
  public Void execute(UpdateCourseEnrollmentsInput input) throws UseCaseException
  {
    Validate.notBlank(input.getGroupId());
    Validate.notBlank(input.getNewGroupId());
    Set<String> groups = new HashSet<>();
    groups.add(input.getGroupId());
    groups.add(input.getNewGroupId());
    List<Course> courses = courseRepository.getCourseList(groups);

    for (Course course : courses)
    {
      try
      {
        CourseDetail currentCourseDetail = course.getCourseDetail();
        ((List<Serializable>) currentCourseDetail.getProperties().get("enrolledGroups")).add(input.getNewGroupId());
        courseRepository.update(course.getCourseId(), currentCourseDetail);
      }
      catch (LMSRepositoryException e)
      {
        LOGGER.error(e.getMessage(), e);
      }
    }

    return null;
  }
}
