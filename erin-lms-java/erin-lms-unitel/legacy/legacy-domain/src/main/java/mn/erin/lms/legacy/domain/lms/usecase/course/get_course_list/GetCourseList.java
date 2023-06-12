/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.get_course_list;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategoryId;
import mn.erin.lms.legacy.domain.lms.model.course.PublishStatus;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.usecase.course.CourseUseCase;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseOutput;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class GetCourseList extends CourseUseCase<GetCourseListInput, List<GetCourseOutput>>
{
  public GetCourseList(CourseRepository courseRepository)
  {
    super(courseRepository);
  }

  @Override
  public List<GetCourseOutput> execute(GetCourseListInput input)
  {
    Validate.notNull(input, "Input is required to get course list!");

    CourseCategoryId courseCategoryId = new CourseCategoryId(input.getCategoryId());

    List<Course> courseList;
    String status = input.getPublishStatus();
    Map<String, Object> properties = input.getProperties();

    if (StringUtils.isBlank(status))
    {
      if (properties.isEmpty())
      {
        courseList = courseRepository.getCourseList(courseCategoryId);
      }
      else
      {
        courseList = courseRepository.getCourseList(courseCategoryId, properties);
      }
      return this.createOutput(courseList);
    }
    else
    {
      PublishStatus publishStatus = PublishStatus.valueOf(status.toUpperCase());
      if (properties.isEmpty())
      {
        courseList = courseRepository.getCourseList(courseCategoryId, publishStatus);
      }
      else
      {
        courseList = courseRepository.getCourseList(courseCategoryId, publishStatus, properties);
      }
    }

    return this.createOutput(courseList);
  }

  private List<GetCourseOutput> createOutput(List<Course> courseList)
  {
    List<GetCourseOutput> results = new ArrayList<>();

    for (Course course : courseList)
    {
      GetCourseOutput output = getCourseOutput(course);
      results.add(output);
    }

    return results;
  }
}
