/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.enrollment.create_enrollment;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollment;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollmentState;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class CreateCourseEnrollments implements UseCase<CreateCourseEnrollmentsInput, CreateCourseEnrollmentsOutput>
{
  private final CourseEnrollmentRepository enrollmentRepository;

  public CreateCourseEnrollments(CourseEnrollmentRepository enrollmentRepository)
  {
    this.enrollmentRepository = Objects.requireNonNull(enrollmentRepository, "CourseEnrollmentRepository cannot be null!");
  }

  @Override
  public CreateCourseEnrollmentsOutput execute(CreateCourseEnrollmentsInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException("Create course enrollment input data cannot be null!");
    }
    CourseId courseId = new CourseId(input.getCourseId());
    CreateCourseEnrollmentsOutput output = new CreateCourseEnrollmentsOutput();
    List<CourseEnrollment> courseEnrollmentList = Validate.notNull(enrollmentRepository.getEnrollmentList(courseId), "Course enrollment list cannot be null!");
    Set<String> learnerIds = input.getLearnerIds();
    if (!courseEnrollmentList.isEmpty())
    {
      getNotEnrolledLearners(learnerIds, courseEnrollmentList);
    }
    for (String id : learnerIds)
    {
      LearnerId learnerId = new LearnerId(id);
      output.addEnrollment(this.enrollmentRepository.createEnrollment(courseId, learnerId, CourseEnrollmentState.NEW));
    }
    return output;
  }

  private void getNotEnrolledLearners(Set<String> learnerIds, List<CourseEnrollment> courseEnrollmentList)
  {
    for (CourseEnrollment courseEnrollment : courseEnrollmentList)
    {
      String learnerId = courseEnrollment.getLearnerId().getId();
      learnerIds.remove(learnerId);
    }
  }
}
