/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.model.assessment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.base.model.Entity;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;

public class Assessment implements Entity<Assessment>
{
  private final AssessmentId id;
  private final CourseId courseId;
  private final List<TestId> courseTests = new ArrayList<>();

  public Assessment(AssessmentId id, CourseId courseId)
  {
    this.id = Objects.requireNonNull(id, "Assessment cannot have null ID!");
    this.courseId = Objects.requireNonNull(courseId, "Course id cannot have null ID!");
  }

  public AssessmentId getId()
  {
    return id;
  }

  public List<TestId> getCourseTests()
  {
    return courseTests;
  }

  public void addCourseTest(TestId courseTest)
  {
    this.courseTests.add(courseTest);
  }

  public CourseId getCourseId()
  {
    return courseId;
  }

  @Override
  public boolean sameIdentityAs(Assessment other)
  {
    return other != null && (this.id.equals(other.id));
  }
}
