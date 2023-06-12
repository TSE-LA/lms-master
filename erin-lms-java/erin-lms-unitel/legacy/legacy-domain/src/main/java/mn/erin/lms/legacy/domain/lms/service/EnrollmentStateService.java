/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.service;

import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollmentState;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface EnrollmentStateService
{
  CourseEnrollmentState getCourseEnrollmentState(CourseId courseId);
}
