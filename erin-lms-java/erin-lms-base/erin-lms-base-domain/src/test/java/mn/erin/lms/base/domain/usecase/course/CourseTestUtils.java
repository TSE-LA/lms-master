package mn.erin.lms.base.domain.usecase.course;

import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import mn.erin.domain.base.model.EntityId;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.AuthorId;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.CourseType;
import mn.erin.lms.base.domain.model.online_course.EmployeeType;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;

/**
 * @author Galsan Bayart.
 */
public class CourseTestUtils
{

  public static CourseDto generateCourseDto(Course course, CourseDetail courseDetail)
  {
    return new CourseDto.Builder(course.getCourseId().getId())
        .ofCategory(course.getCourseCategoryId().getId())
        .ofCategoryName("CategoryName")
        .ofType(course.getCourseType() != null ? course.getCourseType().getType() : null)
        .withTitle(course.getCourseDetail().getTitle())
        .withAssignedDepartments(course.getCourseDepartmentRelation().getAssignedDepartments().stream().map(EntityId::getId).collect(Collectors.toSet()))
        .withAssignedLearners(course.getCourseDepartmentRelation().getAssignedLearners().stream().map(LearnerId::getId).collect(Collectors.toSet()))
        .withAuthor(course.getAuthorId().getId())
        .withContent(course.getCourseContentId() != null ? course.getCourseContentId().getId() : null)
        .withDescription(courseDetail.getDescription())
        .withPublishStatus(courseDetail.getPublishStatus().name())
        .withHasQuiz(courseDetail.hasQuiz())
        .withHasFeedback(courseDetail.hasFeedbackOption())
        .withHasAssessment(courseDetail.hasAssessment())
        .withSuggestedLearners(Collections.emptySet())
        .withHasCertificate(courseDetail.hasCertificate())
        .withAssessmentId(course.getAssessmentId())
        .withCertificateId(course.getCertificateId())
        .withProperties(courseDetail.getProperties())
        .withThumbnailUrl(courseDetail.getThumbnailUrl())
        .createdAt(Date.from(courseDetail.getDateInfo().getCreatedDate().atZone(ZoneId.systemDefault()).toInstant()))
        .modifiedAt(Date.from(courseDetail.getDateInfo().getModifiedDate().atZone(ZoneId.systemDefault()).toInstant()))
        .scheduledAt(courseDetail.getDateInfo().getPublishDate() != null ?
            Date.from(courseDetail.getDateInfo().getPublishDate().atZone(ZoneId.systemDefault()).toInstant()) : null)
        .belongsTo(course.getCourseDepartmentRelation().getCourseDepartment().getId())
        .belongsToName(course.getCourseDetail().getProperties().get("belongingDepartmentName"))
        .withEnrollmentCount(
            Integer.parseInt(courseDetail.getProperties().containsKey("enrollmentCount") ? courseDetail.getProperties().get("enrollmentCount") : String.valueOf(0)))
        .build();
  }

