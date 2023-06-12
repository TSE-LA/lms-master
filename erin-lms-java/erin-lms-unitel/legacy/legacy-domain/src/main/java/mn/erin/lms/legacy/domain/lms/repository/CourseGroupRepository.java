package mn.erin.lms.legacy.domain.lms.repository;

import java.util.List;

import mn.erin.lms.legacy.domain.lms.model.course.CourseGroup;
import mn.erin.lms.legacy.domain.lms.model.course.CourseGroupId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;

/**
 * @author Zorig
 */
public interface CourseGroupRepository
{
  CourseGroup create(CourseId courseId, String groupId);

  List<CourseGroup> listGroupCourses(String groupId);

  List<CourseGroup> listGroups(String courseId);

  boolean delete(CourseGroupId courseGroupId);

  void delete(CourseId courseId);
}
