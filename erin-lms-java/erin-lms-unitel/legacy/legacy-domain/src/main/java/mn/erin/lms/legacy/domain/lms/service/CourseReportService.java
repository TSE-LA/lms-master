/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.service;

import java.util.List;
import java.util.Map;

import mn.erin.lms.legacy.domain.lms.model.report.CourseReport;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface CourseReportService
{
  List<CourseReport> generateCourseReports(String groupId);

  List<CourseReport> generateCourseReports(String groupId, Map<String, Object> courseProperties);

  List<CourseReport> generateCourseReports(String groupId, String categoryName, Map<String, Object> courseProperties);
}
