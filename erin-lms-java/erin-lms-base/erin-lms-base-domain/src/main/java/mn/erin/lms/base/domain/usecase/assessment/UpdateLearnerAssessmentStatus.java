package mn.erin.lms.base.domain.usecase.assessment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.Validate;

import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.assessment.dto.LearnerAssessmentStatusInput;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.base.domain.model.classroom_course.LearnerCourseAssessment;
import mn.erin.lms.base.domain.repository.LearnerCourseAssessmentRepository;

/**
 * @author Erdenetulga
 */
public class UpdateLearnerAssessmentStatus extends CourseUseCase<LearnerAssessmentStatusInput, Void>
{
  private LearnerCourseAssessmentRepository learnerCourseAssessmentRepository;

  public UpdateLearnerAssessmentStatus(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry, LearnerCourseAssessmentRepository learnerCourseAssessmentRepository)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.learnerCourseAssessmentRepository = learnerCourseAssessmentRepository;
  }

  @Override
  public Void execute(LearnerAssessmentStatusInput input)
  {
    Validate.notNull(input);
    LocalDate localDate = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    String sentAssessmentDate = localDate.format(formatter);
    LearnerId learnerId = LearnerId.valueOf(input.getLearnerId());
    CourseId courseId = CourseId.valueOf(input.getCourseId());
    LearnerCourseAssessment learnerCourseAssessment = new LearnerCourseAssessment(learnerId, courseId, sentAssessmentDate, true);
    learnerCourseAssessmentRepository.save(learnerCourseAssessment);
    return null;
  }
}
