/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.get_course_list;

import java.util.List;

import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseOutput;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class GetCourseListOutput
{
  private List<GetCourseOutput> courseList;

  public GetCourseListOutput(List<GetCourseOutput> courseList)
  {
    this.courseList = courseList;
  }

  public List<GetCourseOutput> getList()
  {
    return courseList;
  }
}
