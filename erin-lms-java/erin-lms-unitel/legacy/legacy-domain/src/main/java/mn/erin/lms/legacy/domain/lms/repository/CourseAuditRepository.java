package mn.erin.lms.legacy.domain.lms.repository;

import java.util.List;

import mn.erin.lms.legacy.domain.lms.model.audit.CourseAudit;
import mn.erin.lms.legacy.domain.lms.model.audit.CourseAuditId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface  CourseAuditRepository
{
  CourseAudit create(CourseId courseId, LearnerId learnerId);

  List<CourseAudit> listAll(LearnerId learnerId);

  List<CourseAudit> listAll();

  boolean isExist(CourseId courseId, LearnerId learnerId);

  boolean delete(CourseAuditId id);

  boolean delete(CourseId courseId, LearnerId learnerId);

  void delete(CourseId courseId);
}
