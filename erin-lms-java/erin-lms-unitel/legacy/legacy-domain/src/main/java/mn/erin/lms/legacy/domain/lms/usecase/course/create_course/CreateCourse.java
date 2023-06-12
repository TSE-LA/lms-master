/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.create_course;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategory;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategoryId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseDetail;
import mn.erin.lms.legacy.domain.lms.model.course.UserGroup;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.usecase.course_group.CreateCourseGroup;
import mn.erin.lms.legacy.domain.lms.usecase.course_group.CreateCourseGroupInput;

public class CreateCourse implements UseCase<CreateCourseInput, CreateCourseOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateCourse.class);

  private final CourseRepository courseRepository;
  private final CourseCategoryRepository courseCategoryRepository;
  private final CreateCourseGroup createCourseGroup;

  public CreateCourse(CourseRepository courseRepository, CourseCategoryRepository courseCategoryRepository, CreateCourseGroup createCourseGroup)
  {
    this.courseRepository = Objects.requireNonNull(courseRepository, "CourseRepository cannot be null!");
    this.courseCategoryRepository = Objects.requireNonNull(courseCategoryRepository, "CourseCategoryRepository cannot be null!");
    this.createCourseGroup = Objects.requireNonNull(createCourseGroup, "CreateCourseGroup usecase cannot be null!");
  }

  @Override
  public CreateCourseOutput execute(CreateCourseInput input) throws UseCaseException
  {
    Validate.notNull(input, "Input is required to create a course!");

    CourseCategory courseCategory;

    try
    {
      CourseCategoryId id = new CourseCategoryId(input.getCategoryId());
      courseCategory = courseCategoryRepository.getCourseCategory(id);
    }
    catch (LMSRepositoryException e)
    {
      throw new NoSuchElementException("Course category with the ID: [" + input.getCategoryId() + "] does not exist!");
    }

    // A course is not allowed to be created in a category that has subcategories
    if (!courseCategory.canHaveCourse())
    {
      throw new UseCaseException("Course category [" + courseCategory.getName() +
          "] has subcategories, and therefore is not allowed to have a course!");
    }

    try
    {
      CourseDetail courseDetail = getCourseDetail(input);
      Course course = courseRepository.createCourse(courseDetail, courseCategory.getCategoryId(), new UserGroup(input.getUsers(), input.getGroups()));

      createCourseGroup.execute(new CreateCourseGroupInput(course.getCourseId().getId()));

      return new CreateCourseOutput(course.getCourseId().getId());
    }
    catch (LMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private CourseDetail getCourseDetail(CreateCourseInput input)
  {
    LocalDate localDate = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String dateString = localDate.format(formatter);
    LocalDate parsedDate = LocalDate.parse(dateString, formatter);

    Date date = Date.from(parsedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

    CourseDetail courseDetail = new CourseDetail(input.getTitle());
    courseDetail.setDescription(input.getDescription());
    courseDetail.setCreatedDate(date);
    courseDetail.setModifiedDate(new Date());

    for (Map.Entry<String, Object> courseProperty : input.getProperties().entrySet())
    {
      courseDetail.addProperty(courseProperty.getKey(), (Serializable) courseProperty.getValue());
    }

    return courseDetail;
  }
}
