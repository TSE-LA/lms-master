/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_assessment.create_assessment;

import java.util.ArrayList;
import java.util.List;
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
public class CreateAssessment implements UseCase<CreateAssessmentInput, CreateAssessmentOutput>
{
  private CourseAssessmentRepository courseAssessmentRepository;
  private CourseRepository courseRepository;

  public CreateAssessment(CourseAssessmentRepository courseAssessmentRepository, CourseRepository courseRepository)
  {
    this.courseAssessmentRepository = Objects.requireNonNull(courseAssessmentRepository, "Course assessment repository cannot be null!");
    this.courseRepository = Objects.requireNonNull(courseRepository, "Course repository cannot be null!");
  }

  @Override
  public CreateAssessmentOutput execute(CreateAssessmentInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException("Course Assessment input cannot be null!");
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
    List<TestId> testIds = new ArrayList<>();
    for (String courseTestId : input.getTestIds())
    {
      testIds.add(new TestId(courseTestId));
    }
    Assessment assessment;
    try
    {
      assessment = courseAssessmentRepository.create(courseId, testIds);
    }
    catch (LMSRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
    return createOutput(assessment);
  }

  private CreateAssessmentOutput createOutput(Assessment assessment)
  {
    CreateAssessmentOutput output = new CreateAssessmentOutput(assessment.getId().getId(), assessment.getCourseId().getId());
    for (TestId testId : assessment.getCourseTests())
    {
      output.addTestIds(testId.getId());
    }
    return output;
  }
}
