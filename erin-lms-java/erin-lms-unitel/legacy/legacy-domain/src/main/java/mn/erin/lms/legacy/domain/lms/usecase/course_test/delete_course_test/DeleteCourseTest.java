/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_test.delete_course_test;

import java.util.Objects;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.assessment.TestId;
import mn.erin.lms.legacy.domain.lms.repository.CourseTestRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class DeleteCourseTest implements UseCase<DeleteCourseTestInput, DeleteCourseTestOutput>
{
  private CourseTestRepository courseTestRepository;

  public DeleteCourseTest(CourseTestRepository courseTestRepository)
  {
    this.courseTestRepository = Objects.requireNonNull(courseTestRepository, "Course test repository cannot be null!");
  }

  @Override
  public DeleteCourseTestOutput execute(DeleteCourseTestInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException("Delete course test input cannot be null!");
    }
    boolean deleted;
    try
    {
      deleted = courseTestRepository.delete(new TestId(input.getCourseTestId()));
    }
    catch (LMSRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
    return new DeleteCourseTestOutput(deleted);
  }
}
