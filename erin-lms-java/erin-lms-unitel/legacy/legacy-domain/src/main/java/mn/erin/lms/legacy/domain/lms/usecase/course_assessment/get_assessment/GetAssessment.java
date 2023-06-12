/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_assessment.get_assessment;

import java.util.Objects;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.assessment.Assessment;
import mn.erin.lms.legacy.domain.lms.model.assessment.TestId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseAssessmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class GetAssessment implements UseCase<GetAssessmentInput, GetAssessmentOutput>
{
  private CourseAssessmentRepository courseAssessmentRepository;
  private CourseRepository courseRepository;

  public GetAssessment(CourseAssessmentRepository courseAssessmentRepository, CourseRepository courseRepository)
  {
    this.courseAssessmentRepository = Objects.requireNonNull(courseAssessmentRepository, "Course assessment repository cannot be null!");
    this.courseRepository = Objects.requireNonNull(courseRepository, "Course repository cannot be null!");
  }

  @Override
  public GetAssessmentOutput execute(GetAssessmentInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException("Get assessment input cannot be null!");
    }
    CourseId courseId = new CourseId(input.getCourseId());

    try
    {
      courseRepository.getCourse(courseId);
    }
    catch (LMSRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
    Assessment assessment;
    try
    {
      assessment = courseAssessmentRepository.get(courseId);
    }
    catch (LMSRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
    return createOutput(assessment);
  }

  private GetAssessmentOutput createOutput(Assessment assessment)
  {
    GetAssessmentOutput output = new GetAssessmentOutput(assessment.getId().getId(), assessment.getCourseId().getId());
    for (TestId testId : assessment.getCourseTests())
    {
      output.addCourseTestId(testId.getId());
    }
    return output;
  }
}
