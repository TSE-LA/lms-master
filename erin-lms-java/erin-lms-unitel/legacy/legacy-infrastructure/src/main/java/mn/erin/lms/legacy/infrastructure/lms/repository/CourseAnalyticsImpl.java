/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import mn.erin.domain.aim.model.user.UserId;
import mn.erin.lms.legacy.domain.lms.model.content.CourseContentId;
import mn.erin.lms.legacy.domain.lms.model.course.AuthorId;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseAnalyticData;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategoryId;
import mn.erin.lms.legacy.domain.lms.model.course.PublishStatus;
import mn.erin.lms.legacy.domain.lms.service.CourseAnalytics;
import mn.erin.lms.legacy.domain.lms.service.CourseCounter;
import mn.erin.lms.legacy.infrastructure.lms.repository.mongo.BaseMongoCourseRepository;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseAnalyticsImpl extends BaseMongoCourseRepository
    implements CourseCounter, CourseAnalytics
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CourseAnalyticsImpl.class);

  protected static final String PROPERTY_KEY_START_DATE = "startDate";
  protected static final String PROPERTY_KEY_END_DATE = "endDate";

  public CourseAnalyticsImpl(MongoTemplate mongoTemplate)
  {
    super(mongoTemplate);
  }

  @Override
  public List<CourseAnalyticData> generateCourseAnalytics(CourseContentId courseId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Integer countCreatedCourses(CourseCategoryId courseCategoryId, PublishStatus publishStatus, Map<String, Object> properties, Set<String> groupIds)
  {
    Validate.notNull(courseCategoryId, "Course category ID cannot be null!");
    Validate.notNull(publishStatus, "Publish status cannot be null!");
    UserId currentUsername = UserId.valueOf(accessIdentityManagement.getCurrentUsername());

    Date startDate = (Date) properties.get(PROPERTY_KEY_START_DATE);
    Date endDate = (Date) properties.get(PROPERTY_KEY_END_DATE);

    Map<String, Object> filteredProperties = new HashMap<>(properties);
    filteredProperties.remove(PROPERTY_KEY_START_DATE);
    filteredProperties.remove(PROPERTY_KEY_END_DATE);

    List<Bson> filters = getPropertiesFilters(filteredProperties);
    filters.add(eq(FIELD_COURSE_CATEGORY_ID, courseCategoryId));
    filters.add(eq(FIELD_AUTHOR_ID, currentUsername));
    filters.add(eq(EMBEDDED_FIELD_PUBLISH_STATUS, publishStatus.name()));

    Bson filter = and(filters);
    List<Course> courses = getCourseList(filter);

    return getFilteredCount(startDate, endDate, courses);
  }

  @Override
  public Integer countCreatedCourses(CourseCategoryId courseCategoryId, PublishStatus publishStatus, AuthorId authorId, Map<String, Object> properties,
      Set<String> groupIds)
  {
    throw new UnsupportedOperationException();
  }

  private Integer getFilteredCount(Date startDate, Date endDate, List<Course> courses)
  {
    List<Course> filteredCourses = new ArrayList<>();
    for (Course course : courses)
    {
      Date createdDate = course.getCourseDetail().getCreatedDate();
      if (startDate.getTime() <= createdDate.getTime() && createdDate.getTime() <= endDate.getTime())
      {
        filteredCourses.add(course);
      }
    }

    return filteredCourses.size();
  }
}
