/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.publish_course;

import java.util.List;
import java.util.Set;

import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseOutput;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class PublishCourseOutput
{
  private GetCourseOutput courseOutput;
  private List<String> enrolledLearnerIds;
  private Set<String> groupAdmins;

  public PublishCourseOutput(GetCourseOutput courseOutput, List<String> enrolledLearnerIds, Set<String> groupAdmins)
  {
    this.courseOutput = courseOutput;
    this.enrolledLearnerIds = enrolledLearnerIds;
    this.groupAdmins = groupAdmins;
  }

  public GetCourseOutput getCourseOutput()
  {
    return courseOutput;
  }

  public List<String> getEnrolledLearnerIds()
  {
    return enrolledLearnerIds;
  }

  public Set<String> getGroupAdmins()
  {
    return groupAdmins;
  }
}
