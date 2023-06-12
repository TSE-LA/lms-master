/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.service;

import java.util.List;

import mn.erin.lms.legacy.domain.lms.model.content.CourseContentId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseAnalyticData;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface CourseAnalytics
{
  List<CourseAnalyticData> generateCourseAnalytics(CourseContentId courseContentId);
}
