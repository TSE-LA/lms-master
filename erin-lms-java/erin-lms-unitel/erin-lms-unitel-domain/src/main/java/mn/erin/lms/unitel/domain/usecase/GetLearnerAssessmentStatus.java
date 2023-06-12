package mn.erin.lms.unitel.domain.usecase;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.classroom_course.LearnerCourseAssessment;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.LearnerCourseAssessmentRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.unitel.domain.usecase.dto.LearnerAssessmentStatusInput;

/**
 * @author Erdenetulga
 */
public class GetLearnerAssessmentStatus extends CourseUseCase<LearnerAssessmentStatusInput, LearnerCourseAssessment>
{
  private LearnerCourseAssessmentRepository learnerCourseAssessmentRepository;

  public GetLearnerAssessmentStatus(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry, LearnerCourseAssessmentRepository learnerCourseAssessmentRepository)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.learnerCourseAssessmentRepository = learnerCourseAssessmentRepository;
  }

  @Override
  public LearnerCourseAssessment execute(LearnerAssessmentStatusInput input) throws UseCaseException
  {
    Validate.notNull(input);
    LearnerId learnerId = LearnerId.valueOf(input.getLearnerId());
    CourseId courseId = CourseId.valueOf(input.getCourseId());

    try
    {
      return learnerCourseAssessmentRepository.fetchByCourseIdAndLearnerId(courseId, learnerId);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}
