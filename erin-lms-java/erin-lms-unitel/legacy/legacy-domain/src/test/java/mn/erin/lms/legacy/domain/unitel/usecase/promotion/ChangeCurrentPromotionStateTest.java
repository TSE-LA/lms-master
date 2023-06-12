/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.unitel.usecase.promotion;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.lms.legacy.domain.lms.model.course.AuthorId;
import mn.erin.lms.legacy.domain.lms.model.course.CompanyId;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategory;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategoryId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseDetail;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.unitel.model.PromotionState;

import static mn.erin.lms.legacy.domain.unitel.PromotionConstants.PROPERTY_END_DATE;
import static mn.erin.lms.legacy.domain.unitel.PromotionConstants.PROPERTY_STATE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

public class ChangeCurrentPromotionStateTest
{
  private static final String COMPANY_ID = "-unitel-";
  private static final String CATEGORY_NAME_TEST = "testCategory";

  private static final String CATEGORY_ID_TEST = "c1";
  private static final String COURSE_ID_TEST = "testCourseId";

  private CourseCategoryRepository courseCategoryRepository;
  private CourseRepository courseRepository;
  private ChangeCurrentPromotionState changeCurrentPromotionState;

  @Before
  public void setUp()
  {
    courseCategoryRepository = Mockito.mock(CourseCategoryRepository.class);
    courseRepository = Mockito.mock(CourseRepository.class);

    changeCurrentPromotionState = new ChangeCurrentPromotionState(courseRepository, courseCategoryRepository);
  }

  @Test
  public void verifyUpdatePromotionState() throws LMSRepositoryException
  {
    Mockito.when(courseCategoryRepository.listAll(new CompanyId(COMPANY_ID))).thenReturn(getCourseCategories());
    Mockito.when(courseRepository.getCourseList(new CourseCategoryId(CATEGORY_ID_TEST), getProperties())).thenReturn(getCourseList());

    CourseDetail courseDetail = getCourseDetail();
    courseDetail.addProperty(PROPERTY_STATE, PromotionState.EXPIRED.getStateName().toUpperCase());

    changeCurrentPromotionState.execute(null);
    Mockito.verify(courseRepository, times(1)).update(any(), (CourseDetail) any());
  }

  private List<Course> getCourseList()
  {
    Course course = new Course(new CourseId(COURSE_ID_TEST), new CourseCategoryId(CATEGORY_ID_TEST), new AuthorId("author"), getCourseDetail());

    return Arrays.asList(course);
  }

  private List<CourseCategory> getCourseCategories()
  {
    CourseCategory courseCategory = new CourseCategory(new CompanyId(COMPANY_ID), new CourseCategoryId("p1"), new CourseCategoryId(CATEGORY_ID_TEST),
        CATEGORY_NAME_TEST);
    return Arrays.asList(courseCategory);
  }

  private CourseDetail getCourseDetail()
  {
    CourseDetail courseDetail = new CourseDetail("test");
    courseDetail.addProperty(PROPERTY_END_DATE, "2020-12-25");

    return courseDetail;
  }

  private Map<String, Object> getProperties()
  {
    Map<String, Object> properties = new HashMap<>();
    properties.put(PROPERTY_STATE, PromotionState.CURRENT.getStateName().toUpperCase());

    return properties;
  }
}
