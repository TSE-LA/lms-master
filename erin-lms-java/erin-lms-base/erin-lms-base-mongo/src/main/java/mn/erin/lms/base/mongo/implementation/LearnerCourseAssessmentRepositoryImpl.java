package mn.erin.lms.base.mongo.implementation;

import java.util.Optional;

import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.model.classroom_course.LearnerCourseAssessment;
import mn.erin.lms.base.domain.repository.LearnerCourseAssessmentRepository;
import mn.erin.lms.base.mongo.document.assessment.MongoLearnerCourseAssessment;
import mn.erin.lms.base.mongo.repository.MongoLearnerCourseAssessmentRepository;


/**
 * @author Erdenetulga
 */
public class LearnerCourseAssessmentRepositoryImpl implements LearnerCourseAssessmentRepository
{
  private final MongoLearnerCourseAssessmentRepository mongoLearnerCourseAssessmentRepository;

  public LearnerCourseAssessmentRepositoryImpl(MongoLearnerCourseAssessmentRepository mongoLearnerCourseAssessmentRepository)
  {
    this.mongoLearnerCourseAssessmentRepository = mongoLearnerCourseAssessmentRepository;
  }

  @Override
  public void save(LearnerCourseAssessment learnerCourseAssessment)
  {
    Optional<MongoLearnerCourseAssessment> byCourseIdAndAndLearnerId = mongoLearnerCourseAssessmentRepository
        .findByCourseIdAndAndLearnerId(learnerCourseAssessment.getCourseId().getId(), learnerCourseAssessment.getLearnerId().getId());
    if (!byCourseIdAndAndLearnerId.isPresent())
    {
      MongoLearnerCourseAssessment mongoLearnerCourseAssessment = new MongoLearnerCourseAssessment(learnerCourseAssessment.getCourseId().getId(),
          learnerCourseAssessment.getLearnerId().getId(), learnerCourseAssessment.isSendStatus(), learnerCourseAssessment.getSentDate());
      mongoLearnerCourseAssessmentRepository.save(mongoLearnerCourseAssessment);
    }
  }

  @Override
  public LearnerCourseAssessment fetchByCourseIdAndLearnerId(CourseId courseId, LearnerId learnerId) throws LmsRepositoryException
  {
    Optional<MongoLearnerCourseAssessment> mongoLearnerCourseAssessment = mongoLearnerCourseAssessmentRepository
        .findByCourseIdAndAndLearnerId(courseId.getId(), learnerId.getId());
    if (!mongoLearnerCourseAssessment.isPresent())
    {
      throw new LmsRepositoryException("not found");
    }
    return new LearnerCourseAssessment(
        LearnerId.valueOf(mongoLearnerCourseAssessment.get().getLearnerId()),
        CourseId.valueOf(mongoLearnerCourseAssessment.get().getCourseId()),
        mongoLearnerCourseAssessment.get().getSentDate(),
        mongoLearnerCourseAssessment.get().isSendStatus());
  }
}
