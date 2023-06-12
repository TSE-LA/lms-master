/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_assessment.delete_assessment;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.assessment.Assessment;
import mn.erin.lms.legacy.domain.lms.model.assessment.TestId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseAssessmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseTestRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class DeleteAssessment implements UseCase<DeleteAssessmentInput, DeleteAssessmentOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteAssessment.class);

  private CourseAssessmentRepository courseAssessmentRepository;
  private CourseTestRepository courseTestRepository;

  public DeleteAssessment(CourseAssessmentRepository courseAssessmentRepository, CourseTestRepository courseTestRepository)
  {
    this.courseAssessmentRepository = Objects.requireNonNull(courseAssessmentRepository, "Course Assessment repository cannot be null!");
    this.courseTestRepository = Objects.requireNonNull(courseTestRepository, "CourseTestRepository cannot be null!");
  }

  @Override
  public DeleteAssessmentOutput execute(DeleteAssessmentInput input) throws UseCaseException
  {

    if (input == null)
    {
      throw new UseCaseException("Delete assessment cannot be null!");
    }

    CourseId courseId = new CourseId(input.getCourseId());
    Assessment assessment;
    try
    {
      assessment = courseAssessmentRepository.get(courseId);
    }
    catch (LMSRepositoryException e)
    {
      return new DeleteAssessmentOutput(false);
    }

    boolean deleted;
    try
    {
      deleted = courseAssessmentRepository.delete(new CourseId(input.getCourseId()));

      if (deleted)
      {
        List<TestId> testIds = assessment.getCourseTests();

        for (TestId testId : testIds)
        {
          boolean isCourseTestDeleted = courseTestRepository.delete(testId);
          if (!isCourseTestDeleted)
          {
            LOGGER.warn("COURSE TEST WITH THE ID: {} WAS NOT DELETED!!!", testId.getId());
          }
        }
      }
    }
    catch (LMSRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
    return new DeleteAssessmentOutput(deleted);
  }
}
