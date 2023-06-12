package mn.erin.lms.unitel.domain.usecase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.Validate;

import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.classroom_course.LearnerCourseAssessment;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.LearnerCourseAssessmentRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.unitel.domain.usecase.dto.LearnerAssessmentStatusInput;

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
    TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    df.setTimeZone(tz);
    String sentAssessmentDate = df.format(new Date());
    LearnerId learnerId = LearnerId.valueOf(input.getLearnerId());
    CourseId courseId = CourseId.valueOf(input.getCourseId());
    LearnerCourseAssessment learnerCourseAssessment = new LearnerCourseAssessment(learnerId, courseId, sentAssessmentDate, true);
    learnerCourseAssessmentRepository.save(learnerCourseAssessment);
    return null;
  }
}
