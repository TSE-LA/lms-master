package mn.erin.lms.base.domain.repository;

import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.classroom_course.LearnerCourseAssessment;

/**
 * @author Erdenetulga
 */
public interface LearnerCourseAssessmentRepository
{
  void save(LearnerCourseAssessment learnerCourseAssessment);

  LearnerCourseAssessment fetchByCourseIdAndLearnerId(CourseId courseId, LearnerId learnerId) throws LmsRepositoryException;

}
