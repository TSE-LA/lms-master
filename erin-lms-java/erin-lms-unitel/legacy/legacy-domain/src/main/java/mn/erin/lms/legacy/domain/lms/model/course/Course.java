/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.model.course;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.Entity;
import mn.erin.lms.legacy.domain.lms.model.content.CourseContentId;

public class Course implements Entity<Course>
{
  private final CourseId id;
  private final CourseCategoryId courseCategoryId;
  private final AuthorId authorId;
  private final CourseDetail courseDetail;
  private UserGroup userGroup = new UserGroup();
  private CourseContentId courseContentId;

  public Course(CourseId id, CourseCategoryId courseCategoryId, AuthorId authorId, CourseDetail courseDetail)
  {
    this.id = Objects.requireNonNull(id, "Course ID cannot be null");
    this.courseDetail = Objects.requireNonNull(courseDetail, "Course detail cannot be null!");
    this.authorId = Objects.requireNonNull(authorId, "Course author cannot be null");
    this.courseCategoryId = Objects.requireNonNull(courseCategoryId, "Course category ID cannot be null!");
  }

  public CourseId getCourseId()
  {
    return id;
  }

  public CourseDetail getCourseDetail()
  {
    return courseDetail;
  }

  public CourseCategoryId getCourseCategoryId()
  {
    return courseCategoryId;
  }

  public AuthorId getAuthorId()
  {
    return authorId;
  }

  @Override
  public boolean sameIdentityAs(Course other)
  {
    return other != null && this.id.equals(other.id);
  }

  public CourseContentId getCourseContentId()
  {
    return courseContentId;
  }

  public void setCourseContentId(CourseContentId courseContentId)
  {
    this.courseContentId = courseContentId;
  }

  public UserGroup getUserGroup()
  {
    return userGroup;
  }

  public void setUserGroup(UserGroup userGroup)
  {
    this.userGroup = Validate.notNull(userGroup, "User Group cannot be null!");
  }
}
