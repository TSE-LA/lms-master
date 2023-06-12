package mn.erin.lms.base.domain.model.course;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import mn.erin.domain.base.model.Entity;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.content.CourseContentId;
import mn.erin.lms.base.aim.user.AuthorId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class Course implements Entity<Course>, Comparable<Course>
{
  // Mandatory fields. A course contains an identifier, a detail information and an author who created the course.
  private final CourseId courseId;
  private final CourseDetail courseDetail;
  private final AuthorId authorId;
  private final CourseDepartmentRelation courseDepartmentRelation;
  private final CourseCategoryId courseCategoryId;

  private CourseContentId courseContentId;
  private CourseType courseType;
  private String assessmentId;
  private String certificateId;
  private String courseCategoryName;

  public Course(CourseId courseId, CourseCategoryId courseCategoryId, CourseDetail courseDetail,
      AuthorId authorId, CourseDepartmentRelation courseDepartmentRelation)
  {
    this.courseId = Objects.requireNonNull(courseId, "Course ID cannot be null!");
    this.courseDetail = Objects.requireNonNull(courseDetail, "Course detail cannot be null!");
    this.courseCategoryId = Objects.requireNonNull(courseCategoryId, "Category ID cannot be null!");
    this.authorId = Objects.requireNonNull(authorId, "Author ID cannot be null!");
    this.courseDepartmentRelation = Objects.requireNonNull(courseDepartmentRelation, "Course department relation cannot be null!");
  }

  public void setCourseType(CourseType courseType)
  {
    this.courseType = courseType;
  }

  public CourseType getCourseType()
  {
    return courseType;
  }

  public CourseDepartmentRelation getCourseDepartmentRelation()
  {
    return courseDepartmentRelation;
  }

  public void setCourseContentId(CourseContentId courseContentId)
  {
    this.courseContentId = courseContentId;
  }

  public CourseId getCourseId()
  {
    return courseId;
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

  public CourseContentId getCourseContentId()
  {
    return courseContentId;
  }

  public String getAssessmentId()
  {
    return assessmentId;
  }

  public void setAssessmentId(String assessmentId)
  {
    this.assessmentId = assessmentId;
  }

  public String getCertificateId()
  {
    return certificateId;
  }

  public void setCertificateId(String certificateId)
  {
    this.certificateId = certificateId;
  }

  @Override
  public boolean sameIdentityAs(Course other)
  {
    return this.courseId.equals(other.courseId);
  }

  public String getCourseCategoryName()
  {
    return courseCategoryName;
  }

  public void setCourseCategoryName(String courseCategoryName)
  {
    this.courseCategoryName = courseCategoryName;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Course course = (Course) o;
    return courseId.equals(course.courseId);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(courseId);
  }

  @Override
  public int compareTo(@NotNull Course other)
  {
    return courseDetail.getDateInfo().getModifiedDate().compareTo(other.getCourseDetail().getDateInfo().getModifiedDate());
  }
}
