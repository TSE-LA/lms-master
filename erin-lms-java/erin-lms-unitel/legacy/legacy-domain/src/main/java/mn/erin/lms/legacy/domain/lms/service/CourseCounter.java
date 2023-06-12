/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.service;

import java.util.Map;
import java.util.Set;

import mn.erin.lms.legacy.domain.lms.model.course.AuthorId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategoryId;
import mn.erin.lms.legacy.domain.lms.model.course.PublishStatus;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface CourseCounter
{
  Integer countCreatedCourses(CourseCategoryId courseCategoryId, PublishStatus publishStatus, Map<String, Object> properties, Set<String> groupIds);

  Integer countCreatedCourses(CourseCategoryId courseCategoryId, PublishStatus publishStatus, AuthorId authorId, Map<String, Object> properties,
      Set<String> groupIds);
}
