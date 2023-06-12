/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_assessment.update_assessment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.assessment.Assessment;
import mn.erin.lms.legacy.domain.lms.model.assessment.TestId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseAssessmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class UpdateAssessment implements UseCase<UpdateAssessmentInput, UpdateAssessmentOutput>
{
  private CourseAssessmentRepository courseAssessmentRepository;

  public UpdateAssessment(CourseAssessmentRepository courseAssessmentRepository)
  {
    this.courseAssessmentRepository = Objects.requireNonNull(courseAssessmentRepository, "Course assessment cannot be null!");
  }

  @Override
  public UpdateAssessmentOutput execute(UpdateAssessmentInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException("Update assessment input cannot be null!");
    }
    List<TestId> testIds = new ArrayList<>();
    for (String stringId : input.getCourseTestIds())
    {
      testIds.add(new TestId(stringId));
    }

    Assessment assessment;
    try
    {
      assessment = courseAssessmentRepository.update(new CourseId(input.getCourseId()), testIds);
    }
    catch (LMSRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
    return createOutput(assessment);
  }

  private UpdateAssessmentOutput createOutput(Assessment assessment)
  {
    UpdateAssessmentOutput output = new UpdateAssessmentOutput(assessment.getId().getId(), assessment.getCourseId().getId());
    for (TestId testId : assessment.getCourseTests())
    {
      output.addTestIds(testId.getId());
    }
    return output;
  }
}
