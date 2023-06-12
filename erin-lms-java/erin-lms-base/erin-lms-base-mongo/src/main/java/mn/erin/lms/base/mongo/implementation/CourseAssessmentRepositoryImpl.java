package mn.erin.lms.base.mongo.implementation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.lms.base.domain.model.assessment.CourseAssessment;
import mn.erin.lms.base.domain.model.assessment.QuizId;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.CourseAssessmentRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.mongo.document.assessment.MongoCourseAssessment;
import mn.erin.lms.base.mongo.repository.MongoCourseAssessmentRepository;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseAssessmentRepositoryImpl implements CourseAssessmentRepository
{
  private final MongoCourseAssessmentRepository mongoCourseAssessmentRepository;

  public CourseAssessmentRepositoryImpl(MongoCourseAssessmentRepository mongoCourseAssessmentRepository)
  {
    this.mongoCourseAssessmentRepository = mongoCourseAssessmentRepository;
  }

  @Override
  public CourseAssessment create(CourseId courseId, Collection<QuizId> quizzes)
  {
    return save(courseId, quizzes);
  }

  @Override
  public CourseAssessment update(CourseId courseId, Collection<QuizId> quizzes)
  {
    return save(courseId, quizzes);
  }

  @Override
  public CourseAssessment fetchById(CourseId courseId) throws LmsRepositoryException
  {
    Optional<MongoCourseAssessment> mongoCourseAssessment = mongoCourseAssessmentRepository.findById(courseId.getId());

    if (!mongoCourseAssessment.isPresent())
    {
      throw new LmsRepositoryException("Assessment does not exist for the course: [" + courseId.getId() + "]");
    }

    return getAssessment(mongoCourseAssessment.get());
  }

  @Override
  public boolean delete(CourseId courseId)
  {
    mongoCourseAssessmentRepository.deleteById(courseId.getId());
    return true;
  }

  private CourseAssessment save(CourseId courseId, Collection<QuizId> quizzes)
  {
    MongoCourseAssessment mongoCourseAssessment = new MongoCourseAssessment(courseId.getId(), map(quizzes));
    mongoCourseAssessmentRepository.save(mongoCourseAssessment);
    return new CourseAssessment(courseId, new HashSet<>(quizzes));
  }

  private Set<String> map(Collection<QuizId> quizzes)
  {
    return quizzes.stream().map(QuizId::getId).collect(Collectors.toSet());
  }

  private CourseAssessment getAssessment(MongoCourseAssessment mongoCourseAssessment)
  {
    Set<QuizId> quizzes = mongoCourseAssessment.getQuizIdList().stream().map(QuizId::valueOf)
        .collect(Collectors.toSet());
    return new CourseAssessment(CourseId.valueOf(mongoCourseAssessment.getId()), quizzes);
  }
}
