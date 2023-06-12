package mn.erin.lms.base.mongo.util;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.model.EntityId;
import mn.erin.lms.base.domain.model.announcement.Announcement;
import mn.erin.lms.base.domain.model.announcement.AnnouncementId;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.content.CourseContentId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.CourseType;
import mn.erin.lms.base.domain.model.course.DateInfo;
import mn.erin.lms.base.domain.model.course.PublishStatus;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.AuthorId;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.service.CourseTypeResolver;
import mn.erin.lms.base.domain.service.UnknownCourseTypeException;
import mn.erin.lms.base.mongo.document.announcement.MongoAnnouncement;
import mn.erin.lms.base.mongo.document.course.MongoCourse;
import mn.erin.lms.base.mongo.document.course.MongoPublishStatus;

/**
 * @author Bat-Erdene Tsogoo.
 */
public final class ObjectMapper
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ObjectMapper.class);

  private ObjectMapper()
  {
  }

  public static MongoCourse mapToMongoCourse(Course course)
  {
    CourseDetail courseDetail = course.getCourseDetail();

    String id = course.getCourseId().getId();
    String categoryId = course.getCourseCategoryId().getId();
    String title = courseDetail.getTitle();
    MongoPublishStatus publishStatus = MongoPublishStatus.valueOf(courseDetail.getPublishStatus().name());
    String authorId = course.getAuthorId().getId();

    MongoCourse mongoCourse;
    if (course.getCourseType() != null)
    {
      mongoCourse = new MongoCourse(id, title, categoryId, course.getCourseType().getType(), publishStatus, authorId);
    }
    else
    {
      mongoCourse = new MongoCourse(id, title, categoryId, publishStatus, authorId);
    }

    mongoCourse.setCourseContentId(course.getCourseContentId() != null ? course.getCourseContentId().getId() : null);

    if (courseDetail.getDateInfo() != null)
    {
      DateInfo dateInfo = courseDetail.getDateInfo();
      mongoCourse.setCreatedDate(dateInfo.getCreatedDate());
      mongoCourse.setModifiedDate(dateInfo.getModifiedDate());
      mongoCourse.setPublishDate(dateInfo.getPublishDate());
    }

    mongoCourse.setDescription(courseDetail.getDescription());
    mongoCourse.setProperties(courseDetail.getProperties());
    mongoCourse.setAssignedLearners(course.getCourseDepartmentRelation().getAssignedLearners().stream()
        .map(EntityId::getId).collect(Collectors.toSet()));
    mongoCourse.setAssignedDepartments(course.getCourseDepartmentRelation().getAssignedDepartments().stream()
        .map(EntityId::getId).collect(Collectors.toSet()));
    mongoCourse.setCourseDepartment(course.getCourseDepartmentRelation().getCourseDepartment().getId());
    mongoCourse.setHasFeedbackOption(course.getCourseDetail().hasFeedbackOption());
    mongoCourse.setHasAssessment(course.getCourseDetail().hasAssessment());
    mongoCourse.setHasQuiz(course.getCourseDetail().hasQuiz());
    mongoCourse.setThumbnailUrl(courseDetail.getThumbnailUrl());

    return mongoCourse;
  }

  public static Course mapToCourse(MongoCourse mongoCourse, CourseTypeResolver courseTypeResolver)
  {
    CourseId courseId = CourseId.valueOf(mongoCourse.getId());
    AuthorId authorId = AuthorId.valueOf(mongoCourse.getAuthorId());
    CourseCategoryId courseCategoryId = CourseCategoryId.valueOf(mongoCourse.getCategoryId());
    String courseCategoryName = mongoCourse.getCategoryName();
    String assessmentId = mongoCourse.getAssessmentId();
    String certificateId = mongoCourse.getCertificateId();
    CourseDetail courseDetail = new CourseDetail(mongoCourse.getTitle());
    courseDetail.setDescription(mongoCourse.getDescription());
    courseDetail.setHasQuiz(mongoCourse.hasQuiz());
    courseDetail.setHasFeedbackOption(mongoCourse.hasFeedbackOption());
    courseDetail.setHasAssessment(mongoCourse.isHasAssessment());
    courseDetail.setHasCertificate(mongoCourse.isHasCertificate());
    courseDetail.addProperties(mongoCourse.getProperties());
    courseDetail.changePublishStatus(PublishStatus.valueOf(mongoCourse.getPublishStatus().name()));
    courseDetail.setThumbnailUrl(mongoCourse.getThumbnailUrl());
    courseDetail.setCredit(mongoCourse.getCredit());
    courseDetail.setCertifiedNumber(mongoCourse.getCertifiedNumber());

    DateInfo dateInfo = new DateInfo();
    dateInfo.setCreatedDate(mongoCourse.getCreatedDate());
    dateInfo.setModifiedDate(mongoCourse.getModifiedDate());
    dateInfo.setPublishDate(mongoCourse.getPublishDate());

    courseDetail.setDateInfo(dateInfo);

    CourseDepartmentRelation courseDepartmentRelation = new CourseDepartmentRelation(DepartmentId.valueOf(mongoCourse.getCourseDepartment()));
    Set<DepartmentId> assignedDepartments = mongoCourse.getAssignedDepartments().stream().map(DepartmentId::valueOf)
        .collect(Collectors.toSet());
    //    Set<LearnerId> assignedLearners = mongoCourse.getAssignedLearners().stream().
    courseDepartmentRelation.setAssignedDepartments(assignedDepartments);
    courseDepartmentRelation.setAssignedLearners(mongoCourse.getAssignedLearners().stream().map(LearnerId::valueOf).collect(Collectors.toSet()));

    CourseType courseType = null;
    if (!StringUtils.isBlank(mongoCourse.getCourseType()))
    {
      try
      {
        courseType = courseTypeResolver.resolve(mongoCourse.getCourseType());
      }
      catch (UnknownCourseTypeException e)
      {
        LOGGER.error(e.getMessage(), e);
      }
    }

    Course course = new Course(courseId, courseCategoryId, courseDetail, authorId, courseDepartmentRelation);
    course.setCourseCategoryName(courseCategoryName);
    course.setCourseType(courseType);
    course.setAssessmentId(assessmentId);
    course.setCertificateId(certificateId);
    course.setCourseContentId(mongoCourse.getCourseContentId() != null ? CourseContentId.valueOf(mongoCourse.getCourseContentId()) : null);

    return course;
  }
}
