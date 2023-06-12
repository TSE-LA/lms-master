/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.enrollment.update_enrollment;


import java.util.Objects;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollment;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollmentState;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class UpdateEnrollment implements UseCase<UpdateEnrollmentInput, UpdateEnrollmentOutput>
{
  private CourseEnrollmentRepository enrollmentRepository;

  public UpdateEnrollment(CourseEnrollmentRepository enrollmentRepository)
  {
    this.enrollmentRepository = Objects.requireNonNull(enrollmentRepository, "CourseEnrollmentRepository cannot be null!");
  }

  @Override
  public UpdateEnrollmentOutput execute(UpdateEnrollmentInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException("Update course enrollment input data cannot be null!");
    }
    LearnerId learnerId = new LearnerId(input.getLearnerId());
    CourseId courseId = new CourseId(input.getCourseId());
    CourseEnrollment courseEnrollment;
    try
    {
      courseEnrollment = this.enrollmentRepository.updateEnrollmentState(learnerId, courseId, CourseEnrollmentState.IN_PROGRESS);
    }
    catch (LMSRepositoryException e)
    {
      throw new UseCaseException("Failed to update course enrollment state with course ID: [" + input.getCourseId() + "]", e);
    }
    return new UpdateEnrollmentOutput(courseEnrollment);
  }
}