  public static boolean isEqual(CourseDto expectedDto, CourseDto courseDto)
  {
    if (!expectedDto.getId().equals(courseDto.getId()))
      return false;
    if (!expectedDto.getCourseCategoryId().equals(courseDto.getCourseCategoryId()))
      return false;
    if (!expectedDto.getCourseCategoryName().equals(courseDto.getCourseCategoryName()))
      return false;
    if (expectedDto.getCourseContentId() != null && courseDto.getCourseContentId() != null)
      if (!expectedDto.getCourseContentId().equals(courseDto.getCourseContentId()))
        return false;
    if (expectedDto.getType() != null && courseDto.getType() != null)
      if (!expectedDto.getType().equals(courseDto.getType()))
        return false;
    if (!expectedDto.getAuthorId().equals(courseDto.getAuthorId()))
      return false;
    if (!expectedDto.getTitle().equals(courseDto.getTitle()))
      return false;
    if (expectedDto.getDescription() != null && courseDto.getDescription() != null)
      if (!expectedDto.getDescription().equals(courseDto.getDescription()))
        return false;
    if (expectedDto.getThumbnailUrl() != null && courseDto.getThumbnailUrl() != null)
      if (!expectedDto.getThumbnailUrl().equals(courseDto.getThumbnailUrl()))
        return false;
    if (!(expectedDto.getModulesCount() == courseDto.getModulesCount()))
      return false;
    if (expectedDto.getAssessmentId() != null && courseDto.getAssessmentId() != null)
      if (!expectedDto.getAssessmentId().equals(courseDto.getAssessmentId()))
        return false;
    if (expectedDto.getAssessmentId() != null && courseDto.getAssessmentId() != null)
      if (!expectedDto.getCertificateId().equals(courseDto.getCertificateId()))
        return false;
    if (expectedDto.getProgress() != null && courseDto.getProgress() != null)
      if (!expectedDto.getProgress().equals(courseDto.getProgress()))
        return false;
    if (!expectedDto.getCreatedDate().equals(courseDto.getCreatedDate()))
      return false;
    if (!expectedDto.getModifiedDate().equals(courseDto.getModifiedDate()))
      return false;
    if (!expectedDto.getPublishDate().equals(courseDto.getPublishDate()))
      return false;
    if (!expectedDto.getBelongingDepartmentId().equals(courseDto.getBelongingDepartmentId()))
      return false;
    if (expectedDto.getBelongingDepartmentName() != null && courseDto.getBelongingDepartmentName() != null)
      if (!expectedDto.getBelongingDepartmentName().equals(courseDto.getBelongingDepartmentName()))
        return false;
    if (!(expectedDto.isHasQuiz() == courseDto.isHasQuiz()))
      return false;
    if (!(expectedDto.isHasFeedback() == courseDto.isHasFeedback()))
      return false;
    if (!(expectedDto.isHasAssessment() == courseDto.isHasAssessment()))
      return false;
    if (!(expectedDto.isHasCertificate() == courseDto.isHasCertificate()))
      return false;
    if (!(expectedDto.getEnrollmentCount() == courseDto.getEnrollmentCount()))
    {
      return false;
    }

    AtomicBoolean flag = new AtomicBoolean(true);
    if (expectedDto.getProperties().size() != 0 && courseDto.getProperties().size() != 0)
      courseDto.getProperties().forEach((key, value) -> {
        if (value!= null && !expectedDto.getProperties().get(key).equals(value))
          flag.set(false);
      });
    if (!flag.get())
      return false;

    if (expectedDto.getAssignedDepartments().size() != courseDto.getAssignedDepartments().size())
      return false;

    for (String assignedDepartment : courseDto.getAssignedDepartments())
    {
      if (!expectedDto.getAssignedDepartments().contains(assignedDepartment))
        flag.set(false);
    }
    if (!flag.get())
      return false;

    if (expectedDto.getAssignedLearners().size() != courseDto.getAssignedLearners().size()){
      return false;
    }
    for (String assignedLearners : courseDto.getAssignedLearners())
    {
      if (!expectedDto.getAssignedLearners().contains(assignedLearners))
        flag.set(false);
    }
    if (!flag.get())
      return false;

    if(expectedDto.getSuggestedLearners() != null && courseDto.getSuggestedLearners() != null){
      if (expectedDto.getSuggestedLearners().size() != courseDto.getSuggestedLearners().size())
        return false;
      for (String suggetstedLearners : courseDto.getSuggestedLearners())
      {
        if (!expectedDto.getSuggestedLearners().contains(suggetstedLearners))
          flag.set(false);
      }
    }
    return flag.get();
  }

  public static Course generateCourse()
  {
    CourseId courseId = CourseId.valueOf("courseId");
    CourseCategoryId courseCategoryId = CourseCategoryId.valueOf("category");
    CourseDetail courseDetail = new CourseDetail("title");
    AuthorId authorId = AuthorId.valueOf("author");
    CourseDepartmentRelation departmentRelation = new CourseDepartmentRelation(DepartmentId.valueOf("rootDepartment"));
    CourseType courseType = new EmployeeType();
    Course course = new Course(courseId, courseCategoryId, courseDetail, authorId, departmentRelation);
    course.setCourseType(courseType);
    course.setAssessmentId("assessmentId");
    course.setCertificateId("certificateId");
    return course;
  }
}
